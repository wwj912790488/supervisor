package com.arcsoft.supervisor.recorder;

public interface TaskManagerListener {
	void receivedTaskState(int taskId, int state);

}
