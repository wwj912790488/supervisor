package com.arcsoft.supervisor.service;

import com.arcsoft.supervisor.commons.spring.event.EventManager;
import com.arcsoft.supervisor.model.domain.system.SystemSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Convenient super class for service to provide some common method.
 *
 * @author zw.
 */
public abstract class ServiceSupport {

    /** The Logger instance for currently class. */
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("systemSettings")
    protected SystemSettings systemSettings;

    @Autowired
    private EventManager eventManager;

    public EventManager getEventManager() {
        return eventManager;
    }
}
