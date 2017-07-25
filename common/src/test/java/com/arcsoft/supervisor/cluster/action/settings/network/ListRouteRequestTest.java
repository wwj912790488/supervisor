package com.arcsoft.supervisor.cluster.action.settings.network;

import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseRequestTest;
import org.junit.Test;

import java.io.IOException;

public class ListRouteRequestTest extends BaseRequestTest<ListRouteRequest> {

	@Test
	public void testRequest() throws IOException {
		testConverter(Actions.ROUTE_LIST, new ListRouteRequest());
	}
}
