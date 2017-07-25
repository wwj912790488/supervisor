package com.arcsoft.supervisor.cluster.utils;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;

/**
 * A byte buffer reader lets an application read primitive Java data types from
 * an underlying input stream in a machine-independent way.
 * 
 * @author fjli
 */
public class ByteBufferReader extends DataInputStream implements DataInput {

	/**
	 * Creates a ByteBufferReader so that it uses data as its buffer array. The
	 * buffer array is not copied.
	 * 
	 * @param data - the input buffer.
	 */
	public ByteBufferReader(byte[] data) {
		super(new ByteArrayInputStream(data));
	}

	/**
	 * Creates a ByteBufferReader so that it uses data as its buffer array. The
	 * buffer array is not copied.
	 * 
	 * @param data - the input buffer.
	 * @param offset - the offset in the buffer of the first byte to read.
	 * @param len - the maximum number of bytes to read from the buffer
	 */
	public ByteBufferReader(byte[] data, int offset, int len) {
		super(new ByteArrayInputStream(data, offset, len));
	}

}
