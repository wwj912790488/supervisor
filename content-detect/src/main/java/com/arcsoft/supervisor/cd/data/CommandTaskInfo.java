package com.arcsoft.supervisor.cd.data;

import org.msgpack.annotation.Message;

import java.util.ArrayList;

@Message
// Annotation
public class CommandTaskInfo extends AbstractInfo {
	private int taskid;
	private ArrayList<Integer> checkType = new ArrayList<Integer>();
	private int state;

	public int getTaskid() {
		return taskid;
	}

	public void setTaskid(int taskid) {
		this.taskid = taskid;
	}

	public ArrayList<Integer> getCheckType() {
		return checkType;
	}

	public void setCheckType(ArrayList<Integer> checkType) {
		this.checkType = checkType;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "CommandTaskInfo [taskid=" + taskid + ", checkType=" + checkType.toString() + ", state=" + state + "]";
	}

}
