package com.arcsoft.supervisor.cluster.action.server;

import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseRequestTest;
import org.junit.Test;

import java.io.IOException;

/**
 * Test cases for BindRequest.
 * 
 * @author fjli
 */
public class GetAgentDescRequestTest extends BaseRequestTest<GetAgentDescRequest> {

	@Test
	public void testRequest() throws IOException {
		testConverter(Actions.GET_AGENT_DESC, new GetAgentDescRequest());
	}

}
