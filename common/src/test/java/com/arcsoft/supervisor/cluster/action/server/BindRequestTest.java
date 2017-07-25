package com.arcsoft.supervisor.cluster.action.server;

import com.arcsoft.supervisor.cluster.NodeType;
import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseRequestTest;
import com.arcsoft.supervisor.cluster.node.NodeDescription;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Test cases for BindRequest.
 * 
 * @author fjli
 */
public class BindRequestTest extends BaseRequestTest<BindRequest> {

	@Test
	public void testRequest() throws IOException {
		NodeDescription desc = new NodeDescription(NodeType.TYPE_LIVE,
				"master_id", "master_name", "127.0.0.1", 5001);
		BindRequest expect = new BindRequest(desc);
		BindRequest actual = testConverter(Actions.LIVE_BIND, expect);
		assertEquals(expect.getMaster().getId(), actual.getMaster().getId());
		assertEquals(expect.getMaster().getIp(), actual.getMaster().getIp());
		assertEquals(expect.getMaster().getPort(), actual.getMaster().getPort());
	}

}
