package com.arcsoft.supervisor.service.server.event;

import com.arcsoft.supervisor.model.domain.server.ServerGroup;

/**
 * This event will be delivered when a group is added.
 * 
 * @author fjli
 */
public class ServerGroupAddedEvent extends ServerGroupEvent {

	private static final long serialVersionUID = -6428760382428278468L;

	/**
	 * Construct new event instance.
	 * 
	 * @param group - the added group
	 */
	public ServerGroupAddedEvent(ServerGroup group) {
		super(group);
	}

}
