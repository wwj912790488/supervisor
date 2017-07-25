package com.arcsoft.supervisor.recorder;

public interface RecorderListener {
	void receivedTaskState(int taskId, int state);
}
