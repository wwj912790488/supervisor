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
public class DeleteStorageResponseTest extends BaseResponseTest<DeleteStorageResponse> {

	@Test
	public void testRequest() throws IOException {
		DeleteStorageResponse actual = testConverter(Actions.STORAGE_DELETE, new DeleteStorageResponse());
		assertNotNull(actual);
	}
}
