package com.arcsoft.supervisor.cluster.action.settings.storage;

import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseResponseTest;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;


/**
 * Request for mount storage
 * 
 * @author hxiang
 */
public class MountStorageResponseTest extends BaseResponseTest<MountStorageResponse> {

	@Test
	public void testRequest() throws IOException {
		MountStorageResponse actual = testConverter(Actions.STORAGE_MOUNT, new MountStorageResponse());
		assertNotNull(actual);
	}
}
