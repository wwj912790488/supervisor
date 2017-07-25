package com.arcsoft.supervisor.cluster.node;


import com.arcsoft.supervisor.cluster.Cluster;

/**
 * This class represents a node in the cluster. Usually it is a server.
 * 
 * @author fjli
 */
public interface Node {

	/**
	 * This field is used to search event.
	 */
	public static final int TYPE_SEARCH_ALL = 0;

	/**
	 * Indicate this node is a default node.
	 */
	public static final int TYPE_DEFAULT = 1;

	/**
	 * Returns the cluster this node belongs to.
	 */
	public Cluster getCluster();

	/**
	 * Returns description of this node.
	 */
	public NodeDescription getDescription();

}
