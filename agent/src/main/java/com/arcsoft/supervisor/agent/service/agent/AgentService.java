package com.arcsoft.supervisor.agent.service.agent;

/**
 * Agent service.
 * 
 * @author fjli
 */
public interface AgentService {

	/**
	 * Returns the agent.
	 */
	AgentServer getAgent();

    AgentConfiguration getAgentConfiguration();

}
