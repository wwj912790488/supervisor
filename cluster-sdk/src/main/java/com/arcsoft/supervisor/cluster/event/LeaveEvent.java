package com.arcsoft.supervisor.cluster.event;

/**
 * This event will be generated when node leave from the cluster.
 * 
 * @author fjli
 */
public class LeaveEvent extends NodeEvent {

	/**
	 * Construct a new leave event.
	 * 
	 * @param nodeId - the node id
	 */
	public LeaveEvent(String nodeId) {
		super(nodeId);
	}

}
