package com.arcsoft.supervisor.cluster.converter;

import com.arcsoft.supervisor.cluster.message.Message;
import com.arcsoft.supervisor.cluster.net.DataPackage;

import java.io.IOException;


/**
 * Define a converter which used to convert a object to another type object.
 * 
 * @author fjli
 */
public interface DataConverter<T extends Message> {

	/**
	 * Returns the data type associated with this converter.
	 */
	int getDataType();

	/**
	 * Returns the class associated with this converter.
	 */
	Class<T> getDataClass();

	/**
	 * Converter object to data package.
	 * 
	 * @param object - the specified object
	 * @throws java.io.IOException - if convert failed.
	 */
	DataPackage convert(T object) throws IOException;

	/**
	 * Converter data package to object.
	 * 
	 * @param pack - the specified data package
	 * @throws java.io.IOException - if convert failed.
	 */
	T convert(DataPackage pack) throws IOException;

}
