package com.arcsoft.supervisor.cd.data;

import org.msgpack.annotation.Message;

@Message
// Annotation
public class CheckStreamErrorResult extends AbstractInfo {
	private int taskId;
	private long findTime;
	private long index;
	private int value;

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public long getFindTime() {
		return findTime;
	}

	public void setFindTime(long findTime) {
		this.findTime = findTime;
	}

	public long getIndex() {
		return index;
	}

	public void setIndex(long index) {
		this.index = index;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "CheckStreamErrorResult [taskId=" + taskId + ", findTime=" + findTime + ", index=" + index + ", value="
				+ value + "]";
	}

}
