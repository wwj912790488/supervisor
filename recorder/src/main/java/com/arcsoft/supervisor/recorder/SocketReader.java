package com.arcsoft.supervisor.recorder;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class SocketReader {
	
	private static Logger log = Logger.getLogger(SocketReader.class);

	public static int readSocket(InputStream inputStream, byte[] buffer) throws IOException {

		Arrays.fill(buffer, (byte) 0);

		int length = inputStream.read(buffer, 0, 8);
		if (length < 0) {
			return -1;
		}
		
		log.debug("read header length " + length);
			
		int dataLen = Utils.bytesToInt(buffer, 0);
		
		log.debug("decode length to dateLen " + dataLen);
		int readLen = dataLen;
		do {
			length = inputStream.read(buffer, (dataLen - readLen) + 8, readLen);
			if (length < 0) {
				return -1;
			}
			readLen -= length;
			if (readLen == 0) {
				break;
			}

		} while (true);
		log.debug("read command of length " + dataLen);
		return dataLen + 8;

	}

}
