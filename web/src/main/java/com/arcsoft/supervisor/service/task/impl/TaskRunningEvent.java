package com.arcsoft.supervisor.service.task.impl;

import com.arcsoft.supervisor.model.domain.graphic.MessageStyle;

import java.util.EventObject;

public class TaskRunningEvent extends EventObject {

    private Integer screenId;
    private MessageStyle style;
    private String message;

    public TaskRunningEvent(Integer screenId, MessageStyle style, String message) {
        super(screenId);
        this.screenId = screenId;
        this.style = style;
        this.message = message;
    }

    public Integer getScreenId() {
        return screenId;
    }

    public void setScreenId(Integer screenId) {
        this.screenId = screenId;
    }

    public MessageStyle getStyle() {
        return style;
    }

    public void setStyle(MessageStyle style) {
        this.style = style;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
