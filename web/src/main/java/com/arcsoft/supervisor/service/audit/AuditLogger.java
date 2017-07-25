package com.arcsoft.supervisor.service.audit;

/**
 * A audit logging interface api.
 *
 * @author zw.
 */
public interface AuditLogger<T extends AuditContent> extends AuditFormatter<T>{

    /**
     * Log with the givens {@code content}.
     *
     * @param content the audio content instance
     */
    public void log(T content);

}
