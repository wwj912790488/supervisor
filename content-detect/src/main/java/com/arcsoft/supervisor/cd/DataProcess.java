package com.arcsoft.supervisor.cd;


import com.arcsoft.supervisor.cd.data.*;
import org.apache.log4j.Logger;
import org.msgpack.MessagePack;
import org.msgpack.unpacker.Unpacker;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class DataProcess {

	private Logger logger = Logger.getLogger(DataProcess.class);
	private MessagePack msgpack = new MessagePack();

	public AbstractInfo process(byte[] buffer, int len) {

		int command = Utils.bytesToInt(buffer, 4);
		int length = Utils.bytesToInt(buffer, 8);
		ByteArrayInputStream in = new ByteArrayInputStream(buffer, 12, length);
		AbstractInfo ret = unpack(command, in);
		try {
			in.close();
		} catch (IOException e) {
		}
		return ret;

	}

	private AbstractInfo unpack(int type, ByteArrayInputStream in) {

		switch (type) {
		case CommandDefine.COMMAND_APP_QUIT: {
		}
			break;

		case CommandDefine.COMMAND_RETURN: {
			try {
				Unpacker unpacker = msgpack.createUnpacker(in);
				CommonResultInfo data = unpacker.read(CommonResultInfo.class);
				unpacker.close();
				unpacker = null;
				return data;
			} catch (IOException e) {
				logger.error(null, e);
			}
		}
			break;
		case CommandDefine.COMMAND_TASK_COMPLETE: {
			try {
				Unpacker unpacker = msgpack.createUnpacker(in);
				TaskCompleteInfo data = unpacker.read(TaskCompleteInfo.class);
				unpacker.close();
				unpacker = null;
				return data;
			} catch (IOException e) {
				logger.error(null, e);
			}

		}
			break;
		case CommandDefine.COMMAND_TASK_PROGRESS: {
			try {
				Unpacker unpacker = msgpack.createUnpacker(in);
				ProgressResultInfo data = unpacker.read(ProgressResultInfo.class);
				unpacker.close();
				unpacker = null;
				return data;
			} catch (IOException e) {
				logger.error(null, e);
			}
		}
			break;
		case CommandDefine.COMMAND_CHECK_RESULT: {
			try {
				Unpacker unpacker = msgpack.createUnpacker(in);
				CheckResultInfo data = unpacker.read(CheckResultInfo.class);
				unpacker.close();
				unpacker = null;
				return data;
			} catch (IOException e) {
				logger.error(null, e);
			}
			break;
		}
		case CommandDefine.COMMAND_GET_TASKINFO: {
			try {
				Unpacker unpacker = msgpack.createUnpacker(in);
				CommandTaskInfo data = unpacker.read(CommandTaskInfo.class);
				unpacker.close();
				unpacker = null;
				return data;
			} catch (IOException e) {
				logger.error(null, e);
			}
			break;
		}
		case CommandDefine.COMMAND_CHECK_IP_ADDRESS:
			try {
				Unpacker unpacker = msgpack.createUnpacker(in);
				MediaCheckIpPort data = unpacker.read(MediaCheckIpPort.class);
				unpacker.close();
				unpacker = null;
				return data;
			} catch (IOException e) {
				logger.error(null, e);
			}
			break;
		case CommandDefine.COMMAND_THUMBNAIL:
			try {
				Unpacker unpacker = msgpack.createUnpacker(in);
				CommandGetThumbnail data = unpacker.read(CommandGetThumbnail.class);
				unpacker.close();
				unpacker = null;
				return data;
			} catch (IOException e) {
				logger.error(null, e);
			}
			break;
		case CommandDefine.COMMAND_CHECK_STREAM_ERROR:
			try {
				Unpacker unpacker = msgpack.createUnpacker(in);
				CheckStreamErrorResult data = unpacker.read(CheckStreamErrorResult.class);
				unpacker.close();
				unpacker = null;
				return data;
			} catch (IOException e) {
				logger.error(null, e);
			}
			break;
		case CommandDefine.COMMAND_DETECT_RESULT:
			try {
				Unpacker unpacker = msgpack.createUnpacker(in);
				DetectResultInfo data = unpacker.read(DetectResultInfo.class);
				unpacker.close();
				unpacker = null;
				return data;
			} catch (IOException e) {
				logger.error(null, e);
			}
			break;
		default:
			break;
		}
		return null;
	}

}
