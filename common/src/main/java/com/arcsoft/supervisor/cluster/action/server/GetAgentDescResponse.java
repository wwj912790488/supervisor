package com.arcsoft.supervisor.cluster.action.server;

import com.arcsoft.supervisor.cluster.action.BaseResponse;
import com.arcsoft.supervisor.model.vo.server.AgentDesc;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * The response message for get agent description request.
 * 
 * @author fjli
 */
@XmlRootElement
public class GetAgentDescResponse extends BaseResponse {

	private AgentDesc agentDesc;

	/**
	 * Returns agent description.
	 */
	public AgentDesc getAgentDesc() {
		return agentDesc;
	}

	/**
	 * Set agent description.
	 * 
	 * @param agentDesc - the agent description
	 */
	public void setAgentDesc(AgentDesc agentDesc) {
		this.agentDesc = agentDesc;
	}

}
