package com.arcsoft.supervisor.cd.data;

import org.msgpack.annotation.Message;

@Message
// Annotation
public class MediaCheckIpPort extends AbstractInfo {
	private String ip;
	private int port;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public String toString() {
		return "MediaCheckIPPort [ip=" + ip + ", port=" + port + "]";
	}
}
