package com.arcsoft.supervisor.cluster.action.task;

import com.arcsoft.supervisor.cluster.action.ActionErrorCode;
import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseResponseTest;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Test cases for StopTaskResponse.
 * 
 * @author fjli
 */
public class StopTaskResponseTest extends BaseResponseTest<StopTaskResponse> {

	@Test
	public void testConstruct() {
		StopTaskResponse resp = new StopTaskResponse();
		assertEquals(ActionErrorCode.UNKNOWN_ERROR, resp.getErrorCode());
		resp.setErrorCode(ActionErrorCode.SUCCESS);
		assertEquals(ActionErrorCode.SUCCESS, resp.getErrorCode());
	}

	@Test
	public void testResponse() throws IOException {
		testConverter(Actions.STOP_TASK, new StopTaskResponse());
	}

}
