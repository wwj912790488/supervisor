package com.arcsoft.supervisor.cd.data;

import org.msgpack.annotation.Message;

@Message
// Annotation
public class CommonInfo extends AbstractInfo {

	private int taskid;

	public int getTaskid() {
		return taskid;
	}

	public void setTaskid(int taskid) {
		this.taskid = taskid;
	}
}
