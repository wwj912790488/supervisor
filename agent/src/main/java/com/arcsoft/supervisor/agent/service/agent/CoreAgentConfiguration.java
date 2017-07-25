package com.arcsoft.supervisor.agent.service.agent;

import com.arcsoft.supervisor.cluster.ClusterType;
import com.arcsoft.supervisor.cluster.NodeType;

/**
 * Core agent configuration.
 * 
 * @author fjli
 */
public class CoreAgentConfiguration extends AgentConfiguration {

	/**
	 * Construct live agent configuration.
	 */
	public CoreAgentConfiguration() {
		setClusterType(ClusterType.CORE);
		setServerType(NodeType.TYPE_CORE);
	}

}
