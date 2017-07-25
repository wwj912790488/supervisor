package com.arcsoft.supervisor.cluster.action.server;

import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseRequestTest;
import org.junit.Test;

import java.io.IOException;

/**
 * Test cases for RemoveAgentRequest.
 * 
 * @author fjli
 */
public class RemoveAgentRequestTest extends BaseRequestTest<RemoveAgentRequest> {

	@Test
	public void testRequest() throws IOException {
		testConverter(Actions.REMOVE_AGENT, new RemoveAgentRequest());
	}

}
