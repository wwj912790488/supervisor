package com.arcsoft.supervisor.cd.data;


import org.msgpack.annotation.Message;

@Message
// Annotation
public class CommandGetThumbnail extends AbstractInfo {
	private int taskId;
	private int width;
	private int height;
	private int result;
	private byte[] buffer = new byte[1];
	

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public byte[] getBuffer() {
		return buffer;
	}

	public void setBuffer(byte[] buffer) {
		this.buffer = buffer;
	}

	@Override
	public String toString() {
		return "CommandGetThumbnail [taskId=" + taskId + ", width=" + width + ", height=" + height + ", result="
				+ result + ", buffer=" + buffer.length + "]";
	}
	
	

}
