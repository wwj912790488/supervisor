package com.arcsoft.supervisor.cluster.action.settings.network;

import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseRequestTest;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class StatEthRequestTest extends BaseRequestTest<StatEthRequest> {

	@Test
	public void testRequest() throws IOException {
		StatEthRequest expect = new StatEthRequest("eth0");
		StatEthRequest actual = testConverter(Actions.NETWORK_STAT, expect);		
		assertEquals(expect.getEthId(), actual.getEthId());
	}


}
