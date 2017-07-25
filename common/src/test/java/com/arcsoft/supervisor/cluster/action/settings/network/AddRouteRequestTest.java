package com.arcsoft.supervisor.cluster.action.settings.network;

import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseRequestTest;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

/**
 * @author hxiang
 *
 */
public class AddRouteRequestTest extends BaseRequestTest<AddRouteRequest> {

	@Test
	public void testRequest() throws IOException {
		AddRouteRequest actual = testConverter(Actions.ROUTE_ADD, new AddRouteRequest());
		assertNotNull(actual);
	}

}
