package com.arcsoft.supervisor.cluster.action.settings.time;

import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseResponseTest;
import com.arcsoft.supervisor.model.domain.settings.NTPStatus;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class getNTPResponseTest extends BaseResponseTest<GetNTPResponse> {

	@Test
	public void testResponse() throws IOException {
		List<String> servers = new ArrayList<String>();
		servers.add("172.17.186.90");
		servers.add("172.17.186.91");
		NTPStatus ntp = new NTPStatus(true, servers);

		GetNTPResponse expect = new GetNTPResponse();
		expect.setNtpStatus(ntp);
		GetNTPResponse actual = testConverter(Actions.SYSTEM_GET_NTP, expect);
		assertTrue(actual.getNtpStatus().getIsServiceOn());
		assertEquals(2, actual.getNtpStatus().getNtpServers().size());
		assertEquals(servers.get(0), actual.getNtpStatus().getNtpServers().get(0));
		assertEquals(servers.get(1), actual.getNtpStatus().getNtpServers().get(1));
	}

}
