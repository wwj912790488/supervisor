package com.arcsoft.supervisor.service.settings;



import com.arcsoft.supervisor.model.domain.server.Server;
import com.arcsoft.supervisor.model.domain.settings.DNS;

import java.util.List;

/**
 * Remote DNS service.
 * 
 * @author zw
 */
public interface RemoteDNSService {

	/**
	 * Get the DNS list from the specified agent.
	 * 
	 * @param agent - the specified agent
	 * @return the DNS list.
	 * @throws SystemNotInitializedException if system has not initialized.
	 * @throws AccessDeniedForSlaveException if system is slave.
	 * @throws ServerNotAvailableException if service in not available.
	 * @throws RemoteException if execute request failed.
	 */
	List<DNS> getDnsList(Server agent);

	/**
	 * Add DNS to the specified agent.
	 * 
	 * @param agent - the specified agent
	 * @param dns - the DNS to be added
	 * @throws SystemNotInitializedException if system has not initialized.
	 * @throws AccessDeniedForSlaveException if system is slave.
	 * @throws ServerNotAvailableException if service in not available.
	 * @throws RemoteException if execute request failed.
	 */
	void addDns(Server agent, DNS dns);

	/**
	 * Delete DNS from the specified agent.
	 * 
	 * @param agent - the specified agent
	 * @param dns - the DNS to be deleted
	 * @throws SystemNotInitializedException if system has not initialized.
	 * @throws AccessDeniedForSlaveException if system is slave.
	 * @throws ServerNotAvailableException if service in not available.
	 * @throws RemoteException if execute request failed.
	 */
	void deleteDns(Server agent, DNS dns);

}
