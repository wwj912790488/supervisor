package com.arcsoft.supervisor.cluster.action.server;

import com.arcsoft.supervisor.cluster.action.BaseRequest;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Report the server caps changed.
 * 
 * @author fjli
 */
@XmlRootElement
public class CapabilitiesChangedRequest extends BaseRequest {

	private String id;

	/**
	 * Returns the agent id.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Set the agent id.
	 * 
	 * @param id - the agent id
	 */
	public void setId(String id) {
		this.id = id;
	}

}
