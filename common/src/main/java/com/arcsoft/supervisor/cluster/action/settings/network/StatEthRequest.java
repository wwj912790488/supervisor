package com.arcsoft.supervisor.cluster.action.settings.network;

import com.arcsoft.supervisor.cluster.action.BaseRequest;

import javax.xml.bind.annotation.XmlRootElement;
/**
 * Request for get specific eth's network used rate
 * @author xpeng
 *
 */
@XmlRootElement
public class StatEthRequest extends BaseRequest {
	private String ethId;

	public StatEthRequest(){
		
	}
	
	public StatEthRequest(String ethId) {
		this.ethId = ethId;
	}

	public String getEthId() {
		return ethId;
	}

	public void setEthId(String ethId) {
		this.ethId = ethId;
	}

}
