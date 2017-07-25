package com.arcsoft.supervisor.cluster.action.settings.network;

import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseRequestTest;
import org.junit.Test;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Test for start firewall request.
 *
 * @author hxiang
 */
@XmlRootElement
public class StartFirewallRequestTest extends BaseRequestTest<StartFirewallRequest> {
	@Test
	public void testRequest() throws IOException {
		StartFirewallRequest expect = new StartFirewallRequest();
		StartFirewallRequest actual = testConverter(Actions.FIREWALL_START, expect);		
		assertTrue(actual != null);
	}
}
