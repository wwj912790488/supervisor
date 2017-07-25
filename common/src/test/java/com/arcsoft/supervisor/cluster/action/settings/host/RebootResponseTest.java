package com.arcsoft.supervisor.cluster.action.settings.host;

import com.arcsoft.supervisor.cluster.action.ActionErrorCode;
import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseResponseTest;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Test for RebootResponse.
 *
 * @author hxiang
 */
public class RebootResponseTest extends BaseResponseTest<RebootResponse> {
	
	@Test
	public void testConstruct() {
		RebootResponse resp = new RebootResponse();
		assertEquals(ActionErrorCode.UNKNOWN_ERROR, resp.getErrorCode());
		resp.setErrorCode(ActionErrorCode.SUCCESS);
		assertEquals(ActionErrorCode.SUCCESS, resp.getErrorCode());
	}

	@Test
	public void testResponse() throws IOException {
		testConverter(Actions.SYSTEM_REBOOT, new RebootResponse());
	}
}
