package com.arcsoft.supervisor.agent.service.task.filter;

import com.arcsoft.supervisor.agent.service.task.AlertMessageFilter;

/**
 * An implementation of AlertMessageFilter to do filter with error code.
 *
 * @author zw.
 */
public class ErrorCodeAlertMessageFilter implements AlertMessageFilter {

    /**
     * <ul>
     *     <li>hex prefix with 21 denotes signal detect message.We need ignore it because signal
     *     detect result is report by content detect library.</li>
     *
     * </ul>
     *
     */
    private static final String[] IGNORE_ERRORCODE_HEX_PREFIX = {
              "21"
    };

    @Override
    public boolean doFilter(int level, int code, String msg) {
        String hexCode = String.format("%x", code);
        for (String prefix : IGNORE_ERRORCODE_HEX_PREFIX) {
            if (hexCode.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}
