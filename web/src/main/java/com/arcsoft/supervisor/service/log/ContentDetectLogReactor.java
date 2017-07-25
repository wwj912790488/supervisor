package com.arcsoft.supervisor.service.log;

import com.arcsoft.supervisor.model.domain.log.ContentDetectLog;
import com.arcsoft.supervisor.service.log.event.ContentDetectLogEvent;
import com.arcsoft.supervisor.service.log.impl.DefaultContentDetectLogServiceImpl;

/**
 * Callback interface executed after receive a ContentDetectLog.
 *
 * @author zw.
 * @see DefaultContentDetectLogServiceImpl#onContentDetectLogEvent(ContentDetectLogEvent)
 */
public interface ContentDetectLogReactor {

    /**
     * Returns the name of reactor.
     *
     * @return the name of reactor
     */
    String getName();

    /**
     * React for given ContentDetectLog object.
     *
     * @param contentDetectLog the ContentDetectLog instance
     */
    void react(ContentDetectLog contentDetectLog);

}
