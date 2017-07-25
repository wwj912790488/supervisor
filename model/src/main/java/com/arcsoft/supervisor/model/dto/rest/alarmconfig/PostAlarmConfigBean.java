package com.arcsoft.supervisor.model.dto.rest.alarmconfig;

public class PostAlarmConfigBean {
	private String token;
	private AlarmConfigBean config;
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public AlarmConfigBean getConfig() {
		return config;
	}
	public void setConfig(AlarmConfigBean config) {
		this.config = config;
	}
	
}
