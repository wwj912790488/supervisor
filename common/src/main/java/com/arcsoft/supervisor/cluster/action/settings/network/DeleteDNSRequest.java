package com.arcsoft.supervisor.cluster.action.settings.network;

import com.arcsoft.supervisor.cluster.action.BaseRequest;
import com.arcsoft.supervisor.model.domain.settings.DNS;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * @author hxiang
 *
 */
@XmlRootElement
public class DeleteDNSRequest extends BaseRequest {
	private List<DNS> dnsList = null;
	
	public DeleteDNSRequest(){
		
	}
	
	public DeleteDNSRequest(List<DNS> dnsList){
		this.dnsList = dnsList;
	}

	public void setDNSList(List<DNS> dnsList){
		this.dnsList = dnsList;
	}
	
	public List<DNS> getDNSList(){
		return this.dnsList;
	}
}
