package com.arcsoft.supervisor.cluster.converter;

import com.arcsoft.supervisor.cluster.action.BaseRequest;
import com.arcsoft.supervisor.cluster.net.DataPackage;
import org.junit.Test;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;

import static org.junit.Assert.fail;

/**
 * Test cases for xml data converter.
 * 
 * @author fjli
 */
public class DataConverterTest {

	@XmlRootElement
	static class TestRequest extends BaseRequest {
	}

	static class NoXmlRootRequest extends BaseRequest {
	}

	@XmlRootElement
	static class NoDefaultConstructRequest extends BaseRequest {
		public NoDefaultConstructRequest(int code) {
		}
	}

	class TestConverter extends RequestDataConverter {
		void addMapping() {
			mapping(Integer.MAX_VALUE, TestRequest.class);
			mapping(Integer.MAX_VALUE-1, NoXmlRootRequest.class);
			mapping(Integer.MAX_VALUE-2, NoDefaultConstructRequest.class);
		}
	}

	@Test
	public void testMessage() throws IOException {
		TestConverter converter = new TestConverter();

		// test not registered message.
		try {
			converter.convert(new TestRequest());
			fail();
		} catch (IOException e) {
		}

		// test success case
		converter.addMapping();
		converter.convert(new TestRequest());

		// test registered message, but no declare XmlRootElement.
		try {
			converter.convert(new NoXmlRootRequest());
			fail();
		} catch (IOException e) {
		}

		// test registered message, but no default construct.
		try {
			converter.convert(new NoDefaultConstructRequest(0));
			fail();
		} catch (IOException e) {
		}
	}

	@Test
	public void testDataPackage() throws IOException {
		// get correct data package.
		TestConverter converter = new TestConverter();
		converter.addMapping();
		DataPackage pack = converter.convert(new TestRequest());

		TestConverter converter1 = new TestConverter();

		// test not registered message.
		try {
			converter1.convert(pack);
			fail();
		} catch (IOException e) {
		}

		// test registered message, but no declare XmlRootElement.
		try {
			String xmlData = "<noXmlRootRequest/>";
			DataPackage newPack = new DataPackage(pack.getType(),
					pack.getSubType(), xmlData.getBytes(), pack.getExt());
			converter.convert(newPack);
			fail();
		} catch (IOException e) {
		}

		// test registered message, but no default construct.
		try {
			String xmlData = "<noDefaultConstructRequest/>";
			DataPackage newPack = new DataPackage(pack.getType(),
					pack.getSubType(), xmlData.getBytes(), pack.getExt());
			converter.convert(newPack);
			fail();
		} catch (IOException e) {
		}
	}

}
