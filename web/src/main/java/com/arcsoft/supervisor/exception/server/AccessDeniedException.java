package com.arcsoft.supervisor.exception.server;


import com.arcsoft.supervisor.exception.ApplicationException;

/**
 * This exception will be thrown if the server access is denied.
 * 
 * @author fjli
 */
@SuppressWarnings("serial")
public class AccessDeniedException extends ApplicationException {

    public AccessDeniedException() {
    }

    public AccessDeniedException(String message) {
        super(message);
    }
}
