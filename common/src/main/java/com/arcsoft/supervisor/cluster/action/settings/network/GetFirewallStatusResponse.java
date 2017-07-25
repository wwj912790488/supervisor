package com.arcsoft.supervisor.cluster.action.settings.network;

import com.arcsoft.supervisor.cluster.action.BaseResponse;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Response for get firewall status.
 *
 * @author hxiang
 */
@XmlRootElement
public class GetFirewallStatusResponse extends BaseResponse {
	private boolean isServiceOn = false;

	public boolean isServiceOn() {
		return isServiceOn;
	}

	public void setServiceOn(boolean isServiceOn) {
		this.isServiceOn = isServiceOn;
	}
}
