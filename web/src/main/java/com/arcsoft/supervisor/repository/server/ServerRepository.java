package com.arcsoft.supervisor.repository.server;

import com.arcsoft.supervisor.exception.ObjectAlreadyExistsException;
import com.arcsoft.supervisor.exception.ObjectNotExistsException;
import com.arcsoft.supervisor.exception.server.NameExistsException;
import com.arcsoft.supervisor.model.domain.server.Server;
import com.arcsoft.supervisor.model.domain.server.ServerType;

import java.util.List;


/**
 * Server repository.
 * 
 * @author fjli
 * @author zw
 */
public interface ServerRepository{

	/**
	 * Query all servers.
	 */
	List<Server> listAll();

    /**
     * Query all servers with specified  type of server.
     *
     * @param type the type of server
     */
    List<Server> listByType(ServerType type);


	/**
	 * Find the server with the specified id.
	 * 
	 * @param id - the specified server id
	 * @return Returns the server with the specified id, or null if not found.
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
	 * Add server.
	 * 
	 * @param server - the server to be added
	 * @throws ObjectNotExistsException if the group is not exist.
	 * @throws ObjectAlreadyExistsException if the server is already added
	 * @throws NameExistsException if the name already exist
	 */
	void addServer(Server server) throws ObjectNotExistsException, ObjectAlreadyExistsException, NameExistsException;

	/**
	 * Rename server.
	 * 
	 * @param server - the server to be renamed.
	 * @throws ObjectNotExistsException - if the server is not exist
	 * @throws NameExistsException - if the name already exist
	 */
	void renameServer(Server server) throws ObjectNotExistsException, NameExistsException;

	/**
	 * Update the server state.
	 * 
	 * @param server - the server to be updated
	 * @throws ObjectNotExistsException - if the server is not exist
	 */
	void updateState(Server server) throws ObjectNotExistsException;

	/**
	 * Update the server ip and port.
	 * 
	 * @param server - the server to be updated
	 * @throws ObjectNotExistsException - if the server is not exist
	 */
	void updateAddress(Server server) throws ObjectNotExistsException;

	/**
	 * Update the server online state.
	 * 
	 * @param server - the server to be updated
	 * @throws ObjectNotExistsException - if the server is not exist
	 */
	void updateOnlineState(Server server) throws ObjectNotExistsException;


	/**
	 * Remove server.
	 * 
	 * @param server - the server to be removed
	 */
	void removeServer(Server server);

	/**
	 * Reset the status of all servers.
	 */
	void resetServersStatus();

}
