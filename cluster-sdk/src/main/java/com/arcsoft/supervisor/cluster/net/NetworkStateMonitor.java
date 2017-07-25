package com.arcsoft.supervisor.cluster.net;

import com.arcsoft.supervisor.utils.NamedThreadFactory;
import com.arcsoft.supervisor.utils.NetworkHelper;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


/**
 * Network state monitor.
 * 
 * @author fjli
 */
public class NetworkStateMonitor {

	private ScheduledExecutorService scheduledExcutor;
	private boolean isRunning = false;
	private Map<String, NetworkMonitorTask> tasks = new HashMap<>();
	private List<NetworkStateListener> listeners = new ArrayList<>();
	private Logger log = Logger.getLogger(NetworkStateMonitor.class);

	/**
	 * Start monitor.
	 */
	public synchronized void start() {
		if (!isRunning) {
			log.info("start network monitor.");
			scheduledExcutor = Executors.newScheduledThreadPool(1, NamedThreadFactory.create("NetworkStateMonitor"));
			isRunning = true;
			for (NetworkMonitorTask task : tasks.values()) {
				log.info("start monitor: " + task.getEth());
				monitor(task);
			}
		}
	}

	/**
	 * Stop monitor.
	 */
	public synchronized void stop() {
		if (isRunning) {
			log.info("stop network monitor.");
			isRunning = false;
			for (NetworkMonitorTask task : tasks.values()) {
				ScheduledFuture<?> feature = task.getFuture();
				if (feature != null)
					feature.cancel(false);
			}
			tasks.clear();
			if (scheduledExcutor != null)
				scheduledExcutor.shutdown();
		}
	}

	/**
	 * Add state change listener.
	 * 
	 * @param listener - the specified listener
	 */
	public void addListener(NetworkStateListener listener) {
		synchronized (listeners) {
			if (!listeners.contains(listener))
				listeners.add(listener);
		}
	}

	/**
	 * Remove the specified listener.
	 * 
	 * @param listener - the specified listener
	 */
	public void removeListener(NetworkStateListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}

	/**
	 * Notify inactive event to all listeners.
	 * 
	 * @param eth - the event source
	 */
	private void ethStateChanged(String eth, boolean active) {
		if (!listeners.isEmpty()) {
			NetworkStateListener[] listenersCopy = null;
			synchronized (listeners) {
				listenersCopy = listeners.toArray(new NetworkStateListener[0]);
			}
			for (NetworkStateListener listener : listenersCopy) {
				listener.ethStateChanged(eth, active);
			}
		}
	}

	/**
	 * Start monitor the specified network interface.
	 * 
	 * @param eth - the name of the specified network interface
	 */
	public synchronized void startMonitor(final String eth) {
		if (!tasks.containsKey(eth)) {
			NetworkMonitorTask task = new NetworkMonitorTask(eth);
			tasks.put(eth, task);
			log.debug("add monitor: " + eth);
			if (isRunning) {
				log.info("start monitor: " + eth);
				monitor(task);
			}
		}
	}

	/**
	 * Stop monitor the specified network interface.
	 * 
	 * @param eth - the name of the specified network interface
	 */
	public synchronized void stopMonitor(String eth) {
		if (tasks.containsKey(eth)) {
			log.info("stop monitor: " + eth);
			NetworkMonitorTask task = tasks.get(eth);
			ScheduledFuture<?> future = task.getFuture();
			if (future != null)
				future.cancel(false);
			tasks.remove(eth);
		}
	}

	/**
	 * Monitor the network interface state.
	 * 
	 */
	private synchronized void monitor(final NetworkMonitorTask task) {
		if (!isRunning || !tasks.containsValue(task))
			return;
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					boolean active = NetworkHelper.checkEth(task.getEth());
					Boolean oldState = task.getState();
					if (oldState == null || oldState.booleanValue() != active) {
						task.setState(active);
						ethStateChanged(task.getEth(), active);
					}
				} catch (UnsupportedOperationException e) {
					log.error("unsupported operation: checkEth.", e);
					return;
				} catch (Exception e) {
					log.error("check eth state failed.", e);
				}
				monitor(task);
			}
		};
		try {
			ScheduledFuture<?> future = scheduledExcutor.schedule(runnable, 100, TimeUnit.MILLISECONDS);
			task.setFuture(future);
		} catch(Exception e) {
			log.error("schedule monitor task failed.", e);
		}
	}

}
