package com.arcsoft.supervisor.model.domain.graphic;

import com.arcsoft.supervisor.model.domain.layouttemplate.LayoutPosition;
import com.arcsoft.supervisor.model.domain.layouttemplate.LayoutPositionTemplate;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A schema indicate the predefine setting of {@code screen}.
 *
 * @author zw.
 */
@Entity
@Table(name = "screen_schema")
@DynamicUpdate
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
public class ScreenSchema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The type value of schema
     */
    @Column(name = "schema_value")
    private Integer value;
    @Column(name = "schema_name")
    private String name;
    @Column(name = "row_count")
    private Integer rowCount;
    @Column(name = "column_count")
    private Integer columnCount;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id")
    private Screen screen;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "screenSchema", fetch = FetchType.LAZY)
    private List<ScreenPosition> screenPositions = new ArrayList<>();

    private Integer groupCount;

    private Integer switchTime;

    @OneToOne
    @JoinColumn(name = "template_id")
    private LayoutPositionTemplate template;
    
    public ScreenSchema () {}
    
    public ScreenSchema(Screen screen, Integer value) {
    	this.screen = screen;
    	this.rowCount = 4;
    	this.columnCount = 4;
    	this.value = value;
    	this.name = "";
    	for(int i = 0; i < this.rowCount; i++)
    	{
    		for(int j = 0; j < this.columnCount; j++)
    		{
    			this.screenPositions.add(new ScreenPosition(this, i, j));
    		}
    	}
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Integer getRowCount() {
        return rowCount;
    }

    public void setRowCount(Integer rowCount) {
        this.rowCount = rowCount;
    }

    public Integer getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(Integer columnCount) {
        this.columnCount = columnCount;
    }

    public Screen getScreen() {
        return screen;
    }

    public void setScreen(Screen screen) {
        this.screen = screen;
    }

    public List<ScreenPosition> getScreenPositions() {
        return screenPositions;
    }

    public void setScreenPositions(List<ScreenPosition> screenPositions) {
        this.screenPositions = screenPositions;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    public Integer getGroupCount() {
        return groupCount == null ? 1 : groupCount;
    }

    public void setGroupCount(Integer groupCount) {
        this.groupCount = groupCount;
    }

    public Integer getSwitchTime() {
        return switchTime == null ? 30 : switchTime;
    }

    public void setSwitchTime(Integer switchTime) {
        this.switchTime = switchTime;
    }

    public LayoutPositionTemplate getTemplate() {
        return template;
    }

    public void setTemplate(LayoutPositionTemplate template) {
        this.template = template;
    }

    public Integer getTemplateId() {
        return this.template == null ? -1 : this.template.getId();
    }
}
