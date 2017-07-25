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
public class UpdateStorageRequestTest extends BaseRequestTest<UpdateStorageRequest> {
	@Test
	public void testRequest() throws IOException {
		UpdateStorageRequest actual = testConverter(Actions.STORAGE_UPDATE, new UpdateStorageRequest());
		assertNotNull(actual);
	}
}
