package com.arcsoft.supervisor.model.dto.graphic;

import java.util.ArrayList;
import java.util.List;

public class DeviceSdiWebBean {
	private String id;
	private String name;
	private List<SdiOutputWebBean> sdis = new ArrayList<SdiOutputWebBean>();
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<SdiOutputWebBean> getSdis() {
		return sdis;
	}
	public void setSdis(List<SdiOutputWebBean> sdis) {
		this.sdis = sdis;
	}
	
	
	
}
