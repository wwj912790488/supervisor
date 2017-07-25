package com.arcsoft.supervisor.cluster.action.settings.network;

import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseRequestTest;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Test for get firewall status request.
 *
 * @author hxiang
 */
public class GetFirewallStatusRequestTest extends BaseRequestTest<GetFirewallStatusRequest>{
	
	@Test
	public void testRequest() throws IOException {
		GetFirewallStatusRequest expect = new GetFirewallStatusRequest();
		GetFirewallStatusRequest actual = testConverter(Actions.FIREWALL_GET_STATUS, expect);		
		assertTrue(actual != null);
	}
}
