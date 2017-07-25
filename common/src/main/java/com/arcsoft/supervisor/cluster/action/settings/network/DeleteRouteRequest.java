package com.arcsoft.supervisor.cluster.action.settings.network;

import com.arcsoft.supervisor.cluster.action.BaseRequest;
import com.arcsoft.supervisor.model.domain.settings.Route;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * @author hxiang
 *
 */
@XmlRootElement
public class DeleteRouteRequest extends BaseRequest {
	private List<Route> routes = null;
	
	public DeleteRouteRequest() {
		
	}
	
	public DeleteRouteRequest(List<Route> route) {
		this.routes = route;
	}

	public List<Route> getRoutes() {
		return routes;
	}

	public void setRoutes(List<Route> routes) {
		this.routes = routes;
	}	
}
