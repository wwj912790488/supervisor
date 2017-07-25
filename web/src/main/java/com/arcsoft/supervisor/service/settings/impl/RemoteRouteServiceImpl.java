package com.arcsoft.supervisor.service.settings.impl;


import com.arcsoft.supervisor.cluster.action.settings.network.*;
import com.arcsoft.supervisor.exception.server.RemoteException;
import com.arcsoft.supervisor.model.domain.server.Server;
import com.arcsoft.supervisor.model.domain.settings.Route;
import com.arcsoft.supervisor.service.remote.RemoteExecutorServiceSupport;
import com.arcsoft.supervisor.service.settings.RemoteRouteService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * The implementation of route settings for the specific agent.
 * 
 * @author zw
 */
@Service
public class RemoteRouteServiceImpl extends RemoteExecutorServiceSupport implements RemoteRouteService {

	@Override
	public List<Route> getRoutes(Server agent) {
		ListRouteRequest request = new ListRouteRequest();
		ListRouteResponse response = (ListRouteResponse) remoteExecutorService.remoteExecute(request, agent);
		return response.getRoutes();
	}

	@Override
	public void addRoute(Server agent, Route route) {
		AddRouteRequest request = new AddRouteRequest();
		ArrayList<Route> routes = new ArrayList<>();
		routes.add(route);
		request.setRoutes(routes);
		AddRouteResponse response = (AddRouteResponse) remoteExecutorService.remoteExecute(request, agent);
		if (!response.isSuccess())
			throw new RemoteException(agent);
	}

	@Override
	public void deleteRoute(Server agent, Route route) {
		DeleteRouteRequest request = new DeleteRouteRequest();
		ArrayList<Route> routes = new ArrayList<>();
		routes.add(route);
		request.setRoutes(routes);
		DeleteRouteResponse response = (DeleteRouteResponse) remoteExecutorService.remoteExecute(request, agent);
		if (!response.isSuccess())
			throw new RemoteException(agent);
	}

}
