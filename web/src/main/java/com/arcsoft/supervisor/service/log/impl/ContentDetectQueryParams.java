package com.arcsoft.supervisor.service.log.impl;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A class for holds the query parameters of content detect log.
 *
 * @author zw.
 */
public class ContentDetectQueryParams {

    private String channelName;

    private Integer channelId;

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    private Date startTime;

    private Date endTime;

    /**
     * The value of content detect types.
     */
    private List<Integer> types = new ArrayList<>();

    private List<Integer> groups = new ArrayList<>();

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

    public List<Integer> getTypes() {
        return types;
    }

    public void setTypes(List<Integer> types) {
        this.types = types;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
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



    public List<Integer> getGroups() {
        return groups;
    }

    public void setGroups(List<Integer> groups) {
        this.groups = groups;
    }


}
