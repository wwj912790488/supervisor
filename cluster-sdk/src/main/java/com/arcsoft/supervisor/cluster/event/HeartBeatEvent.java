package com.arcsoft.supervisor.cluster.event;

/**
 * Define a heart beat event for checking node still alive or not.
 * 
 * @author fjli
 */
public class HeartBeatEvent extends NodeEvent {

	/**
	 * Construct new heart beat event.
	 * 
	 * @param nodeId - the specified node id
	 */
	public HeartBeatEvent(String nodeId) {
		super(nodeId);
	}

}
