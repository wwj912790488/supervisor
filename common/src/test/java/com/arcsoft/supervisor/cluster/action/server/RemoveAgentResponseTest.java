package com.arcsoft.supervisor.cluster.action.server;

import com.arcsoft.supervisor.cluster.action.ActionErrorCode;
import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseResponseTest;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Test cases for RemoveAgentResponse.
 * 
 * @author fjli
 */
public class RemoveAgentResponseTest extends BaseResponseTest<RemoveAgentResponse> {

	@Test
	public void testConstruct() {
		RemoveAgentResponse resp = new RemoveAgentResponse();
		assertEquals(ActionErrorCode.UNKNOWN_ERROR, resp.getErrorCode());
		resp.setErrorCode(ActionErrorCode.SUCCESS);
		assertEquals(ActionErrorCode.SUCCESS, resp.getErrorCode());
	}

	@Test
	public void testResponse() throws IOException {
		testConverter(Actions.REMOVE_AGENT, new RemoveAgentResponse());
	}

}
