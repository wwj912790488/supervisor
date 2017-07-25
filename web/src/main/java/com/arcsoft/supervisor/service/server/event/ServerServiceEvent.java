package com.arcsoft.supervisor.service.server.event;

import java.util.EventObject;

/**
 * The base event for server service.
 * 
 * @author fjli
 */
public abstract class ServerServiceEvent extends EventObject {

	private static final long serialVersionUID = 7604571245470032333L;

	/**
	 * Construct new event instance.
	 * 
	 * @param source - the event source object
	 */
	public ServerServiceEvent(Object source) {
		super(source);
	}

}
