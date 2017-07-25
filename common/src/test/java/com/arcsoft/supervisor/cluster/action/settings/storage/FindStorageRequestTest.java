package com.arcsoft.supervisor.cluster.action.settings.storage;

import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseRequestTest;
import org.junit.Test;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;

import static org.junit.Assert.assertNotNull;

/**
 * 
 * @author hxiang
 */
@XmlRootElement
public class FindStorageRequestTest extends BaseRequestTest<FindStorageRequest> {

	@Test
	public void testRequest() throws IOException {
		FindStorageRequest actual = testConverter(Actions.STORAGE_FIND,
				new FindStorageRequest());
		assertNotNull(actual);
	}
}
