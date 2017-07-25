package com.arcsoft.supervisor.cluster.action.settings.network;

import com.arcsoft.supervisor.cluster.action.ActionErrorCode;
import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseResponseTest;
import org.junit.Test;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Test for stop firewall resonse.
 * 
 * @author hxiang
 */
@XmlRootElement
public class StopFirewallResponseTest extends
		BaseResponseTest<StopFirewallResponse> {

	@Test
	public void testConstruct() {
		StopFirewallResponse resp = new StopFirewallResponse();
		assertEquals(ActionErrorCode.UNKNOWN_ERROR, resp.getErrorCode());
		resp.setErrorCode(ActionErrorCode.SUCCESS);
		assertEquals(ActionErrorCode.SUCCESS, resp.getErrorCode());
	}

	@Test
	public void testResponse() throws IOException {
		testConverter(Actions.FIREWALL_STOP, new StopFirewallResponse());
	}
}
