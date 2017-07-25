package com.arcsoft.supervisor.cluster.action.settings.network;

import com.arcsoft.supervisor.cluster.action.ActionErrorCode;
import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseResponseTest;
import com.arcsoft.supervisor.model.domain.settings.FirewallRule;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test for listing firewall rule response
 * 
 * @author hxiang
 */
public class ListFirewallResponseTest extends
		BaseResponseTest<ListFirewallResponse> {
	
	@Test
	public void testConstruct() {
		ListFirewallResponse resp = new ListFirewallResponse();
		assertEquals(ActionErrorCode.UNKNOWN_ERROR, resp.getErrorCode());
		resp.setErrorCode(ActionErrorCode.SUCCESS);
		assertEquals(ActionErrorCode.SUCCESS, resp.getErrorCode());
	}

	@Test
	public void testResonse() throws IOException {
		ListFirewallResponse expect = new ListFirewallResponse();
		List<FirewallRule> rules = new ArrayList<FirewallRule>();
		rules.add(new FirewallRule("tcp", "23"));
		expect.setRules(rules);
		ListFirewallResponse resp = testConverter(Actions.FIREWALL_LIST, expect);
		FirewallRule rule = resp.getRules().get(0);
		assertEquals(rule.getProtocol(), "tcp");
		assertEquals(rule.getDport(), "23");
	}
}
