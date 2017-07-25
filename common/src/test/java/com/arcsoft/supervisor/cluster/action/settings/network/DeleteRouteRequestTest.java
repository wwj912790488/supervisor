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
public class DeleteRouteRequestTest extends BaseRequestTest<DeleteRouteRequest> {

	@Test
	public void testRequest() throws IOException {
		DeleteRouteRequest actual = testConverter(Actions.ROUTE_DELETE, new DeleteRouteRequest());
		assertNotNull(actual);
	}
}
