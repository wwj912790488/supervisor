package com.arcsoft.supervisor.model.domain.layouttemplate;

import com.arcsoft.supervisor.commons.profile.Sartf;

import javax.persistence.*;

@Entity
@Table(name="layout_template_info")
@Sartf
public class LayoutTemplateInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
	private LayoutTemplate template;
	
	private Integer totalWidth;
	private Integer totalHeight;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public LayoutTemplate getTemplate() {
		return template;
	}
	public void setTemplate(LayoutTemplate template) {
		this.template = template;
	}
	public Integer getTotalWidth() {
		return totalWidth;
	}
	public void setTotalWidth(Integer totalWidth) {
		this.totalWidth = totalWidth;
	}
	public Integer getTotalHeight() {
		return totalHeight;
	}
	public void setTotalHeight(Integer totalHeight) {
		this.totalHeight = totalHeight;
	}
	
}
