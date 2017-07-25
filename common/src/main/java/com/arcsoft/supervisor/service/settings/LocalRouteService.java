package com.arcsoft.supervisor.service.settings;

import com.arcsoft.supervisor.model.domain.settings.Route;

import java.util.List;

/**
 * Local route service.
 * 
 * @author hxiang
 */
public interface LocalRouteService {

	List<Route> getRoutes() throws Exception;

	void addRoute(Route route) throws Exception;

	void deleteRoute(Route route) throws Exception;

}
