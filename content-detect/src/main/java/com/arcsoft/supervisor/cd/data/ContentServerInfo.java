package com.arcsoft.supervisor.cd.data;

import com.arcsoft.supervisor.cd.CheckResultListener;
import com.arcsoft.supervisor.transcoder.AppConfig;


public class ContentServerInfo {

	private String appName = AppConfig.getProperty("appdir")!=null? AppConfig.getProperty("appdir")+"/transcoder-supervisor/mediachecker":"/usr/local/arcsoft/arcvideo/transcoder-supervisor/mediachecker";
	//private String appName ="/usr/local/arcsoft/arcvideo/transcoder-supervisor/mediachecker";
	private int listenPort = 0;
	private int startPort = 8000;
	private int processNum = 10;
	private String ip = "127.0.0.1";
	private CheckResultListener checkResultListener = null;

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

	public int getStartPort() {
		return startPort;
	}

	public void setStartPort(int startPort) {
		this.startPort = startPort;
	}

	public int getProcessNum() {
		return processNum;
	}

	public void setProcessNum(int processNum) {
		this.processNum = processNum;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public CheckResultListener getCheckResultListener() {
		return checkResultListener;
	}

	public void setCheckResultListener(CheckResultListener checkResultListener) {
		this.checkResultListener = checkResultListener;
	}

}
