package com.arcsoft.supervisor.model.dto.graphic;

import com.arcsoft.supervisor.model.domain.server.ServerComponent;

public class SdiOutputWebBean {
	private Integer id;
	private String name;
	
	public SdiOutputWebBean() {
		
	}
	
	public SdiOutputWebBean(ServerComponent component) {
		this.id = component.getId();
		this.name = component.getName();
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
