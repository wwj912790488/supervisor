package com.arcsoft.supervisor.cd.data;

import org.msgpack.annotation.Message;

@Message
// Annotation
public class ProgressResultInfo extends AbstractInfo {
	private int taskid;
	private long currentPos;
	private long duration;
	private int rate;
	private int state;

	public int getTaskid() {
		return taskid;
	}

	public void setTaskid(int taskid) {
		this.taskid = taskid;
	}

	public long getCurrentPos() {
		return currentPos;
	}

	public void setCurrentPos(long currentPos) {
		this.currentPos = currentPos;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String toString() {
		String ret = String.format("taskid = %d, current = %d, duration = %d, rate = %.2fX", taskid, currentPos,
				duration, (float) rate / 100);
		if (duration > 0) {
			ret += String.format(";%.2f%%", (float) currentPos * 100 / duration);
		}
		return ret;
	}
}
