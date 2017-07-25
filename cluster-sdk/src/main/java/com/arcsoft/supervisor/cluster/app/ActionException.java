package com.arcsoft.supervisor.cluster.app;


import com.arcsoft.supervisor.cluster.message.Message;

/**
 * This exception throws when execute action.
 *
 * @author fjli
 */
@SuppressWarnings("serial")
public class ActionException extends Exception implements Message {

    private int errorCode = ErrorCode.UNKNOWN;

    /**
     * Constructs a new exception with the specified code.
     *
     * @param errorCode - the specified error code
     */
    public ActionException(int errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Constructs a new exception with the specified code and detail message.
     *
     * @param errorCode    - the specified error code
     * @param message - the specified detail message
     */
    public ActionException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Constructs a new exception with the specified code and detail message.
     *
     * @param errorCode    - the specified error code
     * @param message - the specified detail message
     * @param cause   - the specified cause
     */
    public ActionException(int errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * Returns the error code.
     */
    public int getErrorCode() {
        return this.errorCode;
    }

    @Override
    public int getMessageType() {
        return Message.TYPE_EXCEPTION;
    }

}
