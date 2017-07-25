package com.arcsoft.supervisor.recorder;


import com.arcsoft.supervisor.recorder.data.*;
import org.apache.log4j.Logger;
import org.msgpack.MessagePack;
import org.msgpack.unpacker.Unpacker;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class DataProcess {

	private Logger logger = Logger.getLogger(DataProcess.class);
	private MessagePack msgpack = new MessagePack();

	public Command process(byte[] buffer, int len) {

		int length = Utils.bytesToInt(buffer, 0);
		int type = Utils.bytesToInt(buffer, 4);
		logger.debug("process command buffer of length " + length + " and type " + type);
		ByteArrayInputStream in = new ByteArrayInputStream(buffer, 8, length);
		Command ret = unpack(type, in);
		try {
			in.close();
		} catch (IOException e) {
		}
		return ret;

	}

	private Command unpack(int type, ByteArrayInputStream in) {

		switch (type) {	
			case CommandTypes.COMMAND_TYPE_DIRECT:
				try {
					Unpacker unpacker = msgpack.createUnpacker(in);
					DirectCommand data = unpacker.read(DirectCommand.class);
					unpacker.close();
					return data;
				} catch (IOException e) {
					
				}
				break;
			case CommandTypes.COMMAND_TYPE_REPORT_PORT:
				try {
					Unpacker unpacker = msgpack.createUnpacker(in);
					ReportPortCommand data = unpacker.read(ReportPortCommand.class);
					unpacker.close();
					return data;
				} catch (IOException e) {
					
				}
				break;
		}
		return null;
	}

}
