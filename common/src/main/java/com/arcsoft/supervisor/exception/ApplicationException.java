package com.arcsoft.supervisor.exception;

/**
 * Application exception.
 * 
 * @author fjli
 */
@SuppressWarnings("serial")
public class ApplicationException extends RuntimeException {

	/**
	 * Constructs a new service exception.
	 */
	public ApplicationException() {
	}

	/**
	 * Constructs new exception with the specified message.
	 * 
	 * @param message - the specified message
	 */
	public ApplicationException(String message) {
		super(message);
	}

	/**
	 * Constructs new exception with the specified cause.
	 * 
	 * @param cause - the specified cause
	 */
	public ApplicationException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs new exception with the specified message and cause.
	 * 
	 * @param message - the specified message
	 * @param cause - the specified cause
	 */
	public ApplicationException(String message, Throwable cause) {
		super(message, cause);
	}

}
