package com.arcsoft.supervisor.cluster.action.task;

import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseRequestTest;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Test cases for GetTaskProgressRequest.
 * 
 * @author fjli
 */
public class GetTaskProgressRequestTest extends BaseRequestTest<GetTaskProgressRequest> {

	@Test
	public void testRequest() throws IOException {
		GetTaskProgressRequest expect = new GetTaskProgressRequest();
		expect.setId(1001);
		expect.setFilters(new byte[]{0x01, 0x02});
		GetTaskProgressRequest actual = testConverter(Actions.GET_TASK_PROGRESS, expect);
		assertEquals(expect.getId(), actual.getId());
		assertArrayEquals(expect.getFilters(), actual.getFilters());
	}

}
