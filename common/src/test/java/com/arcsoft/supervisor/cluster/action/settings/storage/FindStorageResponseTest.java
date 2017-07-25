package com.arcsoft.supervisor.cluster.action.settings.storage;

import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseResponseTest;
import org.junit.Test;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;

import static org.junit.Assert.assertNotNull;

/**
 * 
 * @author hxiang
 */

@XmlRootElement
public class FindStorageResponseTest extends BaseResponseTest<FindStorageResponse> {

	@Test
	public void testRequest() throws IOException {
		FindStorageResponse actual = testConverter(Actions.STORAGE_FIND, new FindStorageResponse());
		assertNotNull(actual);
	}
}
