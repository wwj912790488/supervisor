package com.arcsoft.supervisor.model.domain.layouttemplate;

import com.arcsoft.supervisor.commons.profile.Sartf;
import com.arcsoft.supervisor.model.domain.userconfig.UserConfig;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "layout_template")
@Sartf
public class LayoutTemplate {

    public static final String TEMPLATE_PREFIX = "/template/";
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @JsonIgnore
    private String path;
    
    @JsonIgnore
    private Date lastUpdate;
    
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "template", fetch = FetchType.LAZY)
    private List<LayoutTemplateCell> cells = new ArrayList<>();
    
    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "template", fetch = FetchType.LAZY)
    private LayoutTemplateInfo info;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "template", fetch = FetchType.LAZY)
    private List<UserConfig> userconfigs;
    
    @JsonProperty("path")
    public String getRelativePath(){
    	return TEMPLATE_PREFIX + path;
    }
    
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public List<LayoutTemplateCell> getCells() {
		return cells;
	}

	public void setCells(List<LayoutTemplateCell> cells) {
		this.cells = cells;
	}

	public LayoutTemplateInfo getInfo() {
		return info;
	}

	public void setInfo(LayoutTemplateInfo info) {
		this.info = info;
	}

}
