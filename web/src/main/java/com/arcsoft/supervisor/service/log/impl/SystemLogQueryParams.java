package com.arcsoft.supervisor.service.log.impl;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;

/**
 * @author zw.
 */
public class SystemLogQueryParams {

    private Date startTime;

    private Date endTime;

    private String operationInfo;

    /**
     * The value of functional type.
     */
    private Integer funcType;

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

    public String getOperationInfo() {
        return operationInfo;
    }

    public void setOperationInfo(String operationInfo) {
        this.operationInfo = operationInfo;
    }

    public Integer getFuncType() {
        return funcType;
    }

    public void setFuncType(Integer funcType) {
        this.funcType = funcType;
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
