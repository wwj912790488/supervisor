package com.arcsoft.supervisor.cluster.action;

import com.arcsoft.supervisor.cluster.converter.ConversionService;
import com.arcsoft.supervisor.cluster.converter.RequestDataConverter;
import com.arcsoft.supervisor.cluster.net.DataPackage;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Base request test.
 * 
 * @author fjli
 * @param <T> - the request class
 */
public abstract class BaseRequestTest<T extends BaseRequest> {

	/**
	 * Test data converter for the specified request.
	 * 
	 * @param action - the action id
	 * @param expect - the request instance
	 * @throws java.io.IOException if convert failed
	 */
	protected T testConverter(int action, T expect) throws IOException {
		ConversionService.addConverter(new RequestDataConverter());
		DataPackage pack = ConversionService.convert(expect);
		assertEquals(pack.getType(), Actions.TYPE_REQUEST);
		assertEquals(pack.getSubType(), action);
		@SuppressWarnings("unchecked")
		T actual = (T) ConversionService.convert(pack);
		assertEquals(expect.getMessageType(), actual.getMessageType());
		return actual;
	}

}
