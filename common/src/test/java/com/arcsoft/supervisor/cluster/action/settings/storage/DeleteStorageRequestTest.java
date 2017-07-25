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
public class DeleteStorageRequestTest extends BaseRequestTest<DeleteStorageRequest>{

	@Test
	public void testRequest() throws IOException {
		DeleteStorageRequest actual = testConverter(Actions.STORAGE_DELETE, new DeleteStorageRequest());
		assertNotNull(actual);
	}

}
