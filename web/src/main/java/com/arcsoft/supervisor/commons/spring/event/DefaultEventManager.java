package com.arcsoft.supervisor.commons.spring.event;

import com.arcsoft.supervisor.commons.spring.SpringUtils;
import com.arcsoft.supervisor.utils.NamedThreadFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.support.ResourceHolderSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Default event manager.
 * 
 * @author fjli
 */
public class DefaultEventManager implements EventManager, ApplicationContextAware, InitializingBean, DisposableBean {

	private Logger log = Logger.getLogger(DefaultEventManager.class);
	private ApplicationContext context;
	private Map<Class<?>, Map<Method, String>> eventMaps = new HashMap<>();
	private ExecutorService executor = Executors.newCachedThreadPool(NamedThreadFactory.create(
			"EventManager"
	));

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		this.context = context;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Map<String, List<Method>> methodMaps = SpringUtils.findAllMethodsWithAnnotation(context, EventReceiver.class);
		for (Entry<String, List<Method>> entity : methodMaps.entrySet()) {
			List<Method> methods = entity.getValue();
			for (Method method : methods) {
				EventReceiver receiver = method.getAnnotation(EventReceiver.class);
				Class<?> eventType = receiver.value();
				Class<?>[] paramTypes = method.getParameterTypes();
				if (paramTypes.length > 1) {
					log.warn("Cannot mapping the event receiver method [" + method.toGenericString() + "]: too many parameters. ");
					continue;
				} else if (paramTypes.length == 1 && !paramTypes[0].isAssignableFrom(eventType)) {
					log.warn("Cannot mapping the event receiver method [" + method.toGenericString() + "]: event type invalid. ");
					continue;
				}
				Map<Method, String> map = eventMaps.get(eventType);
				if (map == null) {
					map = new HashMap<>();
					map.put(method, entity.getKey());
					eventMaps.put(eventType, map);
				} else {
					map.put(method, entity.getKey());
				}
			}
		}
	}

	@Override
	public void destroy() throws Exception {
		executor.shutdownNow(); //TODO: need to improve
	}

	@Override
	public void deliver(Object event) {
		deliverEvent(event);
	}

	@Override
	public void submit(Object event) {
		EventResourceHolder eventHolder = getEventHolder();
		if (eventHolder != null) {
			eventHolder.addEvent(event);
		} else {
			deliverEvent(event);
		}
	}

	/**
	 * Get the event holder to save events.
	 */
	private EventResourceHolder getEventHolder() {
		if (!TransactionSynchronizationManager.isActualTransactionActive())
			return null;
		if (TransactionSynchronizationManager.isCurrentTransactionReadOnly())
            return null;
		EventResourceHolder eventHolder = (EventResourceHolder) TransactionSynchronizationManager.getResource(this);
		if (eventHolder != null)
			return eventHolder;
		if (TransactionSynchronizationManager.isSynchronizationActive()) {
			eventHolder = new EventResourceHolder();
			eventHolder.setSynchronizedWithTransaction(true);
			TransactionSynchronizationManager.registerSynchronization(new ResourceHolderSynchronization<EventResourceHolder, Object>(eventHolder, this) {
				@Override
				protected void processResourceAfterCommit(EventResourceHolder resourceHolder) {
					for (Object event : resourceHolder.getEvents())
						deliverEvent(event);
				}
				@Override
				protected boolean shouldReleaseBeforeCompletion() {
					return false;
				}
			});
			TransactionSynchronizationManager.bindResource(this, eventHolder);
		}
		return eventHolder;
	}

	/**
	 * Deliver the event to the registered event receivers.
	 */
	private void deliverEvent(final Object event) {
		for (Class<?> type : eventMaps.keySet()) {
			if (!type.isInstance(event))
				continue;
			Map<Method, String> map = eventMaps.get(type);
			for (final Method method : map.keySet()) {
				final Object bean = context.getBean(map.get(method));
				EventReceiver receiver = method.getAnnotation(EventReceiver.class);
				final boolean withArgs = method.getParameterTypes().length != 0;
				if (receiver.sync()) {
					try {
						if (withArgs)
							method.invoke(bean, event);
						else
							method.invoke(bean);
					} catch (Exception e) {
						log.error("deliver event failed.", e);
					}
				} else {
					executor.execute(new Runnable() {
						@Override
						public void run() {
							try {
								if (withArgs)
									method.invoke(bean, event);
								else
									method.invoke(bean);
							} catch (Exception e) {
								log.error("deliver event failed.", e);
							}
						}
					});
				}
			}
		}
	}

}
