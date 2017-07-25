package com.arcsoft.supervisor.cluster.action.settings.host;

import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseRequestTest;
import org.junit.Test;

import java.io.IOException;


/**
 * Test for ShutdownRequest
 *
 * @author hxiang
 */
public class ShutdownRequestTest extends BaseRequestTest<ShutdownRequest> {
	
	@Test
	public void testRequest() throws IOException {
		testConverter(Actions.SYSTEM_SHUTDOWN, new ShutdownRequest());
	}
}
