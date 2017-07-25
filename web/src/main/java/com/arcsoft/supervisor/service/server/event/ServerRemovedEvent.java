package com.arcsoft.supervisor.service.server.event;

import com.arcsoft.supervisor.model.domain.server.Server;

import java.util.ArrayList;
import java.util.List;


/**
 * This event will be delivered when some servers are removed from group.
 * 
 * @author fjli
 */
public class ServerRemovedEvent extends ServerServiceEvent {

	private static final long serialVersionUID = -315579974637112353L;

    private List<Server> servers;

	/**
	 * Construct new event instance.
	 * 
	 * @param servers - the removed servers
	 */
	public ServerRemovedEvent(List<Server> servers) {
		super(servers);
        this.servers = servers;
	}

    public ServerRemovedEvent(Server server) {
        super(server);
        this.servers = new ArrayList<>();
        this.servers.add(server);
    }

    /**
	 * Returns the removed servers.
	 */
	public List<Server> getRemovedServers() {
		return servers;
	}

}
