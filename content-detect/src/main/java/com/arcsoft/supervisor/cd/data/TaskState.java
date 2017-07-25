package com.arcsoft.supervisor.cd.data;

public class TaskState {
	public static final int STATE_DISCONNECT = -2;
	public static final int STATE_ERROR = -1;
	public static final int STATE_STOP = 0;
	public static final int STATE_TASK_RUN = 1;
	public static final int STATE_TASK_COMPLETE = 2;
	public static final int STATE_TASK_CANCEL = 3;
	public static final int STATE_TASK_STOPPING = 4;
	public static final int STATE_TASK_STOP_ERROR = 5;
}
