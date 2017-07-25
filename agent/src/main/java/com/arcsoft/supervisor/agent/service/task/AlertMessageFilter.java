package com.arcsoft.supervisor.agent.service.task;

/**
 * Perform filter operators for alert message.
 *
 * @author zw.
 */
public interface AlertMessageFilter {

    /**
     * Checks the alert message need filter or not.
     *
     * @param level the level of alert
     * @param code the error code
     * @param msg the message
     * @return {@code true} or {@code false} if the alert message don't need filter
     */
    boolean doFilter(int level, int code, String msg);

}
