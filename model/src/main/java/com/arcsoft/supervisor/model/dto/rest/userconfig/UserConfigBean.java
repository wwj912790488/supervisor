package com.arcsoft.supervisor.model.dto.rest.userconfig;

import java.util.List;

public class UserConfigBean {
	private Integer id;
	private Integer template_id;
	private List<UserConfigChannelBean> channels;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getTemplate_id() {
		return template_id;
	}
	public void setTemplate_id(Integer template_id) {
		this.template_id = template_id;
	}
	public List<UserConfigChannelBean> getChannels() {
		return channels;
	}
	public void setChannels(List<UserConfigChannelBean> channels) {
		this.channels = channels;
	}
		
}
