package com.arcsoft.supervisor.service.settings;


import com.arcsoft.supervisor.model.domain.server.Server;
import com.arcsoft.supervisor.model.domain.settings.Eth;

import java.util.List;

/**
 * Service for network operation of remote machine.
 * 
 * @author zw
 */
public interface RemoteEthService {

	/**
	 * Get all valid network interfaces information of the specified agent without slave network interfaces.
	 * 
	 * @param agent - the specified agent
	 * @return all valid network interfaces.
	 * @throws SystemNotInitializedException if system has not initialized.
	 * @throws AccessDeniedForSlaveException if system is slave.
	 * @throws ServerNotAvailableException if the server is not available.
	 * @throws RemoteException if the server can not get the eth list.
	 */
	List<Eth> getValidEths(Server agent);

	/**
	 * List the eths of specific agent.
	 * 
	 * @param agent - the specific agent
	 * @return the eth list
	 * @throws SystemNotInitializedException if system has not initialized.
	 * @throws AccessDeniedForSlaveException if system is slave.
	 * @throws ServerNotAvailableException if the server is not available.
	 * @throws RemoteException if the server can not get the eth list.
	 */
	List<Eth> findAllEths(Server agent);

	/**
	 * Update the eth of specific agent.
	 * 
	 * @param agent - the specific agent
	 * @param eth - the specific eth
	 * @throws SystemNotInitializedException if system has not initialized.
	 * @throws AccessDeniedForSlaveException if system is slave.
	 * @throws ServerNotAvailableException if the server is not available.
	 * @throws RemoteException if the server cannot update the eth information.
	 */
	void updateEth(Server agent, Eth eth);

	/**
	 * Get the specific eth's used rate
	 * 
	 * @param agent - the specific agent
	 * @param ethId - the specific eth's id
	 * @return (val*10000)
	 * @throws SystemNotInitializedException if system has not initialized.
	 * @throws AccessDeniedForSlaveException if system is slave.
	 * @throws ServerNotAvailableException if the server is not available.
	 * @throws RemoteException if the server cannot update the eth information.
	 */
	int getEthUsedRate(Server agent, String ethId);

	/**
	 * Bond two eths, or update eth of the specific agent
	 * <p>
	 * Suppose: 1) only two eths in all can be bond; 2) two eths bond as "bon0"
	 * 
	 * @param agent - the specific agent
	 * @param eth - the eth to be bond or update
	 * @param slaveId - the other eth id to be bond, it can be null.
	 * @throws SystemNotInitializedException if system has not initialized.
	 * @throws AccessDeniedForSlaveException if system is slave.
	 * @throws ServerNotAvailableException if the server is not available。
	 * @throws RemoteException if the server cannot get timezone。
	 */
	void bondAndUpdateEth(Server agent, Eth eth, String slaveId);

}
