package com.arcsoft.supervisor.service.server;

import com.arcsoft.supervisor.model.domain.server.Server;

import java.util.EventListener;



/**
 * Server event listener.
 * 
 * @author fjli
 */
public interface ServerEventListener extends EventListener {
	
	/**
	 * Notify listener when server is removed from group
	 * 
	 * @param server  - the changed server
	 */
	void onServerRemoved(Server server);
	
	
	/**
	 * Notify listener when server state changed
	 * 
	 * @param server  - the changed server
	 */
	void onServerStateChange(Server server);
	
	/**
	 * Notify listener when server limited changed
	 * 
	 * @param server  - the changed server
	 */
	void onServerLimitedChange(Server server);
	

}
