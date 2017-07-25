package com.arcsoft.supervisor.service.task;

import com.arcsoft.supervisor.model.domain.server.Server;
import com.arcsoft.supervisor.model.domain.server.ServerFunction;
import com.arcsoft.supervisor.model.domain.server.ServerType;

/**
 * A interface to get lower used server with load balance.
 *
 * @author zw.
 */
public interface ServerLoadBalance {

    /**
     * Returns the idled server by given {@code type} and {@code function}.
     *
     * @param type     which type of server
     * @param function which function of server
     * @return the idled server or {@code null} if there is haven't any idled server
     */
    public Server getServer(ServerType type, ServerFunction function);

}
