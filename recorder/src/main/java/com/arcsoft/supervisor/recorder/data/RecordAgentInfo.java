package com.arcsoft.supervisor.recorder.data;

import com.arcsoft.supervisor.recorder.RecorderListener;

public class RecordAgentInfo {

	//private String appName = "/usr/local/arcsoft/arcvideo/transcoder-supervisor/streamrecoder";
	private String appName = "/home/tw/transcoder/streamrecorder";
	private int listenPort = 10001;
	private String ip = "127.0.0.1";
	private RecorderListener recorderListener;
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public int getListenPort() {
		return listenPort;
	}
	public void setListenPort(int listenPort) {
		this.listenPort = listenPort;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public RecorderListener getRecorderListener() {
		return recorderListener;
	}
	public void setRecorderListener(RecorderListener recorderListener) {
		this.recorderListener = recorderListener;
	}

}
