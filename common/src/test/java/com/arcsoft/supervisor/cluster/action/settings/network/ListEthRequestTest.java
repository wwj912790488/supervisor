package com.arcsoft.supervisor.cluster.action.settings.network;

import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseRequestTest;
import org.junit.Test;

import java.io.IOException;

/**
 * Test cases for ListEthRequest.
 * 
 * @author fjli
 */
public class ListEthRequestTest extends BaseRequestTest<ListEthRequest> {

	@Test
	public void testRequest() throws IOException {
		testConverter(Actions.NETWORK_LIST, new ListEthRequest());
	}

}
