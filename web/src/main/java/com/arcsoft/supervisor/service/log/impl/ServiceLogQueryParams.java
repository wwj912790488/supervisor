package com.arcsoft.supervisor.service.log.impl;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;

/**
 * A class to holds the query parameters of {@code ServiceLog}.
 *
 * @author zw.
 */
public class ServiceLogQueryParams {

    private Date startTime;

    private Date endTime;

    private Byte module;

    private Byte level;

    private String description;

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

    public Byte getModule() {
        return module;
    }

    public void setModule(Byte module) {
        this.module = module;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Byte getLevel() {
        return level;
    }

    public void setLevel(Byte level) {
        this.level = level;
    }

    public String getStartTimeAsString(){
        if (startTime == null){
            return "";
        }
        return DateFormatUtils.format(startTime, "yyyy-MM-dd HH:mm:ss");
    }

    public String getEndTimeAsString(){
        if (endTime == null){
            return "";
        }
        return DateFormatUtils.format(endTime, "yyyy-MM-dd HH:mm:ss");
    }
}
