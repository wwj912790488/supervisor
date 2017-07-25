package com.arcsoft.supervisor.cluster.action.settings.storage;

import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseRequestTest;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

/**
 * 
 * @author hxiang
 */
public class FindRemoteMountedStorageRequestTest extends BaseRequestTest<FindRemoteMountedStorageRequest> {

	@Test
	public void testRequest() throws IOException {
		FindRemoteMountedStorageRequest actual = testConverter(Actions.STORAGE_FIND_REMOTE_MOUNTED,
				new FindRemoteMountedStorageRequest());
		assertNotNull(actual);
	}
}
