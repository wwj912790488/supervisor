package com.arcsoft.supervisor.service.server.event;


import com.arcsoft.supervisor.model.domain.server.Server;

/**
 * This event will be delivered when some servers are added to group.
 * 
 * @author fjli
 */
public class ServerAddedEvent extends ServerEvent {

	private static final long serialVersionUID = -315579974637112353L;

    private Server server;


	/**
	 * Construct new event instance.
	 * 
	 * @param server - the added servers
	 */
	public ServerAddedEvent(Server server) {
		super(server);
	}

    public Server getAddedServer() {
        return server;
    }
}
