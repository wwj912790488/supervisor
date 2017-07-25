package com.arcsoft.supervisor.cluster.converter;

import com.arcsoft.supervisor.cluster.event.*;
import com.arcsoft.supervisor.cluster.message.Message;
import com.arcsoft.supervisor.cluster.net.DataPackage;
import com.arcsoft.supervisor.cluster.node.NodeDescription;
import com.arcsoft.supervisor.cluster.utils.ByteBufferReader;
import com.arcsoft.supervisor.cluster.utils.ByteBufferWriter;

import java.io.IOException;



/**
 * Convert between event and data package.
 * 
 * @author fjli
 */
public class EventDataConverter implements DataConverter<Event> {

	@Override
	public int getDataType() {
		return Message.TYPE_EVENT;
	}

	@Override
	public Class<Event> getDataClass() {
		return Event.class;
	}

	@Override
	public DataPackage convert(Event event) throws IOException {
		ByteBufferWriter writer = new ByteBufferWriter();
		try {
			if (event instanceof JoinEvent) {
				NodeDescription desc = ((JoinEvent) event).getNode();
				writer.writeInt(desc.getType());
				writer.writeUTF8(desc.getId());
				writer.writeUTF8(desc.getName());
				writer.writeUTF8(desc.getIp());
				writer.writeInt(desc.getPort());
                writer.writeUTF8(desc.getNetmask());
                writer.writeUTF8(desc.getGateway());
                writer.writeUTF8(desc.getEth());
                writer.writeUTF8(desc.getFunctions());
                return new DataPackage(getDataType(), Event.JOIN_EVENT, writer.toBytes());
			} else if (event instanceof LeaveEvent) {
				String nodeId = ((LeaveEvent) event).getNodeId();
				writer.writeUTF8(nodeId);
				return new DataPackage(getDataType(), Event.LEAVE_EVENT, writer.toBytes());
			} else if (event instanceof HeartBeatEvent) {
				String nodeId = ((HeartBeatEvent) event).getNodeId();
				writer.writeUTF8(nodeId);
				return new DataPackage(getDataType(), Event.HEART_BEAT_EVENT, writer.toBytes());
			} else if (event instanceof SearchEvent) {
				SearchEvent searchEvent = (SearchEvent) event;
				writer.writeInt(searchEvent.getType());
				return new DataPackage(getDataType(), Event.SEARCH_EVENT, writer.toBytes());
			} else if (event instanceof HeartBeatStartEvent) {
				HeartBeatStartEvent startEvent = (HeartBeatStartEvent) event;
				writer.writeUTF8(startEvent.getNodeId());
				writer.writeLong(startEvent.getInterval());
				writer.writeInt(startEvent.getPort());
				return new DataPackage(getDataType(), Event.HEART_BEAT_START_EVENT, writer.toBytes());
			} else if (event instanceof HeartBeatStopEvent) {
				HeartBeatStopEvent stopEvent = (HeartBeatStopEvent) event;
				writer.writeUTF8(stopEvent.getNodeId());
				return new DataPackage(getDataType(), Event.HEART_BEAT_STOP_EVENT, writer.toBytes());
			} else if (event instanceof ResponseEvent) {
				return new DataPackage(getDataType(), Event.RESPONSE_EVENT, writer.toBytes());
			} else {
				return null;
			}
		} finally {
			writer.close();
		}
	}

	@Override
	public Event convert(DataPackage pack) throws IOException {
		if (pack.getSubType() == Event.RESPONSE_EVENT) {
			return new ResponseEvent();
		}
		ByteBufferReader reader = new ByteBufferReader(pack.getData());
		try {
			switch(pack.getSubType()) {
			case Event.JOIN_EVENT: {
				int type = reader.readInt();
				String id = reader.readUTF();
				String name = reader.readUTF();
				String ip = reader.readUTF();
				int port = reader.readInt();
                String netmask = reader.readUTF();
                String gateWay = reader.readUTF();
                String eth = reader.readUTF();
                String functions = reader.readUTF();
				NodeDescription desc = new NodeDescription(type, id, name, ip, port, netmask, eth, gateWay, functions);
                return new JoinEvent(desc);
			}
			case Event.LEAVE_EVENT: {
				String nodeId = reader.readUTF();
				return new LeaveEvent(nodeId);
			}
			case Event.HEART_BEAT_EVENT: {
				String nodeId = reader.readUTF();
				return new HeartBeatEvent(nodeId);
			}
			case Event.SEARCH_EVENT: {
				int type = reader.readInt();
				return new SearchEvent(type);
			}
			case Event.HEART_BEAT_START_EVENT: {
				String nodeId = reader.readUTF();
				long interval = reader.readLong();
				int port = reader.readInt();
				return new HeartBeatStartEvent(nodeId, interval, port);
			}
			case Event.HEART_BEAT_STOP_EVENT: {
				String nodeId = reader.readUTF();
				return new HeartBeatStopEvent(nodeId);
			}
			default:
				return null;
			}
		} finally {
			reader.close();
		}
	}

}
