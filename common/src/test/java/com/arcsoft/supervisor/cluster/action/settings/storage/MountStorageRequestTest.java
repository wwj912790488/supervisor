package com.arcsoft.supervisor.cluster.action.settings.storage;

import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseRequestTest;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

/**
 * Request for mount storage
 * 
 * @author hxiang
 */
public class MountStorageRequestTest extends BaseRequestTest<MountStorageRequest> {

	@Test
	public void testRequest() throws IOException {
		MountStorageRequest actual = testConverter(Actions.STORAGE_MOUNT, new MountStorageRequest());
		assertNotNull(actual);
	}

}
