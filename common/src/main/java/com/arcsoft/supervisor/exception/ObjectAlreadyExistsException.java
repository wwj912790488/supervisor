package com.arcsoft.supervisor.exception;

/**
 * This exception may be thrown when add or update an object.
 * 
 * @author fjli
 */
@SuppressWarnings("serial")
public class ObjectAlreadyExistsException extends ApplicationException {

	private Object object;

	/**
	 * Construct new exception with the specified object.
	 * 
	 * @param object - the specified object.
	 */
	public ObjectAlreadyExistsException(Object object) {
		this.object = object;
	}

	/**
	 * Returns the specified object.
	 */
	public Object getObject() {
		return this.object;
	}

}
