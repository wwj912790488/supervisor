package com.arcsoft.supervisor.cluster.action.settings.network;

import com.arcsoft.supervisor.cluster.action.BaseRequest;
import com.arcsoft.supervisor.model.domain.settings.Eth;

import javax.xml.bind.annotation.XmlRootElement;
/**
 * Request for update specific eth.
 * @author xpeng
 *
 */
@XmlRootElement
public class SaveEthRequest extends BaseRequest {

	private Eth eth;

	public SaveEthRequest() {
	}

	public SaveEthRequest(Eth eth) {
		this.eth = eth;
	}

	public Eth getEth() {
		return eth;
	}

	public void setEth(Eth eth) {
		this.eth = eth;
	}

}
