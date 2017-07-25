package com.arcsoft.supervisor.model.dto.rest.userconfig;

public class PostUserConfigBean {
	private String token;
	private UserConfigBean config;
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public UserConfigBean getConfig() {
		return config;
	}
	public void setConfig(UserConfigBean config) {
		this.config = config;
	}
	
}
