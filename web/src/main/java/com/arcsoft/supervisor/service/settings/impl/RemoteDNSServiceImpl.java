package com.arcsoft.supervisor.service.settings.impl;



import com.arcsoft.supervisor.cluster.action.settings.network.*;
import com.arcsoft.supervisor.exception.server.RemoteException;
import com.arcsoft.supervisor.model.domain.server.Server;
import com.arcsoft.supervisor.model.domain.settings.DNS;
import com.arcsoft.supervisor.service.remote.RemoteExecutorServiceSupport;
import com.arcsoft.supervisor.service.settings.RemoteDNSService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * This service process DNS settings for the specified agent.
 * 
 * @author zw
 */
@Service
public class RemoteDNSServiceImpl extends RemoteExecutorServiceSupport implements RemoteDNSService {

	@Override
	public List<DNS> getDnsList(Server agent) {
		ListDNSResponse response = (ListDNSResponse) remoteExecutorService.remoteExecute(new ListDNSRequest(), agent);
		return response.getDnsList();
	}

	@Override
	public void addDns(Server agent, DNS dns) {
		AddDNSRequest request = new AddDNSRequest();
		ArrayList<DNS> dnsList = new ArrayList<DNS>();
		dnsList.add(dns);
		request.setDNSList(dnsList);
		AddDNSResponse response = (AddDNSResponse) remoteExecutorService.remoteExecute(request, agent);
		if (!response.isSuccess())
			throw new RemoteException(agent);
	}

	@Override
	public void deleteDns(Server agent, DNS dns) {
		DeleteDNSRequest request = new DeleteDNSRequest();
		ArrayList<DNS> dnsList = new ArrayList<DNS>();
		dnsList.add(dns);
		request.setDNSList(dnsList);
		DeleteDNSResponse response = (DeleteDNSResponse) remoteExecutorService.remoteExecute(request, agent);
		if (!response.isSuccess())
			throw new RemoteException(agent);
	}

}
