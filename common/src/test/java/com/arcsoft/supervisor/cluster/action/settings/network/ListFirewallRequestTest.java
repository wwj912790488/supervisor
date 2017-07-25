package com.arcsoft.supervisor.cluster.action.settings.network;

import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseRequestTest;
import org.junit.Test;

import java.io.IOException;

/**
 * Test for listing firewall rule request.
 * 
 * @author hxiang
 */
public class ListFirewallRequestTest extends BaseRequestTest<ListFirewallRequest> {
	
	@Test
	public void testRequest() throws IOException {
		testConverter(Actions.FIREWALL_LIST, new ListFirewallRequest());
	}
}
