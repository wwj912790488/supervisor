package com.arcsoft.supervisor.cluster.action.settings.network;

import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseRequestTest;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

/**
 * Test for adding dns request.
 * 
 * @author hxiang
 */
public class AddDNSRequestTest extends BaseRequestTest<AddDNSRequest> {
	@Test
	public void testRequest() throws IOException {
		AddDNSRequest actual = testConverter(Actions.DNS_ADD, new AddDNSRequest());
		assertNotNull(actual);
	}
}
