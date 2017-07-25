package com.arcsoft.supervisor.model.dto.rest.screen;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author zw.
 */
public class ScreenSchemaBean {

    private Integer id;
    private Integer row;
    @JsonProperty("col")
    private Integer column;
    @JsonProperty("group")
    private Integer groupCount;
    private Integer switchTime;
    @JsonProperty("subscrns")
    private List<ScreenPositionBean> positionJsons;

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

    public List<ScreenPositionBean> getPositionJsons() {
        return positionJsons;
    }

    public void setPositionJsons(List<ScreenPositionBean> positionJsons) {
        this.positionJsons = positionJsons;
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
}
