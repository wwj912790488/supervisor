package com.arcsoft.supervisor.exception.service;


import com.arcsoft.supervisor.exception.ApplicationException;

/**
 * Thrown when failed to process the specified business logical.
 *
 * @author zw.
 */
public class BusinessException extends ApplicationException {

    /**
     * {@link Description} holds the exception description.
     */
    private final Description description;

    public BusinessException(Description description) {
        this.description = description;
    }

    public BusinessException(Description description, Throwable throwable) {
        super(throwable);
        this.description = description;
    }

    public BusinessException(Description description, Throwable throwable, String message) {
        super(message, throwable);
        this.description = description;
    }

    public Description getDescription() {
        return this.description;
    }

    public static BusinessException create(Description statusCode) {
        return new BusinessException(statusCode);
    }

    public static BusinessException create(Description statusCode, Throwable throwable) {
        return new BusinessException(statusCode, throwable);
    }

    public static BusinessException create(Description statusCode, Throwable throwable, String message) {
        return new BusinessException(statusCode, throwable, message);
    }

    public static BusinessException wrap(Description statusCode, Throwable cause) {
        if (cause instanceof BusinessException) {
            BusinessException exception = (BusinessException) cause;
            return statusCode != exception.getDescription()
                    ? BusinessException.create(statusCode, cause, exception.getMessage())
                    : exception;
        }else {
            return BusinessException.create(statusCode, cause, cause.getMessage());
        }
    }

    @Override
    public String toString() {
        return super.toString() + " - " + this.description;
    }
}
