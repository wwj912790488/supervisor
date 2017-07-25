package com.arcsoft.supervisor.cluster.event;


import com.arcsoft.supervisor.cluster.node.NodeDescription;

/**
 * This event will be generated when a node is joined to the specified cluster.
 * 
 * @author fjli
 */
public class JoinEvent extends Event {

	private NodeDescription desc;

	/**
	 * Construct a new event.
	 * 
	 * @param desc - the description of the node
	 */
	public JoinEvent(NodeDescription desc) {
		this.desc = desc;
	}

	/**
	 * Returns the node description.
	 */
	public NodeDescription getNode() {
		return desc;
	}

}
