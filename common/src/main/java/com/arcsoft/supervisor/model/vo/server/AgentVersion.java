package com.arcsoft.supervisor.model.vo.server;

/**
 * The versions.
 * 
 * @author fjli
 */
public class AgentVersion {

	private String agentVersion;
	private String transcoderVersion;

	/**
	 * Returns the agent version.
	 */
	public String getAgentVersion() {
		return agentVersion;
	}

	/**
	 * Set the agent version.
	 * 
	 * @param agentVersion - the agent version to set
	 */
	public void setAgentVersion(String agentVersion) {
		this.agentVersion = agentVersion;
	}

	/**
	 * Returns the transcoder version.
	 */
	public String getTranscoderVersion() {
		return transcoderVersion;
	}

	/**
	 * Set the transcoder version.
	 * 
	 * @param transcoderVersion - the transcoder version to set
	 */
	public void setTranscoderVersion(String transcoderVersion) {
		this.transcoderVersion = transcoderVersion;
	}

}
