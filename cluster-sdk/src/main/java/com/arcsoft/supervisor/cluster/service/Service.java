package com.arcsoft.supervisor.cluster.service;

import com.arcsoft.supervisor.cluster.Cluster;

import java.io.IOException;

/**
 * The service is responsible for receiving events or requests from cluster or nodes.
 * 
 * @author fjli
 */
public abstract class Service {

	protected Cluster cluster;

	/**
	 * Construct new service in the specified cluster.
	 * 
	 * @param cluster - the specified cluster
	 */
	protected Service(Cluster cluster) {
		this.cluster = cluster;
	}

	/**
	 * Returns the cluster this service belongs to.
	 */
	public Cluster getCluster() {
		return cluster;
	}

	/**
	 * Start service.
	 */
	public abstract void start() throws IOException;

	/**
	 * Stop service.
	 */
	public abstract void stop();

}
