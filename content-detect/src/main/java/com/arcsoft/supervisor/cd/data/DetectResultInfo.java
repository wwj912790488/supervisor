package com.arcsoft.supervisor.cd.data;

import com.arcsoft.supervisor.cd.Utils;
import org.msgpack.annotation.Message;

@Message
// Annotation
public class DetectResultInfo extends AbstractInfo {
	private int taskid;
	private int channel;
	private String resultId;
	private long checkType;
	private long startTime;
	private long endTime;
	private int value1;// is live
	private int value2;// if is audio, value2 is channel

	public int getTaskid() {
		return taskid;
	}

	public void setTaskid(int taskid) {
		this.taskid = taskid;
	}

	public String getResultId() {
		return resultId;
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	public void setResultId(String resultId) {
		this.resultId = resultId;
	}

	public long getCheckType() {
		return checkType;
	}

	public void setCheckType(long checkType) {
		this.checkType = checkType;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public int getValue1() {
		return value1;
	}

	public void setValue1(int value1) {
		this.value1 = value1;
	}

	public int getValue2() {
		return value2;
	}

	public void setValue2(int value2) {
		this.value2 = value2;
	}

	@Override
	public String toString() {
		if (value1 == 1) {
			return "DetectResultInfo [taskid=" + taskid + ", channel=" + channel + ", resultId=" + resultId
					+ ", checkType=0x" + Long.toHexString(checkType) + ", startTime=" + Utils.toLocalTime(startTime)
					+ ", endTime=" + Utils.toLocalTime(endTime) + ", value1=" + value1 + ", value2=" + value2 + "]";
		} else {
			return "DetectResultInfo [taskid=" + taskid + ", channel=" + channel + ", resultId=" + resultId
					+ ", checkType=0x" + Long.toHexString(checkType) + ", startTime=" + Utils.toTimeString(startTime)
					+ ", endTime=" + Utils.toTimeString(endTime) + ", value1=" + value1 + ", value2=" + value2 + "]";
		}

	}

}
