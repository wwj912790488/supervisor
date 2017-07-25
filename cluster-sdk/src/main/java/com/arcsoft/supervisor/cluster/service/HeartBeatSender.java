package com.arcsoft.supervisor.cluster.service;

import com.arcsoft.supervisor.cluster.converter.ConversionService;
import com.arcsoft.supervisor.cluster.event.HeartBeatEvent;
import com.arcsoft.supervisor.cluster.net.DataPackage;
import com.arcsoft.supervisor.cluster.net.MulticastClient;
import com.arcsoft.supervisor.cluster.node.LocalNode;
import com.arcsoft.supervisor.utils.NamedThreadFactory;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The heart beat sender is used to send beat heart events to the request.
 * 
 * @author fjli
 */
class HeartBeatSender {

	private MulticastClient client;
	private LocalNode node;
	private long interval;
	private SocketAddress localAddr;
	private SocketAddress destAddr;
	private DataPackage pack;
	private ScheduledExecutorService executor;
	private Logger log = Logger.getLogger(HeartBeatSender.class);
	private final HeartBeatSenderListener heartBeatSenderListener;

	/**
	 * Construct new sender.
	 * 
	 * @param node - local node.
	 */
	public HeartBeatSender(LocalNode node) {
		this(node, null);

	}

	/**
	 * Construct a new sender.
	 *
	 * @param node the local node
	 * @param heartBeatSenderListener the listener for local node
	 */
	public HeartBeatSender(LocalNode node, HeartBeatSenderListener heartBeatSenderListener) {
		this.heartBeatSenderListener = heartBeatSenderListener;
		this.node = node;
	}

	/**
	 * Set local address.
	 * 
	 * @param local - the local address.
	 */
	public void setLocalAddress(SocketAddress local) {
		this.localAddr = local;
	}

	/**
	 * Set remote address.
	 * 
	 * @param dest - the remote address.
	 */
	public void setRemoteAddress(SocketAddress dest) {
		this.destAddr = dest;
	}

	/**
	 * Set the interval between two heart beat events.
	 * 
	 * @param interval - the interval in milliseconds
	 */
	public void setInterval(long interval) {
		this.interval = interval;
	}

	/**
	 * Start sending heart beat events with the specified interval.
	 * 
	 * @throws java.io.IOException - if start failed
	 */
	public synchronized void start() throws IOException {
		// if started, do nothing
		if (client != null)
			return;

		// create UDP client and data for sending events.
		client = new MulticastClient(localAddr);
		String id = node.getDescription().getId();
		pack = ConversionService.convert(new HeartBeatEvent(id));

		// send events with fix rate.
		executor = Executors.newSingleThreadScheduledExecutor(NamedThreadFactory.create("HeartBeatSender"));
		executor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				if (executor.isShutdown())
					return;
				try {
					client.send(destAddr, pack);
				} catch (IOException e) {
					log.error("send heart beat failed", e);
				}
			}
		}, 0, interval, TimeUnit.MILLISECONDS);
		log.info("heart beat sender started.");
        if (heartBeatSenderListener != null) {
            heartBeatSenderListener.onStart();
        }

	}

	/**
	 * Stop sending the heart beat events.
	 */
	public synchronized void stop() {
		if (executor != null) {
			executor.shutdown();
			executor = null;
		}
		if (client != null) {
			client.close();
			client = null;
			log.info("heart beat sender stopped.");
			if (heartBeatSenderListener != null) {
                heartBeatSenderListener.onStop();
            }
		}
	}

}
