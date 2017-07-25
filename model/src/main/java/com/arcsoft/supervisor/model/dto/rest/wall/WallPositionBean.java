package com.arcsoft.supervisor.model.dto.rest.wall;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author zw.
 */
public class WallPositionBean {

    private byte row;
    @JsonProperty("col")
    private byte column;
    @JsonProperty("scrnid")
    private Integer screenId = -1;

    public WallPositionBean() {
    }

    public WallPositionBean(byte row, byte column, Integer screenId) {
        this.row = row;
        this.column = column;
        this.screenId = screenId;
    }

    public byte getRow() {
        return row;
    }

    public void setRow(byte row) {
        this.row = row;
    }

    public byte getColumn() {
        return column;
    }

    public void setColumn(byte column) {
        this.column = column;
    }

    public Integer getScreenId() {
        return screenId;
    }

    public void setScreenId(Integer screenId) {
        this.screenId = screenId;
    }
}
