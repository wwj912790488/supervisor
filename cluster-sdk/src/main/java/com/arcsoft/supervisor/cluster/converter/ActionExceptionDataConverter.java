package com.arcsoft.supervisor.cluster.converter;

import com.arcsoft.supervisor.cluster.app.ActionException;
import com.arcsoft.supervisor.cluster.message.Message;
import com.arcsoft.supervisor.cluster.net.DataPackage;
import com.arcsoft.supervisor.cluster.utils.ByteBufferReader;
import com.arcsoft.supervisor.cluster.utils.ByteBufferWriter;

import java.io.IOException;


/**
 * Converter between ActionException and data package.
 * 
 * @author fjli
 */
public class ActionExceptionDataConverter implements DataConverter<ActionException> {

	@Override
	public int getDataType() {
		return Message.TYPE_EXCEPTION;
	}

	@Override
	public Class<ActionException> getDataClass() {
		return ActionException.class;
	}

	@Override
	public DataPackage convert(ActionException object) throws IOException {
		ByteBufferWriter writer = new ByteBufferWriter();
		try {
			if (object instanceof ActionException) {
				ActionException exception = (ActionException) object;
				writer.writeInt(exception.getErrorCode());
				writer.writeUTF8(exception.getMessage());
				return new DataPackage(getDataType(), 0, writer.toBytes());
			} else {
				return null;
			}
		} finally {
			writer.close();
		}
	}

	@Override
	public ActionException convert(DataPackage pack) throws IOException {
		switch(pack.getSubType()) {
		case 0:
			ByteBufferReader reader = new ByteBufferReader(pack.getData());
			try {
				int errorCode = reader.readInt();
				String message = reader.readUTF();
				return new ActionException(errorCode, message);
			} finally {
				reader.close();
			}
		default:
			return null;
		}
	}

}
