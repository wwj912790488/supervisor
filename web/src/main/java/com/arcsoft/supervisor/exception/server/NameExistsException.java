package com.arcsoft.supervisor.exception.server;


import com.arcsoft.supervisor.exception.ObjectAlreadyExistsException;

/**
 * This exception will be thrown when rename or create server or server group.
 * 
 * @author fjli
 */
@SuppressWarnings("serial")
public class NameExistsException extends ObjectAlreadyExistsException {

	/**
	 * Construct new NameExistsException.
	 * 
	 * @param name - the specified name
	 */
	public NameExistsException(String name) {
		super(name);
	}

	/**
	 * Returns the exist name.
	 */
	public String getName() {
		return (String) getObject();
	}

}
