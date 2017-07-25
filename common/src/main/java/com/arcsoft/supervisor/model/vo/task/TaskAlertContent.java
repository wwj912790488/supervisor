package com.arcsoft.supervisor.model.vo.task;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * A object to holds task alert content and other values.
 *
 * @author zw.
 */
public class TaskAlertContent {

    /**
     * The id of task object
     */
    private int taskId;
    /**
     * The value 0 indicates WARNING and value 1 is ERROR
     */
    private int level;
    /**
     * The value of error code
     */
    private int code;
    /**
     * The description
     */
    private String message;

    /**
     * The alert message source.
     */
    private String ip;

    public TaskAlertContent() {}

    public TaskAlertContent(int taskId, int level, int code, String message, String ip) {
        this.taskId = taskId;
        this.level = level;
        this.code = code;
        this.message = message;
        this.ip = ip;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("ip", ip)
                .append("taskId", taskId)
                .append("level", level)
                .append("code", String.format("0x%x", code))
                .append("message", message)
                .toString();
    }

}
