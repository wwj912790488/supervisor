package com.arcsoft.supervisor.cd.data;

import org.msgpack.annotation.Message;

@Message
// Annotation
public class CommonResultInfo extends AbstractInfo {
	private int taskid;
	private int command;
	private int result;
	private String message;

	public int getTaskid() {
		return taskid;
	}

	public void setTaskid(int taskid) {
		this.taskid = taskid;
	}

	public int getCommand() {
		return command;
	}

	public void setCommand(int command) {
		this.command = command;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String toString() {
		String ret = String.format("taskid:%d, command: 0x%x, result: 0x%x, message:%s", taskid, command, result,
				message);

		return ret;
	}
}
