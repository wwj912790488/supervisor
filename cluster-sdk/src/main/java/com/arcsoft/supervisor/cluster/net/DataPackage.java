package com.arcsoft.supervisor.cluster.net;

import com.arcsoft.supervisor.cluster.utils.ByteBufferWriter;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class represents a data package.
 * <p>
 * Data package are used to describe the content when transfered through nodes
 * in cluster.
 * 
 * Data package format:
 * <pre>
 * -------------------------------------------------------------------------------------------------------------------
 * |    type(32b)   |  sub type(32b) |  checksum(32b) | data length(32b) |  data offset(32bit) |  extension headers  |
 * -------------------------------------------------------------------------------------------------------------------
 * |    data                                                                                                         |
 * -------------------------------------------------------------------------------------------------------------------
 * </pre>
 * 
 * @author xpeng
 * @author fjli
 */
public class DataPackage {

	private int type;
	private int subType;
	private byte[] data;
	private byte[] ext;

	/**
	 * Create data package without sub type and extension header data.
	 * 
	 * @param type - the message type
	 * @param data - the message data
	 */
	public DataPackage(int type, byte[] data) {
		this(type, 0, data, null);
	}

	/**
	 * Create data package with extension header data, but without sub type.
	 * 
	 * @param type - the message type
	 * @param data - the message data
	 * @param ext - the message extension header
	 */
	public DataPackage(int type, byte[] data, byte[] ext) {
		this(type, 0, data, ext);
	}

	/**
	 * Create data package with sub type, but without extension header data.
	 * 
	 * @param type - the message type
	 * @param subType - the message sub type
	 * @param data - the message data
	 */
	public DataPackage(int type, int subType, byte[] data) {
		this(type, subType, data, null);
	}

	/**
	 * Create data package with sub type and extension header data.
	 * 
	 * @param type - the message type
	 * @param subType - the message sub type
	 * @param data - the message data
	 * @param ext - the message extension header
	 */
	public DataPackage(int type, int subType, byte[] data, byte[] ext) {
		this.type = type;
		this.subType = subType;
		this.data = data;
		this.ext = ext;
	}

	/**
	 * Returns type.
	 */
	public int getType() {
		return type;
	}

	/**
	 * Returns sub type.
	 */
	public int getSubType() {
		return subType;
	}

	/**
	 * Returns data.
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * Returns extension header data.
	 */
	public byte[] getExt() {
		return ext;
	}

	/**
	 * Convert this data package to a byte array.
	 *
	 * @throws java.io.IOException
	 */
	public byte[] toBytes() throws IOException {
		ByteBufferWriter writer = new ByteBufferWriter();
		try {
			// write type id
			writer.writeInt(getType());
			// write sub type id
			writer.writeInt(getSubType());
			// write checksum
			writer.writeInt(0);
			// write data length
			writer.writeInt(data.length);
			if (ext != null && ext.length > 0) {
				// write data offset
				writer.writeInt(20 + ext.length);
				// write extension header data
				writer.write(ext);
			} else {
				// write data offset
				writer.writeInt(20);
			}
			// write data
			writer.write(data);
			return writer.toBytes();
		} finally {
			writer.close();
		}
	}

	/**
	 * Read a data package from the specified input stream.
	 * 
	 * @param is - the specified input stream.
	 * @return Returns a data package read from the specified input stream. 
	 * @throws java.io.IOException
	 */
	public static DataPackage read(InputStream is) throws IOException {
		DataInputStream dis = new DataInputStream(is);
		// read type id.
		int type = dis.readInt();
		// read sub type id.
		int subType = dis.readInt();
		// read checksum.
		dis.readInt();
		// read data length.
		int dataLen = dis.readInt();
		if (dataLen < 0)
			throw new IOException("Invalid data.");
		// read data offset.
		int offset = dis.readInt();
		if (offset < 20)
			throw new IOException("Invalid data.");
		// read extension header data.
		int extLen = offset - 20;
		byte[] ext = null;
		if (extLen > 0) {
			ext = new byte[extLen];
			dis.readFully(ext);
		}
		// read data.
		byte[] data = null;
		if (dataLen > 0) {
			data = new byte[dataLen];
			dis.readFully(data);
		}
		return new DataPackage(type, subType, data, ext);
	}

}
