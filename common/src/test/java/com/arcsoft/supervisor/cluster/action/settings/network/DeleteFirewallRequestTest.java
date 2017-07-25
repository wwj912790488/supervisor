package com.arcsoft.supervisor.cluster.action.settings.network;

import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseRequestTest;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

/**
 * Test for deleting firewall rule rquest
 *
 * @author xpeng
 */
public class DeleteFirewallRequestTest extends BaseRequestTest<DeleteFirewallRequest> {
	@Test
	public void testRequest() throws IOException {
		DeleteFirewallRequest actual = testConverter(Actions.FIREWALL_DELETE, new DeleteFirewallRequest());
		assertNotNull(actual);
	}
}
