package com.arcsoft.supervisor.service.task.impl;

import java.util.Date;
import java.util.EventObject;

public class TaskStopEvent extends EventObject {
    private Integer taskId;
    Date stopTime;

    public TaskStopEvent(Integer taskId, Date stopTime) {
        super(taskId);
        this.taskId = taskId;
        this.stopTime = stopTime;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public Date getStopTime() {
        return stopTime;
    }

    public void setStopTime(Date stopTime) {
        this.stopTime = stopTime;
    }
}
