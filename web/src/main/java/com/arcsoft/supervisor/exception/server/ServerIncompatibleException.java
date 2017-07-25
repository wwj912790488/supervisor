package com.arcsoft.supervisor.exception.server;


import com.arcsoft.supervisor.exception.ApplicationException;
import com.arcsoft.supervisor.model.domain.server.Server;

/**
 * This is exception will be thrown when the agent server is incompatible.
 * 
 * @author fjli
 */
@SuppressWarnings("serial")
public class ServerIncompatibleException extends ApplicationException {

	private Server server;

	/**
	 * Construct new exception.
	 * 
	 * @param server - the cause server.
	 */
	public ServerIncompatibleException(Server server) {
		this.server = server;
	}

	/**
	 * Returns the cause server.
	 */
	public Server getServer() {
		return server;
	}

}
