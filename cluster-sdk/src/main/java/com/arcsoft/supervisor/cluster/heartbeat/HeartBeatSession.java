package com.arcsoft.supervisor.cluster.heartbeat;

import com.arcsoft.supervisor.cluster.converter.ConversionService;
import com.arcsoft.supervisor.cluster.event.HeartBeatEvent;
import com.arcsoft.supervisor.cluster.event.HeartBeatStartEvent;
import com.arcsoft.supervisor.cluster.event.HeartBeatStopEvent;
import com.arcsoft.supervisor.cluster.net.ConnectOptions;
import com.arcsoft.supervisor.cluster.net.DataPackage;
import com.arcsoft.supervisor.cluster.net.MulticastClient;
import com.arcsoft.supervisor.cluster.node.LocalNode;
import com.arcsoft.supervisor.cluster.node.Node;
import com.arcsoft.supervisor.utils.NamedThreadFactory;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Beat heart session. If the start method is called, the session will send a
 * HeartBeatStartEvent to the monitor target in TCP, and wait the heart beat
 * events. The target will send the heart beat events in UDP with the specified
 * interval. If this session cannot receive the event within the timeout time.
 * Then the HeartBeatSessionTimeoutEvent will be notified to the listeners.
 * 
 * @author fjli
 */
public class HeartBeatSession {

	private static Logger log = Logger.getLogger(HeartBeatSession.class);
	private LocalNode localNode;
	private Node target;
	private long interval = 5000;
	private long timeout = 15000;
	private int port = 0;
	private int userPort = 0;
	private List<HeartBeatSessionListener> listeners = new ArrayList<>();
	private MulticastClient server;
	private Thread thread;
	private ScheduledExecutorService executor;
	private ConnectOptions connectOptions;
	private Object eventLock = new Object();
	private boolean eventReceived;
	private boolean sessionTimeout;

	/**
	 * Create new heart beat session to monitor the specified target node.
	 * 
	 * @param local - 
	 * @param target - the specified target node.
	 */
	public HeartBeatSession(LocalNode local, Node target) {
		this.localNode = local;
		this.target = target;
	}

	/**
	 * Returns the monitor target.
	 */
	public Node getTarget() {
		return target;
	}

	/**
	 * Set the heart beat listen port.
	 * 
	 * @param port - the listen port.
	 */
	public void setPort(int port) {
		this.userPort = port;
	}

	/**
	 * Return the heart beat port.
	 */
	public int getPort() {
		return this.userPort != 0 ? this.userPort : this.port;
	}

	/**
	 * Set internal between two heart beat events.
	 * 
	 * @param interval - interval in milliseconds
	 */
	public void setInterval(long interval) {
		this.interval = interval;
	}

	/**
	 * Returns the heart beat interval.
	 */
	public long getInterval() {
		return interval;
	}

	/**
	 * Set timeout for receiving next heart beat event.
	 * 
	 * @param timeout - timeout in milliseconds
	 */
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	/**
	 * Returns the timeout.
	 */
	public long getTimeout() {
		return timeout;
	}

	/**
	 * Set connection options.
	 * 
	 * @param connectOptions - the connection options.
	 */
	public void setConnectOptions(ConnectOptions connectOptions) {
		this.connectOptions = connectOptions;
	}

	/**
	 * Add heart beat session listener.
	 * 
	 * @param listener - the listener to be added
	 */
	public void addListener(HeartBeatSessionListener listener) {
		synchronized (listeners) {
			if (!listeners.contains(listener))
				listeners.add(listener);
		}
	}

	/**
	 * Remove heart beat session listener.
	 * 
	 * @param listener - the listener to be added
	 */
	public void removeListener(HeartBeatSessionListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}

	/**
	 * Start the session.
	 * 
	 * @throws java.io.IOException - if start failed.
	 */
	public synchronized void start() throws IOException {
		// if already created, do nothing
		if (server != null)
			return;

		eventReceived = false;
		sessionTimeout = false;

		try {
			// create UDP server for receive heart beat events.
			this.port = this.userPort;
			server = new MulticastClient(null);
			server.bind(localNode.getDescription().getIp(), port);

			// send start event to target
			port = server.getLocalPort();
			String nodeId = localNode.getDescription().getId();
			HeartBeatStartEvent event = new HeartBeatStartEvent(nodeId, interval, port);
			localNode.getCluster().sendEvent(event, target, connectOptions);
		} catch(IOException e) {
			log.info("start heartbeat failed", e);
			// if exception occur, close the server.
			if (server != null) {
				server.close();
				server = null;
			}
			// throws exception.
			throw e;
		}

		// create thread to receive heart beat events.
		executor = Executors.newSingleThreadScheduledExecutor(NamedThreadFactory.create("HeartBeatSession"));
		thread = new Thread() {
			public void run() {
				doEventLoop();
			}
		};
		thread.start();
		log.info("Heart beat session started.");
	}

	/**
	 * Stop the session.
	 */
	public synchronized void stop() {
		// if session timeout, the session timeout thread will do stop.
		if (sessionTimeout)
			return;

		// stop this session.
		stopInternal();
	}

	private void stopInternal() {
		// if already stopped, do nothing
		if (server == null)
			return;

		// cancel scheduled executor..
		executor.shutdown();

		// send stop event to the target.
		try {
			String nodeId = localNode.getDescription().getId();
			target.getCluster().sendEvent(new HeartBeatStopEvent(nodeId), target, connectOptions);
		} catch(Exception e) {
		}

		// close the DUP server.
		server.close();
		server = null;

		// wait for thread to exit.
		if (thread.isAlive()) {
			try {
				thread.join();
			} catch (InterruptedException e) {
			}
		}
		log.info("Heart beat session stopped.");
	}

	/**
	 * Wait until one event is received or session is timeout since session started.
	 * 
	 * @param maxWaitingTime - the max waiting time
	 * @return false if session timeout or if no event received at the specified timeout, otherwise true.
	 * @throws InterruptedException if any thread interrupted the current thread before maxWaitingTime.
	 */
	public boolean waitForEvent(long maxWaitingTime) throws InterruptedException {
		synchronized (eventLock) {
			if (sessionTimeout)
				return false;
			if (eventReceived)
				return true;
			eventLock.wait(maxWaitingTime);
			return !sessionTimeout && eventReceived;
		}
	}

	/**
	 * Notify session event.
	 * 
	 * @param event - the event to be notified.
	 */
	private void notifySessionEvent(HeartBeatSessionEvent event) {
		synchronized (listeners) {
			for (HeartBeatSessionListener listener : listeners) {
				listener.sessionEventReceived(event);
			}
		}
	}

	/**
	 * Process timeout.
	 */
	private synchronized void processTimeout() {
		log.error("Heart beat session timeout.");
		synchronized (eventLock) {
			sessionTimeout = true;
			eventLock.notifyAll();
		}

		// notify time out event.
		notifySessionEvent(new HeartBeatSessionTimeoutEvent(this));

		// stop this session.
		stopInternal();
	}

	/**
	 * Loop for receiving heart beat events.
	 */
	private void doEventLoop() {
		// get remove node id.
		String nodeId = target.getDescription().getId();

		// create time out task.
		ScheduledFuture<?> future = null;
		Runnable timeoutHandler = new Runnable() {
			@Override
			public void run() {
				processTimeout();
			}
		};

		// set the first timeout point
		future = executor.schedule(timeoutHandler, timeout, TimeUnit.MILLISECONDS);

		// event loop.
		while (true) {
			try {
				DataPackage pack = server.receive();
				if (pack == null)
					continue;
				Object object = ConversionService.convert(pack);
				if (!(object instanceof HeartBeatEvent))
					continue;
				HeartBeatEvent event = (HeartBeatEvent) object;
				if (nodeId.equals(event.getNodeId())) {
					// if already timeout, exit thread.
					if (!future.cancel(false))
						break;
					// Indicate there are at least one event is received.
					synchronized (eventLock) {
						eventReceived = true;
						eventLock.notifyAll();
					}
					// set next timeout point
					future = executor.schedule(timeoutHandler, timeout, TimeUnit.MILLISECONDS);
				}
			} catch(IOException e) {
				future.cancel(false);
				executor.shutdown();
				break;
			}
		}
	}

}
