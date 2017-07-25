package com.arcsoft.supervisor.service.cluster.impl;


import com.arcsoft.supervisor.cluster.Configuration;
import com.arcsoft.supervisor.cluster.NodeType;

/**
 * Commander configuration.
 * 
 * @author fjli
 */
public class CommanderConfiguration extends Configuration {

	/**
	 * Construct new commander configuration.
	 * 
	 * @param clusterType - the commander type
	 */
	public CommanderConfiguration(int clusterType) {
		setClusterType(clusterType);
		setServerType(NodeType.TYPE_COMMANDER);
	}

}
