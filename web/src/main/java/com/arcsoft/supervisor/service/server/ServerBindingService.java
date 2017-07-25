package com.arcsoft.supervisor.service.server;

import com.arcsoft.supervisor.cluster.action.BaseResponse;
import com.arcsoft.supervisor.exception.server.HeatBeatEventNotReceivedException;
import com.arcsoft.supervisor.exception.server.RemoteException;
import com.arcsoft.supervisor.exception.server.ServerNotAvailableException;
import com.arcsoft.supervisor.exception.system.AccessDeniedForSlaveException;
import com.arcsoft.supervisor.exception.system.SystemNotInitializedException;
import com.arcsoft.supervisor.model.domain.server.Server;
import com.arcsoft.supervisor.model.vo.server.AgentDesc;

/**
 * Server binding service.
 * 
 * @author fjli
 */
public interface ServerBindingService {

	/**
	 * Get the version, license and ability of the specified agent.
	 * 
	 * @param agent - the specified agent
	 * @return the agent description
	 * @throws SystemNotInitializedException if system has not initialized.
	 * @throws AccessDeniedForSlaveException if system is slave.
	 * @throws ServerNotAvailableException if the server is not available.
	 * @throws RemoteException if invoke failed in agent side.
	 */
	AgentDesc getAgentDesc(Server agent);

	/**
	 * Add the specified agent into this commander control.
	 * 
	 * @param agent - the specified agent
	 * @throws SystemNotInitializedException if system has not initialized.
	 * @throws AccessDeniedForSlaveException if system is slave.
	 * @throws ServerNotAvailableException if the server is not available.
	 * @throws RemoteException if invoke failed in agent side.
	 * @throws HeatBeatEventNotReceivedException if heart beat event is not received.
	 */
	BaseResponse addAgent(Server agent);

	/**
	 * Remote the specified agent from this commander control.
	 * 
	 * @param agent - the specified agent
	 * @throws SystemNotInitializedException if system has not initialized.
	 * @throws AccessDeniedForSlaveException if system is slave.
	 * @throws ServerNotAvailableException if the server is not available.
	 * @throws RemoteException if invoke failed in agent side.
	 */
	void removeAgent(Server agent);

	/**
	 * Bind the master and slave in 1+1 live group.
	 * 
	 * @param master - the master live agent
	 * @param slave - the slave live agent
	 * @throws SystemNotInitializedException if system has not initialized.
	 * @throws AccessDeniedForSlaveException if system is slave.
	 * @throws ServerNotAvailableException if the server is not available.
	 * @throws RemoteException if invoke failed in agent side.
	 */
	void bind(Server master, Server slave);

	/**
	 * Unbind the specified server with the bound server.
	 * 
	 * @param agent - the specified agent, either master or slave.
	 * @throws SystemNotInitializedException if system has not initialized.
	 * @throws AccessDeniedForSlaveException if system is slave.
	 * @throws ServerNotAvailableException - if the server is not available.
	 * @throws RemoteException if invoke failed in agent side.
	 */
	void unbind(Server agent);

	/**
	 * Notify master and slave to switch role.
	 * 
	 * @param master - the master server in 1+1 backup group
	 * @throws SystemNotInitializedException if system has not initialized.
	 * @throws AccessDeniedForSlaveException if system is slave.
	 * @throws ServerNotAvailableException if the server is not available.
	 * @throws RemoteException if invoke failed in agent side
	 */
	void switchRole(Server master);

}
