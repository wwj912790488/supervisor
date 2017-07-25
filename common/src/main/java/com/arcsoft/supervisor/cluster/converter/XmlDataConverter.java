package com.arcsoft.supervisor.cluster.converter;

import com.arcsoft.supervisor.cluster.message.Message;
import com.arcsoft.supervisor.cluster.net.DataPackage;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * Abstract XML data converter for request and response.
 * 
 * @author fjli
 * @param <T> - request or response
 */
public abstract class XmlDataConverter<T extends Message> implements DataConverter<T> {

	private HashMap<Integer, Class<?>> actionToClassMap = new HashMap<Integer, Class<?>>();
	private HashMap<Class<?>, Integer> classToActionMap = new HashMap<Class<?>, Integer>();

	/**
	 * Add action mapping.
	 * 
	 * @param action - the action id
	 * @param type - the message type
	 */
	protected void mapping(int action, Class<?> type) {
		actionToClassMap.put(action, type);
		classToActionMap.put(type, action);
	}

	@Override
	public DataPackage convert(T object) throws IOException {
		Integer action = classToActionMap.get(object.getClass());
		if (action == null)
			throw new IOException("Unsupported message: " + object.getClass());
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(object.getClass());
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			jaxbMarshaller.marshal(object, bos);
			byte[] data = bos.toByteArray();
			return new DataPackage(getDataType(), action, data);
		} catch (JAXBException e) {
			throw new IOException("convert failed.", e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public T convert(DataPackage pack) throws IOException {
		byte[] data = pack.getData();
		Class<?> clazz = actionToClassMap.get(pack.getSubType());
		if (clazz == null)
			throw new IOException("Unsupported type=[" + pack.getType() + ", " + pack.getSubType() + "]");
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
			Unmarshaller jaxbUnmarshal = jaxbContext.createUnmarshaller();
			ByteArrayInputStream bis = new ByteArrayInputStream(data);
			return (T) jaxbUnmarshal.unmarshal(bis);
		} catch (JAXBException e) {
			throw new IOException("convert failed.", e);
		}
	}

}
