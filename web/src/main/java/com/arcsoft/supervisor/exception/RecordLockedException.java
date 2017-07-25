package com.arcsoft.supervisor.exception;

/**
 * @author zw.
 */
public class RecordLockedException extends ApplicationException {

    public RecordLockedException() {
        super();
    }

    public RecordLockedException(String message) {
        super(message);
    }

    public RecordLockedException(Throwable cause) {
        super(cause);
    }

    public RecordLockedException(String message, Throwable cause) {
        super(message, cause);
    }
}
