package com.arcsoft.supervisor.model.domain.layouttemplate;

import com.arcsoft.supervisor.commons.profile.Sartf;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

@Entity
@Table(name="layout_template_cell")
@Sartf
public class LayoutTemplateCell {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
	private LayoutTemplate template;
	@JsonProperty("cell_index")
	private Integer cellIndex;
	private Integer xPos;
	private Integer yPos;
	private Integer width;
	private Integer height;
	
	public LayoutTemplate getTemplate() {
		return template;
	}
	public void setTemplate(LayoutTemplate template) {
		this.template = template;
	}

	public Integer getCell_index() {
		return cellIndex;
	}
	public void setCell_index(Integer cell_index) {
		this.cellIndex = cell_index;
	}
	public Integer getxPos() {
		return xPos;
	}
	public void setxPos(Integer xPos) {
		this.xPos = xPos;
	}
	public Integer getyPos() {
		return yPos;
	}
	public void setyPos(Integer yPos) {
		this.yPos = yPos;
	}
	public Integer getWidth() {
		return width;
	}
	public void setWidth(Integer width) {
		this.width = width;
	}
	public Integer getHeight() {
		return height;
	}
	public void setHeight(Integer height) {
		this.height = height;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
}
