package com.arcsoft.supervisor.service.server;

import com.arcsoft.supervisor.exception.ObjectNotExistsException;
import com.arcsoft.supervisor.exception.server.AccessDeniedException;
import com.arcsoft.supervisor.exception.server.NameExistsException;
import com.arcsoft.supervisor.exception.server.ServerBusyException;
import com.arcsoft.supervisor.model.domain.server.Server;
import com.arcsoft.supervisor.model.domain.server.ServerFunction;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Server services.
 *
 * @author zw
 */
public interface ServerService {

    /**
     * List all internal server.
     */
    Page<Server> list(int pageNo, int pageSize);

    /**
     * Returns server items with given {@code ids}.
     *
     * @param ids the list of server id
     * @return a list of {@code Server} found by the ids
     */
    List<Server> getByIds(Collection<String> ids);

    /**
     * Returns un-joined agent server items.
     *
     * @return a list of {@code Server} of un-joined agent
     */
    List<Server> getUnjoinedAgentServers();

    /**
     * Returns joined agent server items.
     *
     * @return a list of {@code Server} of joined agent
     */
    List<Server> getJoinedAgentServers();

    /**
     * Returns server with givens {@code ip} and {@code port}.
     *
     * @param ip   the ip of server
     * @param port the port of server
     * @return a server found by ip and port or {@code null} if not found
     */
//    Server getServerWithIpAndPort(String ip, int port);

    /**
     * Returns joined and alive server items with givens {@code type}.
     *
     * @param type the type of server
     * @return a list of joined and alive {@code Server} with type
     */
    List<Server> findByJoinedTrueAndAliveTrueAndType(int type);


//    Server findByJoinedTrueAndAliveTrueAndId(String id);

    /**
     * Add a server.
     *
     * @param server the server will be added
     */
    void addServer(Server server);

    void addServers(List<Server> servers);

    List<Server> addAndScanServer() throws IOException;

    void updateToJoinAndSetNameAndRemarkAndActiveFunctions(Server server);

    void updateNameAndRemarkAndActiveFunctions(Server server);

    List<Server> getJoinedAndAlivedAgentServersWithFunction(ServerFunction function);

    /**
     * Changes the server to join.
     *
     * @param id the identify value of server
     */
//    void updateToJoin(String id);


    /**
     * Add servers to the specified group.
     *
     * @param group - the specified group.
     * @throws SystemNotInitializedException  if system has not initialized
     * @throws AccessDeniedForSlaveException  if system is slave.
     * @throws ObjectNotExistsException       if the group is not exist
     * @throws ObjectAlreadyExistsException   if one of the server already added
     * @throws NameExistsException            if the name already exist
     * @throws ServerNotAvailableException    if the server is not available
     * @throws RemoteException                if execute remote action failed
     * @throws TooManyServerException         if add too many servers in live group
     * @throws ServerIncompatibleException    if the added server is incompatible with others
     * @throws NoServerAvailableException     if no server is available.
     * @throws HeatBeatEventNotReceivedException if heart beat event is not received
     */
//	void addServers(ServerGroup group);

    /**
     * Get server with the specified id.
     *
     * @param id - the specified server id
     * @return Returns the server with the specified id, or null if not exist.
     */
    Server getServer(String id);

    /**
     * Check the server with the specified name is exist or not.
     *
     * @param name - the specified server name
     * @return true if the server with the specified name is exist.
     */
    boolean isExistsServerName(String name);

    /**
     * Rename server.
     *
     * @param server - the server to be renamed.
     * @throws ObjectNotExistsException - if the server is not exist
     * @throws NameExistsException      - if the name already exist
     */
//    void renameServer(Server server);

    /**
     * Update the server state.
     *
     * @param server - the server to be updated
     * @throws ObjectNotExistsException - if the server is not exist
     */
    void updateState(Server server);

    /**
     * Update the server ip and port.
     *
     * @param server - the server to be updated
     * @throws ObjectNotExistsException - if the server is not exist
     */
//    void updateAddress(Server server);

    /**
     * Update the server online state.
     *
     * @param server - the server to be updated
     * @throws ObjectNotExistsException - if the server is not exist
     */
    void updateOnlineState(Server server);


    /**
     * Remove server.
     *
     * @param server - the server to be removed.
     * @throws ServerBusyException if the server is busy
     */
    void removeServer(Server server);

    void removeServers(List<Server> servers);

    /**
     * Reset the status of all servers.
     */
    void updateServersStatus();

    /**
     * Select a backup server to take over the specified worker server.
     *
     * @param worker - the specified server to be replaced
     * @return the selected backup server.
     * @throws ObjectNotExistsException if the server is not exist.
     * @throws AccessDeniedException    if the server is not allow for this action.
     */
    Server updateWorkerServer(String worker);

}
