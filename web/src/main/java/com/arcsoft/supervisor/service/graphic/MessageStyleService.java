package com.arcsoft.supervisor.service.graphic;

import com.arcsoft.supervisor.model.domain.graphic.MessageStyle;

public interface MessageStyleService {
    MessageStyle getDefault();
    void updateDefault(MessageStyle style);
    MessageStyle save(MessageStyle style);
}
