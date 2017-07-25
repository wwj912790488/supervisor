package com.arcsoft.supervisor.service.remote;


import com.arcsoft.supervisor.cluster.action.BaseRequest;
import com.arcsoft.supervisor.cluster.action.BaseResponse;
import com.arcsoft.supervisor.model.domain.server.Server;

/**
 * This service is used to execute the request on the specified agent.
 * 
 * @author fjli
 */
public interface RemoteExecutorService {

	/**
	 * Execute request on the specified agent with default time out.
	 * 
	 * @param request - the request to be executed
	 * @param agent - the specified agent
	 * @return Returns the response.
	 * @throws SystemNotInitializedException if system has not initialized.
	 * @throws AccessDeniedForSlaveException if system is slave.
	 * @throws ServerNotAvailableException if the agent server is not available.
	 * @throws RemoteException if invoke failed.
	 */
	public BaseResponse remoteExecute(BaseRequest request, Server agent);

	/**
	 * Execute request on the specified agent with the specified timeout.
	 * 
	 * @param request - the request to be executed
	 * @param agent - the specified agent
	 * @param connectTimeout - the connection timeout
	 * @param readTimeout - the read timeout
	 * @return Returns the response.
	 * @throws SystemNotInitializedException if system has not initialized.
	 * @throws AccessDeniedForSlaveException if system is slave.
	 * @throws ServerNotAvailableException if the agent server is not available.
	 * @throws RemoteException if invoke failed.
	 */
	public BaseResponse remoteExecute(BaseRequest request, Server agent, int connectTimeout, int readTimeout);

}
