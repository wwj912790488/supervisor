package com.arcsoft.supervisor.cluster.action.settings.network;

import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseRequestTest;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

/**
 * Test for adding firewall rule request.
 *
 * @author hxiang
 */
public class AddFirewallRequestTest extends BaseRequestTest<AddFirewallRequest> {
	@Test
	public void testRequest() throws IOException {
		AddFirewallRequest actual = testConverter(Actions.FIREWALL_ADD, new AddFirewallRequest());
		assertNotNull(actual);
	}
}
