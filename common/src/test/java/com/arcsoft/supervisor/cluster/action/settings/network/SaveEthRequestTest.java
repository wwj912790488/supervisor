package com.arcsoft.supervisor.cluster.action.settings.network;

import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseRequestTest;
import com.arcsoft.supervisor.model.domain.settings.Eth;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Test cases for SaveEthRequest.
 * 
 * @author fjli
 */
public class SaveEthRequestTest extends BaseRequestTest<SaveEthRequest> {

	@Test
	public void testRequest() throws IOException {
		SaveEthRequest expect = new SaveEthRequest(new Eth("eth0"));
		SaveEthRequest actual = testConverter(Actions.NETWORK_SAVE, expect);		
		assertEquals(expect.getEth().getId(), actual.getEth().getId());
	}

}
