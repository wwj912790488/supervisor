package com.arcsoft.supervisor.cluster.action.server;

import com.arcsoft.supervisor.cluster.action.ActionErrorCode;
import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseResponseTest;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Test cases for AddAgentResponse.
 * 
 * @author fjli
 */
public class AddAgentResponseTest extends BaseResponseTest<AddAgentResponse> {

	@Test
	public void testConstruct() {
		AddAgentResponse resp = new AddAgentResponse();
		assertEquals(ActionErrorCode.UNKNOWN_ERROR, resp.getErrorCode());
		resp.setErrorCode(ActionErrorCode.SUCCESS);
		assertEquals(ActionErrorCode.SUCCESS, resp.getErrorCode());
	}

	@Test
	public void testResponse() throws IOException {
		testConverter(Actions.ADD_AGENT, new AddAgentResponse());
	}

}
