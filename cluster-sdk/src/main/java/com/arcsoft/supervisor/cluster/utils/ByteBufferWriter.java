package com.arcsoft.supervisor.cluster.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A byte buffer writer lets an application write primitive Java data types to
 * an byte array in a portable way.
 * 
 * @author fjli
 */
public class ByteBufferWriter extends DataOutputStream implements DataOutput {

	/**
	 * Creates a new ByteBufferWriter.
	 */
	public ByteBufferWriter() {
		super(new ByteArrayOutputStream());
	}

	/**
	 * Creates a new ByteBufferWriter, with a buffer capacity of the specified
	 * size, in bytes.
	 * 
	 * @param size - the initial size.
	 * @throws IllegalArgumentException if size is negative.
	 */
	public ByteBufferWriter(int size) {
		super(new ByteArrayOutputStream(size));
	}

	/**
	 * Write UTF8 safely.
	 * 
	 * @param str
	 * @throws java.io.IOException
	 */
	public void writeUTF8(String str) throws IOException {
		writeUTF(str != null ? str : "");
	}

	/**
	 * Creates a newly allocated byte array. Its size is the current size of
	 * this byte buffer and the valid contents of the buffer have been copied
	 * into it.
	 * 
	 * @return the current contents of this byte buffer, as a byte array.
	 */
	public byte[] toBytes() {
		return ((ByteArrayOutputStream) out).toByteArray();
	}

}
