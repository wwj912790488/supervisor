package com.arcsoft.supervisor.agent.service.agent;

/**
 * Core agent is a server in the core cluster.
 * 
 * @author fjli
 */
public class CoreAgent extends AgentServer {

	/**
	 * Construct new live agent with the specified configuration.
	 * 
	 * @param config
	 */
	public CoreAgent(CoreAgentConfiguration config) {
		super(config);
	}

}
