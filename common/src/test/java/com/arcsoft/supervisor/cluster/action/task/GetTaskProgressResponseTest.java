package com.arcsoft.supervisor.cluster.action.task;

import com.arcsoft.supervisor.cluster.action.ActionErrorCode;
import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseResponseTest;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Test cases for GetTaskProgressResponse.
 * 
 * @author fjli
 */
public class GetTaskProgressResponseTest extends BaseResponseTest<GetTaskProgressResponse> {

	@Test
	public void testConstruct() {
		GetTaskProgressResponse resp = new GetTaskProgressResponse();
		assertEquals(ActionErrorCode.UNKNOWN_ERROR, resp.getErrorCode());
		resp.setErrorCode(ActionErrorCode.SUCCESS);
		assertEquals(ActionErrorCode.SUCCESS, resp.getErrorCode());
	}

	@Test
	public void testResponse() throws IOException {
		GetTaskProgressResponse expect = new GetTaskProgressResponse();
		expect.setXml("<progress>test data</progress>");
		GetTaskProgressResponse actual = testConverter(Actions.GET_TASK_PROGRESS, expect);
		assertEquals(expect.getXml(), actual.getXml());
	}

}
