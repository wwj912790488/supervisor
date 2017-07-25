/**
 * 
 */
package com.arcsoft.supervisor.cluster.action.settings.network;

import com.arcsoft.supervisor.cluster.action.ActionErrorCode;
import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseResponseTest;
import com.arcsoft.supervisor.model.domain.settings.Route;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author hxiang
 *
 */
public class ListRouteResponseTest extends BaseResponseTest<ListRouteResponse> {
	@Test
	public void testConstruct() {
		ListRouteResponse resp = new ListRouteResponse();
		assertEquals(ActionErrorCode.UNKNOWN_ERROR, resp.getErrorCode());
		resp.setErrorCode(ActionErrorCode.SUCCESS);
		assertEquals(ActionErrorCode.SUCCESS, resp.getErrorCode());
	}

	@Test
	public void testResponse() throws IOException {
		ListRouteResponse expect = new ListRouteResponse();
		List<Route> routes = new ArrayList<Route>();
		routes.add(new Route("127.0.0.1", "255.255.255.0" ,"0.0.0.0", "eth0"));
		expect.setRoutes(routes);
		ListRouteResponse resp = testConverter(Actions.ROUTE_LIST, expect);
		assertNotNull(resp);
		assertEquals(resp.getRoutes().get(0).getDest(), "127.0.0.1");
		assertEquals(resp.getRoutes().get(0).getMask(), "255.255.255.0");
		assertEquals(resp.getRoutes().get(0).getGateway(), "0.0.0.0");
		assertEquals(resp.getRoutes().get(0).getIface(), "eth0");
	}

}
