package com.arcsoft.supervisor.cluster.converter;

import com.arcsoft.supervisor.cluster.message.Message;
import com.arcsoft.supervisor.cluster.net.DataPackage;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;


/**
 * Converter service. User can register new converter for the specified data
 * type and class.
 * 
 * @author fjli
 */
public class ConversionService {

	private static HashMap<Integer, DataConverter<?>> converters = new HashMap<Integer, DataConverter<?>>();

	static {
		addConverter(new EventDataConverter());
		addConverter(new ActionExceptionDataConverter());
	}

	/**
	 * Add data converter for the specified data type and class.
	 * 
	 * @param converter - the data converter
	 */
	public static void addConverter(DataConverter<?> converter) {
		converters.put(converter.getDataType(), converter);
	}

	/**
	 * Converter event to data pack.
	 * 
	 * @param object - the object to be converted
	 * @return Returns a data package converted from the specified object.
	 * @throws java.io.IOException
	 */
	public static DataPackage convert(Message object) throws IOException {
		DataConverter<?> converter = converters.get(object.getMessageType());
		if (converter == null)
			throw new IOException("Cannot find converter for object " + object);
		DataPackage pack = null;
		try {
			Method method = converter.getClass().getMethod("convert", Message.class);
			pack = (DataPackage) method.invoke(converter, object);
		} catch (Exception e) {
			if (e instanceof IOException)
				throw (IOException) e;
			throw new IOException("Convert failed.", e);
		}
		if (pack == null)
			throw new IOException("Convert object failed, object: " + object.getClass().getName());
		return pack;
	}

	/**
	 * Converter pack to object.
	 * 
	 * @param pack - the data pack to be converted
	 * @return Returns a object converted from the specified data package.
	 * @throws java.io.IOException
	 */
	public static Message convert(DataPackage pack) throws IOException {
		int type = pack.getType();
		DataConverter<?> converter = converters.get(type);
		if (converter == null)
			throw new IOException("Cannot find converter for type: " + type);
		Message object = null;
		try {
			object = converter.convert(pack);
		} catch(RuntimeException e) {
			throw new IOException("Convert failed.", e);
		}
		if (object == null)
			throw new IOException("Convert object failed, type: " + type);
		return object;
	}

}
