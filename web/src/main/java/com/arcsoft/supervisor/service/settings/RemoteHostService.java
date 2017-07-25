package com.arcsoft.supervisor.service.settings;


import com.arcsoft.supervisor.model.domain.server.Server;

/**
 * Service for remote host setting
 * 
 * @author zw
 */
public interface RemoteHostService {

	/**
	 * Reboot the specified agent.
	 * 
	 * @param agent - the specified agent.
	 * @throws SystemNotInitializedException if system has not initialized.
	 * @throws AccessDeniedForSlaveException if system is slave.
	 * @throws ServerNotAvailableException if service in not available.
	 * @throws RemoteException if execute request failed.
	 */
	void reboot(Server agent);

	/**
	 * Shutdown the specified agent.
	 * 
	 * @param agent - the specified agent.
	 * @throws SystemNotInitializedException if system has not initialized.
	 * @throws AccessDeniedForSlaveException if system is slave.
	 * @throws ServerNotAvailableException if service in not available.
	 * @throws RemoteException if execute request failed.
	 */
	void shutdown(Server agent);

}
