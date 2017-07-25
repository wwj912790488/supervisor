package com.arcsoft.supervisor.model.vo.server;

import java.util.Map;

/**
 * The agent description
 * 
 * @author fjli
 */
public class AgentDesc {

	private Map<String, Boolean> networkState;
	private AgentVersion version;
	private Integer gpus;

	/**
	 * Returns network states.
	 */
	public Map<String, Boolean> getNetworkState() {
		return networkState;
	}

	/**
	 * Set network states.
	 * 
	 * @param networkState - the states map
	 */
	public void setNetworkState(Map<String, Boolean> networkState) {
		this.networkState = networkState;
	}


	/**
	 * Returns the agent version.
	 */
	public AgentVersion getVersion() {
		return version;
	}

	/**
	 * Set the agent version.
	 * 
	 * @param version - the version to set
	 */
	public void setVersion(AgentVersion version) {
		this.version = version;
	}

	/**
	 * Test whether this agent is compatible with the specified agent or not.
	 * 
	 * @param other - the specified agent description.
	 * @return true if it is compatible.
	 */
	public boolean isCompatible(AgentDesc other) {
		return true;
	}

	public Integer getGpus() {
		return gpus;
	}

	public void setGpus(Integer gpus) {
		this.gpus = gpus;
	}
}
