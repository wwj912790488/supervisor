package com.arcsoft.supervisor.service.audit;

import com.arcsoft.supervisor.commons.SupervisorDefs;

import java.util.Date;

/**
 * Defines the based structure of audit content.
 *
 * @author zw.
 */
public interface AuditContent {

    /**
     * Retrieves the {@code AuditLevel}.
     *
     * @return the level
     */
    AuditLevel getLevel();

    /**
     * Retrieves the description.
     *
     * @return the description
     */
    String getDescription();

    Date getCreatedTime();

    SupervisorDefs.Modules getModule();

}
