package com.arcsoft.supervisor.service.task.impl;

import com.arcsoft.supervisor.model.dto.graphic.ScreenPositionConfig;

/**
 * @author yshe.
 */

public class ValidScreenPosition{
    private Integer index;
    private Integer validindex;
    private ScreenPositionConfig config;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer getValidindex() {
        return validindex;
    }

    public void setValidindex(Integer validindex) {
        this.validindex = validindex;
    }

    public void setConfig(ScreenPositionConfig config){this.config=config;}
    public ScreenPositionConfig getConfig(){return config;}

    public ValidScreenPosition(Integer index,Integer validindex,ScreenPositionConfig config)
    {
        this.index = index;
        this.validindex = validindex;
        this.config = config;
    }
}