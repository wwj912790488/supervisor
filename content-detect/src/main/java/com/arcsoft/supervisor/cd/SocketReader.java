package com.arcsoft.supervisor.cd;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class SocketReader {

	public static int readSocket(InputStream inputStream, byte[] buffer) throws IOException {
		if (buffer == null) {
			return -1;
		}
		Arrays.fill(buffer, (byte) 0);
		do {
			int length = inputStream.read(buffer, 0, 4);
			if (length < 0) {
				return -1;
			}
			if (buffer[0] == 'M' && buffer[1] == 'V' && buffer[2] == 'C' && buffer[3] == 'H') {
				break;
			}
		} while (true);

		int length = inputStream.read(buffer, 4, 8);
		if (length < 0) {
			return -1;
		}
		int dataLen = Utils.bytesToInt(buffer, 8);
		int readLen = dataLen;
		do {
			length = inputStream.read(buffer, (dataLen - readLen) + 12, readLen);
			if (length < 0) {
				return -1;
			}
			readLen -= length;
			if (readLen == 0) {
				break;
			}

		} while (true);

		return dataLen + 12;

	}

}
