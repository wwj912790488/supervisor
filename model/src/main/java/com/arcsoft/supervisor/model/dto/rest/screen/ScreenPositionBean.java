package com.arcsoft.supervisor.model.dto.rest.screen;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author zw.
 */
public class ScreenPositionBean {

    private Integer row;
    @JsonProperty("col")
    private Integer column;
    private Integer x;
    private Integer y;
    private Integer group;
    private Integer channel = -1;

    public ScreenPositionBean() {
    }

    public ScreenPositionBean(Integer row, Integer column, Integer x, Integer y, Integer group, Integer channel) {
        this.row = row;
        this.column = column;
        this.x = x;
        this.y = y;
        this.channel = channel;
        this.group = group;
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

    public Integer getChannel() {
        return channel;
    }

    public void setChannel(Integer channel) {
        this.channel = channel;
    }

    public Integer getGroup() {
        return group;
    }

    public void setGroup(Integer group) {
        this.group = group;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }
}
