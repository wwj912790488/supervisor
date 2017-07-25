package com.arcsoft.supervisor.service.settings.impl;

import com.arcsoft.supervisor.model.domain.settings.Route;
import com.arcsoft.supervisor.repository.settings.RouteRepository;
import com.arcsoft.supervisor.service.settings.LocalRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The implementation of LocalRouteService.
 * 
 * @author hxiang
 * @author zw
 */
@Service
public class LocalRouteServiceImpl implements LocalRouteService {

    @Autowired
	private RouteRepository routeRepository;

    public void setRouteRepository(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    @Override
	public List<Route> getRoutes() throws Exception {
		return routeRepository.getRoutes();
	}

	@Override
	public void addRoute(Route route) throws Exception {
		routeRepository.add(route);
	}

	@Override
	public void deleteRoute(Route route) throws Exception {
		routeRepository.delete(route);
	}

}
