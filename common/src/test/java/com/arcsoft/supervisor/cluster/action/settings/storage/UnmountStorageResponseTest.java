package com.arcsoft.supervisor.cluster.action.settings.storage;

import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseResponseTest;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

/**
 * Response for unmount storage
 * 
 * @author hxiang
 */
public class UnmountStorageResponseTest extends BaseResponseTest<UnmountStorageResponse> {

	@Test
	public void testRequest() throws IOException {
		UnmountStorageResponse actual = testConverter(Actions.STORAGE_UNMOUNT, new UnmountStorageResponse());
		assertNotNull(actual);
	}
}
