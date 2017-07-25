package com.arcsoft.supervisor.cd.data;

import org.msgpack.annotation.Message;

import java.util.ArrayList;

@Message
// Annotation
public class TaskCompleteInfo extends AbstractInfo {
	private int taskid;
	private long duration;
	private int result;
	private ArrayList<Integer> checkType = new ArrayList<Integer>();

	public int getTaskid() {
		return taskid;
	}

	public void setTaskid(int taskid) {
		this.taskid = taskid;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public ArrayList<Integer> getCheckType() {
		return checkType;
	}

	public void setCheckType(ArrayList<Integer> checkType) {
		this.checkType = checkType;
	}

	public String toString() {
		String ret = String.format("Complete taskid:%d; duration:%d; result:0x%x, type:%s", taskid, duration, result,
				checkType.toString());
		return ret;
	}
}
