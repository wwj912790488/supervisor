package com.arcsoft.supervisor.cluster.action.server;

import com.arcsoft.supervisor.cluster.action.BaseRequest;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class ListSDIRequest extends BaseRequest {
	private String id;
	private List<String> sdis;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<String> getSdis() {
		return sdis;
	}
	public void setSdis(List<String> sdis) {
		this.sdis = sdis;
	}
	
}
