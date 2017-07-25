package com.arcsoft.supervisor.cd.data;

public class TaskInfo {

	private int port = 0;
	private int taskId = 0;
	private String ip;
	private int state = TaskState.STATE_STOP;

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "TaskInfo [port=" + port + ", taskId=" + taskId + ", ip=" + ip + ", state=" + state + "]";
	}
	
	

}
