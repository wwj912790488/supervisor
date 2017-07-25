package com.arcsoft.supervisor.service.settings.impl;

import com.arcsoft.supervisor.cluster.action.settings.host.RebootRequest;
import com.arcsoft.supervisor.cluster.action.settings.host.RebootResponse;
import com.arcsoft.supervisor.cluster.action.settings.host.ShutdownRequest;
import com.arcsoft.supervisor.cluster.action.settings.host.ShutdownResponse;
import com.arcsoft.supervisor.exception.server.RemoteException;
import com.arcsoft.supervisor.model.domain.server.Server;
import com.arcsoft.supervisor.service.remote.RemoteExecutorServiceSupport;
import com.arcsoft.supervisor.service.settings.RemoteHostService;
import org.springframework.stereotype.Service;

/**
 * The implementation of RemoteHostService
 * 
 * @author zw
 */
@Service
public class RemoteHostServiceImpl extends RemoteExecutorServiceSupport implements RemoteHostService {

	@Override
	public void reboot(Server agent) {
		RebootRequest request = new RebootRequest();
		RebootResponse response = (RebootResponse) remoteExecutorService.remoteExecute(request, agent);
		if (!response.isSuccess())
			throw new RemoteException(agent);
	}

	@Override
	public void shutdown(Server agent) {
		ShutdownRequest request = new ShutdownRequest();
		ShutdownResponse response = (ShutdownResponse) remoteExecutorService.remoteExecute(request, agent);
		if (!response.isSuccess())
			throw new RemoteException(agent);
	}

}
