package com.arcsoft.supervisor.cluster.action.settings.network;

import com.arcsoft.supervisor.cluster.action.BaseResponse;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Response for get specific eth's used rate
 * 
 * @author xpeng
 * 
 */
@XmlRootElement
public class StatEthResponse extends BaseResponse {
	private int usedRate;

	public int getUsedRate() {
		return usedRate;
	}

	public void setUsedRate(int usedRate) {
		this.usedRate = usedRate;
	}
}
