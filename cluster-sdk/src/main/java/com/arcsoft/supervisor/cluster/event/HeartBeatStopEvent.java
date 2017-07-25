package com.arcsoft.supervisor.cluster.event;

/**
 * In order stop a heart beat session manually, the monitor node is required to send
 * HeartBeatStopEvent to the target node.
 * 
 * @author fjli
 */
public class HeartBeatStopEvent extends NodeEvent {

	/**
	 * Construct new heart beat stop event.
	 * 
	 * @param id - the monitor node id.
	 */
	public HeartBeatStopEvent(String id) {
		super(id);
	}

}
