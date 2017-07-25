package com.arcsoft.supervisor.cluster.action.server;

import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseRequestTest;
import org.junit.Test;

import java.io.IOException;

/**
 * Test cases for GroupUnbindRequest.
 * 
 * @author fjli
 */
public class UnbindRequestTest extends BaseRequestTest<UnbindRequest> {

	@Test
	public void testRequest() throws IOException {
		testConverter(Actions.LIVE_UNBIND, new UnbindRequest());
	}

}
