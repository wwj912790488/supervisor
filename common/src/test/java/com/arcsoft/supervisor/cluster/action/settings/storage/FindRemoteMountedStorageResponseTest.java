package com.arcsoft.supervisor.cluster.action.settings.storage;

import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseResponseTest;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;


/**
 * 
 * @author hxiang
 */
public class FindRemoteMountedStorageResponseTest extends BaseResponseTest<FindRemoteMountedStorageResponse> {

	@Test
	public void testRequest() throws IOException {
		FindRemoteMountedStorageResponse actual = testConverter(Actions.STORAGE_FIND_REMOTE_MOUNTED,
				new FindRemoteMountedStorageResponse());
		assertNotNull(actual);
	}
}
