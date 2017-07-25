package com.arcsoft.supervisor.service.server.event;


import com.arcsoft.supervisor.model.domain.server.Server;

/**
 * This event will be delivered when a slave server take over a master server.
 * 
 * @author fjli
 */
public class ServerTakeOverEvent extends ServerServiceEvent {

	private static final long serialVersionUID = -7876681623400494923L;

	private Server oldServer;
	private Server newServer;

	/**
	 * Construct new event instance.
	 * 
	 * @param oldServer - the old server
	 * @param newServer - the new server
	 */
	public ServerTakeOverEvent(Server oldServer, Server newServer) {
        super(oldServer);
        this.oldServer = oldServer;
		this.newServer = newServer;
	}

	/**
	 * Returns the old server.
	 */
	public Server getOldServer() {
		return oldServer;
	}

	/**
	 * Return the new server.
	 */
	public Server getNewServer() {
		return newServer;
	}
}
