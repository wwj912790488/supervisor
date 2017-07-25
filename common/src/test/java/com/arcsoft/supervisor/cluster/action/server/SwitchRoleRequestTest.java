package com.arcsoft.supervisor.cluster.action.server;

import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseRequestTest;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Test cases for SwitchRoleRequest.
 * 
 * @author fjli
 */
public class SwitchRoleRequestTest extends BaseRequestTest<SwitchRoleRequest> {

	@Test
	public void testRequest() throws IOException {
		SwitchRoleRequest expect = new SwitchRoleRequest();
		expect.setReason(RoleSwitchReason.REASON_MASTER_NETWORK_ERROR);
		SwitchRoleRequest actual = testConverter(Actions.LIVE_SWITCH_ROLE, expect);
		assertEquals(expect.getReason(), actual.getReason());
	}

}
