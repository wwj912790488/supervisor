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
public class AddStorageResponseTest extends BaseResponseTest<AddStorageResponse> {

	@Test
	public void testRequest() throws IOException {
		AddStorageResponse actual = testConverter(Actions.STORAGE_ADD, new AddStorageResponse());
		assertNotNull(actual);
	}
}
