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
public class AddStorageRequestTest extends BaseRequestTest<AddStorageRequest> {

	@Test
	public void testRequest() throws IOException {
		AddStorageRequest actual = testConverter(Actions.STORAGE_ADD, new AddStorageRequest());
		assertNotNull(actual);
	}
}
