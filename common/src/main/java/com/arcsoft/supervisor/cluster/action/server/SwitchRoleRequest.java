package com.arcsoft.supervisor.cluster.action.server;

import com.arcsoft.supervisor.cluster.action.BaseRequest;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Request the slave server as master.
 * 
 * @author fjli
 */
@XmlRootElement
public class SwitchRoleRequest extends BaseRequest {

	private int reason;

	/**
	 * Default construct, for data converter.
	 */
	public SwitchRoleRequest() {
	}

	/**
	 * Returns the reason.
	 */
	public int getReason() {
		return reason;
	}

	/**
	 * Set the reason.
	 * 
	 * @param reason - the specified reason
	 */
	public void setReason(int reason) {
		this.reason = reason;
	}

}
