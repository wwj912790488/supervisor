package com.arcsoft.supervisor.service.log.event;

import com.arcsoft.supervisor.model.domain.log.ContentDetectLog;

import java.util.EventObject;

/**
 * A event class for <code>ContentDetectLog</code>.
 *
 * @author zw.
 */
public class ContentDetectLogEvent extends EventObject {

    private final ContentDetectLog contentDetectLog;

    private final boolean enableRecord;

    /**
     * Constructs a prototypical Event.
     *
     * @param contentDetectLog The object on which the Event initially occurred.
     * @param enableRecord the channel record is enable or not
     * @throws IllegalArgumentException if source is null.
     */
    public ContentDetectLogEvent(ContentDetectLog contentDetectLog, boolean enableRecord) {
        super(contentDetectLog);
        this.contentDetectLog = contentDetectLog;
        this.enableRecord = enableRecord;
    }

    public ContentDetectLog getContentDetectLog() {
        return contentDetectLog;
    }

    public boolean isEnableRecord() {
        return enableRecord;
    }
}
