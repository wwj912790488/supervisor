package com.arcsoft.supervisor.cluster.action.settings.network;

import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseRequestTest;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

public class ListDNSRequestTest extends BaseRequestTest<ListDNSRequest> {

	@Test
	public void testRequest() throws IOException {
		ListDNSRequest expect = new ListDNSRequest();
		ListDNSRequest actual = testConverter(Actions.DNS_LIST, expect);
		assertNotNull(actual);
	}

}
