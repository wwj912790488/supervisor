package com.arcsoft.supervisor.cluster.action.settings.network;

import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseRequestTest;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Test for StopFirewallRequest.
 *
 * @author hxiang
 */

public class StopFirewallRequestTest extends BaseRequestTest<StopFirewallRequest> {
	@Test
	public void testRequest() throws IOException {
		StopFirewallRequest expect = new StopFirewallRequest();
		StopFirewallRequest actual = testConverter(Actions.FIREWALL_STOP, expect);		
		assertTrue(actual != null);
	}

}
