package com.arcsoft.supervisor.cluster.action.settings.network;

import com.arcsoft.supervisor.cluster.action.BaseResponse;
import com.arcsoft.supervisor.model.domain.settings.DNS;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
/**
 * @author hxiang
 * Response for get dns list;
 */
@XmlRootElement
public class ListDNSResponse extends BaseResponse {
	private List<DNS> dnsList = null;

	public List<DNS> getDnsList() {
		return dnsList;
	}

	public void setDnsList(List<DNS> dnsList) {
		this.dnsList = dnsList;
	}
	
}
