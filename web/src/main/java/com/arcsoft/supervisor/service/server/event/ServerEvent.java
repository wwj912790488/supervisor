package com.arcsoft.supervisor.service.server.event;

import com.arcsoft.supervisor.model.domain.server.Server;
import com.google.common.base.MoreObjects;

/**
 * The server event.
 *
 * @author fjli
 */
public abstract class ServerEvent extends ServerServiceEvent {

    private static final long serialVersionUID = 442301543768459907L;

    /**
     * Construct new event instance.
     *
     * @param server - the specified server
     */
    public ServerEvent(Server server) {
        super(server);
    }

    /**
     * Returns the server.
     */
    public Server getServer() {
        return (Server) getSource();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("ip", getServer().getIp())
                .add("id", getServer().getId())
                .add("alive", getServer().isAlive())
                .toString();
    }

}
