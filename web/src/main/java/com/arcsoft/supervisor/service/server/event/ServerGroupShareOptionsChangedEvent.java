package com.arcsoft.supervisor.service.server.event;

import com.arcsoft.supervisor.model.domain.server.ServerGroup;

/**
 * This event will be delivered when the server group share options changed.
 * 
 * @author fjli
 */
public class ServerGroupShareOptionsChangedEvent extends ServerGroupEvent {

	private static final long serialVersionUID = -6672792540931212027L;

	/**
	 * Construct new event instance.
	 * 
	 * @param group - the specified group
	 */
	public ServerGroupShareOptionsChangedEvent(ServerGroup group) {
		super(group);
	}

}
