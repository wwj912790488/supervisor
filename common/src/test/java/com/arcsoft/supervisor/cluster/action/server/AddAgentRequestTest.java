package com.arcsoft.supervisor.cluster.action.server;

import com.arcsoft.supervisor.cluster.NodeType;
import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseRequestTest;
import com.arcsoft.supervisor.cluster.node.NodeDescription;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Test cases for AddAgentRequest.
 * 
 * @author fjli
 */
public class AddAgentRequestTest extends BaseRequestTest<AddAgentRequest> {

	@Test
	public void testRequest() throws IOException {
		NodeDescription desc = new NodeDescription(NodeType.TYPE_COMMANDER,
				"commander_id", "commander_name", "127.0.0.1", 5001);
		AddAgentRequest expect = new AddAgentRequest(desc);
		AddAgentRequest actual = testConverter(Actions.ADD_AGENT, expect);
		assertEquals(expect.getCommander().getId(), actual.getCommander().getId());
		assertEquals(expect.getCommander().getIp(), actual.getCommander().getIp());
		assertEquals(expect.getCommander().getPort(), actual.getCommander().getPort());
	}

}
