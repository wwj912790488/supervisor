package com.arcsoft.supervisor.exception;

/**
 * This exception may be thrown when update or delete an object.
 * 
 * @author fjli
 */
@SuppressWarnings("serial")
public class ObjectNotExistsException extends ApplicationException {

	private Object object;

	/**
	 * Construct new exception with the specified object.
	 * 
	 * @param object - the specified object.
	 */
	public ObjectNotExistsException(Object object) {
		this.object = object;
	}

	/**
	 * Returns the specified object.
	 */
	public Object getObject() {
		return this.object;
	}

}
