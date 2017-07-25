package com.arcsoft.supervisor.model.dto.rest.screen;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean class to constructs the screen preview response.
 *
 * @author zw
 */
public class ScreenPreviewBean {

    /**
     * The request handle status.
     */
    private int code;

    /**
     * The rtsp url of IP-Stream compose.
     */
    private String url;

    /**
     * The row count of screen
     */
    @JsonProperty(value = "rowcount")
    private int rowCount;

    /**
     * The column count of screen
     */
    @JsonProperty(value = "colcount")
    private int colCount;


    @JsonProperty(value = "positions")
    private List<PositionUrlBean> positionUrlBeans = new ArrayList<>();


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public int getColCount() {
        return colCount;
    }

    public void setColCount(int colCount) {
        this.colCount = colCount;
    }

    public List<PositionUrlBean> getPositionUrlBeans() {
        return positionUrlBeans;
    }

    public void setPositionUrlBeans(List<PositionUrlBean> positionUrlBeans) {
        this.positionUrlBeans = positionUrlBeans;
    }
}
