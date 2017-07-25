package com.arcsoft.supervisor.model.dto.rest.userconfig;

import com.arcsoft.supervisor.model.domain.userconfig.UserConfig;

import java.util.List;

public class UserConfigSetBean {
	
	private Integer running_id;
	private List<UserConfig> configs;
	private Integer code;
	
	public Integer getRunning_id() {
		return running_id;
	}
	public void setRunning_id(Integer running_id) {
		this.running_id = running_id;
	}
	public List<UserConfig> getConfigs() {
		return configs;
	}
	public void setConfigs(List<UserConfig> configs) {
		this.configs = configs;
	}
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	
	
}
