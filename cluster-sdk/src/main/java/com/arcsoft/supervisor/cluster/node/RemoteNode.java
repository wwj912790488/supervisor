package com.arcsoft.supervisor.cluster.node;


import com.arcsoft.supervisor.cluster.Cluster;

/**
 * This class represents a remote node in the cluster.
 * 
 * @author fjli
 */
public class RemoteNode extends AbstractNode {

	/**
	 * Construct new remote node.
	 * 
	 * @param cluster - the specified cluster this node belongs to
	 * @param desc - the description of remote node
	 */
	public RemoteNode(Cluster cluster, NodeDescription desc) {
		super(cluster, desc);
	}

}
