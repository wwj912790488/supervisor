package com.arcsoft.supervisor.cluster.action.settings.network;

import com.arcsoft.supervisor.cluster.action.BaseResponse;
import com.arcsoft.supervisor.model.domain.settings.Route;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
/**
 * @author hxiang
 * Response for list route
 */
@XmlRootElement
public class ListRouteResponse extends BaseResponse {
	private List<Route> routes = null;
	
	public List<Route> getRoutes() {
		return routes;
	}

	public void setRoutes(List<Route> routes) {
		this.routes = routes;
	}	
}
