package com.arcsoft.supervisor.cluster.action.server;

import com.arcsoft.supervisor.cluster.action.BaseRequest;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Live role switch request. When the slave detected the master is unavailable,
 * the slave will server as master, then this server will send this request to
 * the commander, in order to change the role of previous master to slave role.
 * 
 * @author fjli
 */
@XmlRootElement
public class LiveRoleSwitchRequest extends BaseRequest {

	private String id;

	/**
	 * Default construct, for data converter.
	 */
	public LiveRoleSwitchRequest() {
	}

	/**
	 * Construct role switch request with the specified node id.
	 * 
	 * @param id - the specified node id
	 */
	public LiveRoleSwitchRequest(String id) {
		this.id = id;
	}

	/**
	 * Returns the node id, which will switch the role.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Set the node id which will switch the role.
	 * 
	 * @param id - the node id
	 */
	public void setId(String id) {
		this.id = id;
	}

}
