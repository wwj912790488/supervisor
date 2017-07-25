package com.arcsoft.supervisor.model.dto.rest.layouttemplate;

import com.arcsoft.supervisor.model.domain.layouttemplate.LayoutTemplate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UpdatedLayoutTemplate {
	@JsonIgnore
	private Date updatedDate;
	private List<LayoutTemplate> templates;
	private Integer code;
	
	@JsonProperty("lastupdate")
	public String getLastUpdate() {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		return sf.format(updatedDate);
	}
	
	public Date getUpdatedDate() {
		return updatedDate;
	}
	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}
	public List<LayoutTemplate> getTemplates() {
		return templates;
	}
	public void setTemplates(List<LayoutTemplate> templates) {
		this.templates = templates;
	}
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	
	
}
