package com.arcsoft.supervisor.cluster.action;

import com.arcsoft.supervisor.cluster.converter.ConversionService;
import com.arcsoft.supervisor.cluster.converter.ResponseDataConverter;
import com.arcsoft.supervisor.cluster.net.DataPackage;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Base response test.
 * 
 * @author fjli
 * @param <T> - the response class
 */
public abstract class BaseResponseTest<T extends BaseResponse> {

	/**
	 * Test data converter for the specified response.
	 * 
	 * @param action - the action id
	 * @param expect - the response instance
	 * @throws java.io.IOException - if convert failed
	 */
	protected T testConverter(int action, T expect) throws IOException {
		ConversionService.addConverter(new ResponseDataConverter());
		DataPackage pack = ConversionService.convert(expect);
		assertEquals(pack.getType(), Actions.TYPE_RESPONSE);
		assertEquals(pack.getSubType(), action);
		@SuppressWarnings("unchecked")
		T actual = (T) ConversionService.convert(pack);
		assertEquals(expect.getMessageType(), actual.getMessageType());
		assertEquals(expect.getErrorCode(), actual.getErrorCode());
		return actual;
	}

}
