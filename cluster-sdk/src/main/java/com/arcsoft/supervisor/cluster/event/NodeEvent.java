package com.arcsoft.supervisor.cluster.event;

/**
 * This is a super class for all node events.
 * 
 * @author fjli
 */
public abstract class NodeEvent extends Event {

	private String nodeId;

	/**
	 * Construct new node event with the specified node id.
	 * 
	 * @param id - the specified node id
	 */
	public NodeEvent(String id) {
		this.nodeId = id;
	}

	/**
	 * Returns the node id.
	 */
	public String getNodeId() {
		return nodeId;
	}

}
