package com.arcsoft.supervisor.model.dto.rest.userconfig;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class AudioChannelBean {
	private String token;
	private Integer audio_cell_index;
	private Integer code;
	private Integer config_id;
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}	

	public Integer getAudio_cell_index() {
		return audio_cell_index;
	}
	public void setAudio_cell_index(Integer audio_cell_index) {
		this.audio_cell_index = audio_cell_index;
	}
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public Integer getConfig_id() {
		return config_id;
	}
	public void setConfig_id(Integer config_id) {
		this.config_id = config_id;
	}
	
	
}
