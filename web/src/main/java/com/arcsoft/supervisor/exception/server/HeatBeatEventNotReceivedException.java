package com.arcsoft.supervisor.exception.server;


import com.arcsoft.supervisor.exception.ApplicationException;
import com.arcsoft.supervisor.model.domain.server.Server;

/**
 * This exception will be raised if the heat beat event is not received after start heart beat session.
 * 
 * @author fjli
 */
@SuppressWarnings("serial")
public class HeatBeatEventNotReceivedException extends ApplicationException {

	private Server server;

	/**
	 * Construct a HeatBeatEventNotReceivedException.
	 * 
	 * @param server - the cause server.
	 */
	public HeatBeatEventNotReceivedException(Server server) {
		this.server = server;
	}

	/**
	 * Returns the cause server.
	 */
	public Server getServer() {
		return server;
	}

}
