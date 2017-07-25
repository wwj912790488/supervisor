package com.arcsoft.supervisor.cluster.action.settings.network;

import com.arcsoft.supervisor.cluster.action.BaseResponse;
import com.arcsoft.supervisor.model.domain.settings.Eth;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
/**
 * Response for get all the eths.
 * @author xpeng
 *
 */
@XmlRootElement
public class ListEthResponse extends BaseResponse {
	private List<Eth> eths;

	public List<Eth> getEths() {
		return eths;
	}

	public void setEths(List<Eth> eths) {
		this.eths = eths;
	}
}
