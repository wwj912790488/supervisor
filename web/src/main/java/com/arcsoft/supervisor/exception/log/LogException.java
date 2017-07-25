package com.arcsoft.supervisor.exception.log;

import com.arcsoft.supervisor.exception.ApplicationException;

/**
 * A exception indicate the user is exists.
 *
 * @author jt.
 */
public class LogException extends ApplicationException {

    private static final long serialVersionUID = 4532287469132463393L;
    
    private final String logException;

    public LogException(String logException) {
        super("Process log error.");
        this.logException = logException;
    }

    public String getLogException() {
        return logException;
    }
}
