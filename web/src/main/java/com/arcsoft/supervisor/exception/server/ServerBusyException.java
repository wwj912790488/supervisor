package com.arcsoft.supervisor.exception.server;


import com.arcsoft.supervisor.exception.ApplicationException;
import com.arcsoft.supervisor.model.domain.server.Server;

/**
 * This is exception will be thrown when delete a server which is busy.
 * 
 * @author fjli
 */
@SuppressWarnings("serial")
public class ServerBusyException extends ApplicationException {

	private Server server;

	/**
	 * Construct a ServerBusyException.
	 * 
	 * @param server - the cause server.
	 */
	public ServerBusyException(Server server) {
		this.server = server;
	}

	/**
	 * Returns the cause server.
	 */
	public Server getServer() {
		return server;
	}

}
