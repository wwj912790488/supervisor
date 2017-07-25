package com.arcsoft.supervisor.cluster.action.settings.network;

import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseRequestTest;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

/**
 * @author hxiang
 *
 */
public class DeleteDNSRequestTest extends BaseRequestTest<DeleteDNSRequest> {

	@Test
	public void testRequest() throws IOException {
		DeleteDNSRequest actual = testConverter(Actions.DNS_DELETE, new DeleteDNSRequest());
		assertNotNull(actual);
	}
}
