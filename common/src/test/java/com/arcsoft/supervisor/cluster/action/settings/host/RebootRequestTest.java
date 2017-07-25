package com.arcsoft.supervisor.cluster.action.settings.host;

import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseRequestTest;
import org.junit.Test;

import java.io.IOException;

/**
 * Test for RebootRequest.
 *
 * @author hxiang
 */
public class RebootRequestTest extends BaseRequestTest<RebootRequest> {
	
	@Test
	public void testRequest() throws IOException {
		testConverter(Actions.SYSTEM_REBOOT, new RebootRequest());
	}
	
}
