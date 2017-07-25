package com.arcsoft.supervisor.agent.service.agent;

import com.arcsoft.supervisor.cluster.ClusterType;
import com.arcsoft.supervisor.cluster.NodeType;

/**
 * Live agent configuration.
 * 
 * @author fjli
 */
public class LiveAgentConfiguration extends AgentConfiguration {

	/**
	 * Construct live agent configuration.
	 */
	public LiveAgentConfiguration() {
		setClusterType(ClusterType.LIVE);
		setServerType(NodeType.TYPE_LIVE);
	}

}
