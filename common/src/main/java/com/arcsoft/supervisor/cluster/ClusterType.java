package com.arcsoft.supervisor.cluster;

/**
 * Define cluster type.
 * 
 * @author fjli
 */
public interface ClusterType {

	/**
	 * Indicate the cluster is a core cluster.
	 */
	public static final int CORE = 0;

	/**
	 * Indicate the cluster is a live cluster.
	 */
	public static final int LIVE = 1;

}
