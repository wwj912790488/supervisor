package com.arcsoft.supervisor.service.server.event;


import com.arcsoft.supervisor.model.domain.server.ServerGroup;

/**
 * The server group event.
 * 
 * @author fjli
 */
public abstract class ServerGroupEvent extends ServerServiceEvent {

	private static final long serialVersionUID = 3431988881060833932L;

	protected ServerGroup group;

	/**
	 * Construct new event instance.
	 * 
	 * @param group - the specified group
	 */
	public ServerGroupEvent(ServerGroup group) {
		super(group);
		this.group = group;
	}

	/**
	 * Returns the specified group.
	 */
	public ServerGroup getGroup() {
		return group;
	}

}
