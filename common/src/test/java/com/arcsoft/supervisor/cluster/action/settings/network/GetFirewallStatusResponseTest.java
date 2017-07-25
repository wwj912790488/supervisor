package com.arcsoft.supervisor.cluster.action.settings.network;

import com.arcsoft.supervisor.cluster.action.ActionErrorCode;
import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseResponseTest;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Test for get firewall status response.
 *
 * @author hxiang
 */
public class GetFirewallStatusResponseTest extends BaseResponseTest<GetFirewallStatusResponse>{
	@Test
	public void testConstruct() {
		GetFirewallStatusResponse resp = new GetFirewallStatusResponse();
		assertEquals(ActionErrorCode.UNKNOWN_ERROR, resp.getErrorCode());
		resp.setErrorCode(ActionErrorCode.SUCCESS);
		assertEquals(ActionErrorCode.SUCCESS, resp.getErrorCode());
	}

	@Test
	public void testResponse() throws IOException {
		testConverter(Actions.FIREWALL_GET_STATUS, new GetFirewallStatusResponse());
	}
}
