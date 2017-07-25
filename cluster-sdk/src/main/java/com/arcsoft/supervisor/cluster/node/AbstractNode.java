package com.arcsoft.supervisor.cluster.node;


import com.arcsoft.supervisor.cluster.Cluster;

/**
 * This is the abstract node.
 * 
 * @author fjli
 */
public abstract class AbstractNode implements Node {

	protected final Cluster cluster;
	protected NodeDescription desc;

	/**
	 * Construct a new default node.
	 * 
	 * @param cluster the cluster instance
     * @param desc the node detail info
	 */
	public AbstractNode(Cluster cluster, NodeDescription desc) {
		this.cluster = cluster;
		this.desc = desc;
	}

	/**
	 * Returns the cluster this node belongs to.
	 */
	@Override
	public Cluster getCluster() {
		return this.cluster;
	}

	/**
	 * Returns description of this node.
	 */
	@Override
	public NodeDescription getDescription() {
		return this.desc;
	}

}
