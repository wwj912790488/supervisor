package com.arcsoft.supervisor.cluster.action.server;

import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseRequestTest;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Test cases for LiveRoleSwitchRequest.
 * 
 * @author fjli
 */
public class LiveRoleSwitchRequestTest extends BaseRequestTest<LiveRoleSwitchRequest> {

	@Test
	public void testRequest() throws IOException {
		LiveRoleSwitchRequest expect = new LiveRoleSwitchRequest("slave_id");
		LiveRoleSwitchRequest actual = testConverter(Actions.LIVE_ROLE_SWITCH_EVENT, expect);
		assertEquals(expect.getId(), actual.getId());
	}

}
