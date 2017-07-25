package com.arcsoft.supervisor.cluster.action.task;

import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseRequestTest;
import com.arcsoft.supervisor.model.vo.task.TaskStateChange;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Test cases for StateChangeRequest.
 * 
 * @author fjli
 */
public class StateChangeRequestTest extends BaseRequestTest<StateChangeRequest> {

	@Test
	public void testRequest() throws IOException {
		StateChangeRequest expect = new StateChangeRequest();
		TaskStateChange state = new TaskStateChange();
		state.setId(1001);
		state.setDate(new Date());
//		state.setPostProcessingTime(80);
//		state.setTranscodingTime(100);
		state.setState("CANCELLED");
		expect.add(state);
		StateChangeRequest actual = testConverter(Actions.TASK_STATE_CHANGE, expect);
		TaskStateChange actualState = actual.getStates().get(0);
		assertEquals(state.getId(), actualState.getId());
		assertEquals(state.getDate(), actualState.getDate());
//		assertEquals(state.getPostProcessingTime(), actualState.getPostProcessingTime());
//		assertEquals(state.getTranscodingTime(), actualState.getTranscodingTime());
		assertEquals(state.getState(), actualState.getState());
	}

}
