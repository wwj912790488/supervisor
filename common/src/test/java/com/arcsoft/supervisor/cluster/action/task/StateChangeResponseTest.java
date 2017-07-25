package com.arcsoft.supervisor.cluster.action.task;

import com.arcsoft.supervisor.cluster.action.ActionErrorCode;
import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseResponseTest;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Test cases for StateChangeResponse.
 * 
 * @author fjli
 */
public class StateChangeResponseTest extends BaseResponseTest<StateChangeResponse> {

	@Test
	public void testConstruct() {
		StartTaskResponse resp = new StartTaskResponse();
		assertEquals(ActionErrorCode.UNKNOWN_ERROR, resp.getErrorCode());
		resp.setErrorCode(ActionErrorCode.SUCCESS);
		assertEquals(ActionErrorCode.SUCCESS, resp.getErrorCode());
	}

	@Test
	public void testResponse() throws IOException {
		testConverter(Actions.TASK_STATE_CHANGE, new StateChangeResponse());
	}

}
