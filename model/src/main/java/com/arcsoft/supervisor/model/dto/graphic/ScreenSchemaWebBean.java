package com.arcsoft.supervisor.model.dto.graphic;

import com.arcsoft.supervisor.model.domain.graphic.Screen;
import com.arcsoft.supervisor.model.domain.graphic.ScreenPosition;
import com.arcsoft.supervisor.model.domain.graphic.ScreenSchema;

import java.util.ArrayList;
import java.util.List;

public class ScreenSchemaWebBean {
	private Integer id;
	private String name;
    private Integer row;
    private Integer column;
	private Integer groupCount;
    private Integer switchTime;
	private List<ScreenPositionWebBean> positions;
    private Integer template;
    
    public ScreenSchemaWebBean() {}
    
    public ScreenSchemaWebBean(ScreenSchema schema) {
    	this.id = schema.getId();
    	this.row = schema.getRowCount();
    	this.column = schema.getColumnCount();
    	this.name = schema.getName();
		this.groupCount = schema.getGroupCount();
        this.switchTime = schema.getSwitchTime();
		this.positions = new ArrayList<>();
		for(ScreenPosition position : schema.getScreenPositions()) {
			positions.add(new ScreenPositionWebBean(position));
		}
        this.template = schema.getTemplateId();
    }
    
    
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getRow() {
		return row;
	}


	public void setRow(Integer row) {
		this.row = row;
	}


	public Integer getColumn() {
		return column;
	}


	public void setColumn(Integer column) {
		this.column = column;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getGroupCount() {
		return groupCount;
	}

	public void setGroupCount(Integer groupCount) {
		this.groupCount = groupCount;
	}

    public Integer getSwitchTime() {
        return switchTime;
    }

    public void setSwitchTime(Integer switchTime) {
        this.switchTime = switchTime;
    }

	public List<ScreenPositionWebBean> getPositions() {
		return positions;
	}

	public void setPositions(List<ScreenPositionWebBean> positions) {
		this.positions = positions;
	}

    public Integer getTemplate() {
        return template;
    }

    public void setTemplate(Integer template) {
        this.template = template;
    }
}
