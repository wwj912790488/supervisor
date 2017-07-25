package com.arcsoft.supervisor.model.vo.server;

import java.util.Map;

/**
 * This class describes the current state of the server.
 * 
 * @author fjli
 */
public class ServerStateInfo {

	private Map<String, Boolean> networkState;

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

}
