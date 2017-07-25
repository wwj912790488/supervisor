package com.arcsoft.supervisor.cluster.action.settings.network;

import com.arcsoft.supervisor.cluster.action.ActionErrorCode;
import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseResponseTest;
import com.arcsoft.supervisor.model.domain.settings.DNS;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ListDNSResponseTest extends BaseResponseTest<ListDNSResponse>{

	@Test
	public void testConstruct() {
		ListDNSResponse resp = new ListDNSResponse();
		assertEquals(ActionErrorCode.UNKNOWN_ERROR, resp.getErrorCode());
		resp.setErrorCode(ActionErrorCode.SUCCESS);
		assertEquals(ActionErrorCode.SUCCESS, resp.getErrorCode());
	}
	
	@Test
	public void testResonse() throws IOException {
		ListDNSResponse expect = new ListDNSResponse();
		List<DNS> dnsList = new ArrayList<DNS>();
		dnsList.add(new DNS("127.0.0.1"));
		expect.setDnsList(dnsList);
		ListDNSResponse resp = testConverter(Actions.DNS_LIST, expect);
		DNS dns = resp.getDnsList().get(0);
		assertEquals(dns.toString(), "127.0.0.1");
	}
}
