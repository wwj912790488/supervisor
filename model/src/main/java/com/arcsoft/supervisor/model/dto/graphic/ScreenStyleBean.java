package com.arcsoft.supervisor.model.dto.graphic;

import com.arcsoft.supervisor.model.domain.graphic.MessageStyle;

public class ScreenStyleBean {
    private Integer screenId;
    private MessageStyle style;

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
}
