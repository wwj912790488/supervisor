package com.arcsoft.supervisor.cd;

public interface TaskManagerListener {
	void receivedTaskState(int taskId, int state);

}
