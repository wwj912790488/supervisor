package com.arcsoft.supervisor.cluster.node;

/**
 * Listener object to receive nodes which query from the specified cluster.
 * 
 * @author fjli
 */
public interface NodeListener {

	/**
	 * Notify when remote node is received.
	 * 
	 * @param node - the node to be notified
	 */
	void nodeReceived(RemoteNode node);

}
