package com.arcsoft.supervisor.cluster.action.settings.storage;

import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseRequestTest;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;


/**
 * Request for unmount storage
 * 
 * @author hxiang
 */
public class UnmountStorageRequestTest extends BaseRequestTest<UnmountStorageRequest> {

	@Test
	public void testRequest() throws IOException {
		UnmountStorageRequest actual = testConverter(Actions.STORAGE_UNMOUNT, new UnmountStorageRequest());
		assertNotNull(actual);
	}
}
