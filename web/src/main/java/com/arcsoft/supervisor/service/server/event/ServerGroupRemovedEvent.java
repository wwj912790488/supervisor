package com.arcsoft.supervisor.service.server.event;

import com.arcsoft.supervisor.model.domain.server.ServerGroup;

/**
 * This event will be delivered when a group is removed.
 * 
 * @author fjli
 */
public class ServerGroupRemovedEvent extends ServerGroupEvent {

	private static final long serialVersionUID = 4111703156273016257L;

	/**
	 * Construct new event instance.
	 * 
	 * @param group - the removed group
	 */
	public ServerGroupRemovedEvent(ServerGroup group) {
		super(group);
	}

}
