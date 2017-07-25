package com.arcsoft.supervisor.service.alarm.impl;

import java.util.Date;

public class CustomAlarmLogQueryParams {

    private Date startTime;
    private Date endTime;
    private Integer configId;

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getConfigId() {
        return configId;
    }

    public void setConfigId(Integer configId) {
        this.configId = configId;
    }
}
