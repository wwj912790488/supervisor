package com.arcsoft.supervisor.service.server;

import com.arcsoft.supervisor.model.domain.server.AbstractOpsServer;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Defines functional logic for ops.
 *
 * @author zw.
 */
public interface OpsServerService<T extends AbstractOpsServer> {

    void save(T opsServer);

    T getById(String id);

    List<T> findAll();

    void delete(String id);

    void WakeOpsServer(T server) throws UnknownHostException, SocketException, IllegalArgumentException, IOException;
}
