package com.arcsoft.supervisor.cluster.action.task;

import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseRequestTest;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Test cases for StopTaskRequest.
 * 
 * @author fjli
 */
public class StopTaskRequestTest extends BaseRequestTest<StopTaskRequest> {

	@Test
	public void testRequest() throws IOException {
		StopTaskRequest expect = new StopTaskRequest();
		expect.setIds(Arrays.asList(1001));
		StopTaskRequest actual = testConverter(Actions.STOP_TASK, expect);
		assertEquals(expect.getIds().get(0), actual.getIds().get(0));
	}

}
