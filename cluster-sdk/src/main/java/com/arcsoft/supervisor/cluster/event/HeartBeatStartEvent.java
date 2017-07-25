package com.arcsoft.supervisor.cluster.event;

/**
 * In order create a heart beat session, the monitor node is required to send
 * HeartBeatStartEvent to the target node first.
 * 
 * @author fjli
 */
public class HeartBeatStartEvent extends NodeEvent {

	private long interval;
	private int port;

	/**
	 * Construct the heart beat start event.
	 * 
	 * @param nodeId - the monitor node id
	 * @param interval - the heart beat interval
	 * @param port - the listening port
	 */
	public HeartBeatStartEvent(String nodeId, long interval, int port) {
		super(nodeId);
		this.interval = interval;
		this.port = port;
	}

	/**
	 * Returns the interval between two heart beat events.
	 */
	public long getInterval() {
		return interval;
	}

	/**
	 * Returns the listening port.
	 */
	public int getPort() {
		return port;
	}

}
