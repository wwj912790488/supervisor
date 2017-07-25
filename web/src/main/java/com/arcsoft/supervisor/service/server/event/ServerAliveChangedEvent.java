package com.arcsoft.supervisor.service.server.event;


import com.arcsoft.supervisor.model.domain.server.Server;

/**
 * This event will be delivered when the server alive state is changed.
 * 
 * @author fjli
 */
public class ServerAliveChangedEvent extends ServerEvent {

	private static final long serialVersionUID = -4955593401103799039L;

	/**
	 * Construct new event instance.
	 * 
	 * @param server - the server who's alive state is changed
	 */
	public ServerAliveChangedEvent(Server server) {
		super(server);
	}

}
