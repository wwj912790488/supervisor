package com.arcsoft.supervisor.cluster.action.task;

import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseRequestTest;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Test cases for GetTaskThumbnailRequest.
 * 
 * @author fjli
 */
public class GetTaskThumbnailRequestTest extends BaseRequestTest<GetTaskThumbnailRequest> {

	@Test
	public void testRequest() throws IOException {
		GetTaskThumbnailRequest expect = new GetTaskThumbnailRequest();
		expect.setId(1001);
		expect.setWidth(128);
		GetTaskThumbnailRequest actual = testConverter(Actions.GET_TASK_THUMBNAIL, expect);
		assertEquals(expect.getId(), actual.getId());
		assertEquals(expect.getWidth(), actual.getWidth());
	}

}
