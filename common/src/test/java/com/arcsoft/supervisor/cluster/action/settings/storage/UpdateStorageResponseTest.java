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
public class UpdateStorageResponseTest extends BaseResponseTest<UpdateStorageResponse> {

	@Test
	public void testRequest() throws IOException {
		UpdateStorageResponse actual = testConverter(Actions.STORAGE_UPDATE, new UpdateStorageResponse());
		assertNotNull(actual);
	}
}
