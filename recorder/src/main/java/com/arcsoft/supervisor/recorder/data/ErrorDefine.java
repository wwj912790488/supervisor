package com.arcsoft.supervisor.recorder.data;

public class ErrorDefine {
	// ERROR result
	public static final int MVCH_ERROR_BASE = 0x81200000;
	public static final int MVCH_EXISTINSTANCE = (MVCH_ERROR_BASE + 1);
	public static final int MVCH_ID_GRAPHNOEXIT = (MVCH_ERROR_BASE + 2);
	public static final int MVCH_ID_ERROR = (MVCH_ERROR_BASE + 3);
	public static final int MVCH_NOT_SUPPORT_TYPE = (MVCH_ERROR_BASE + 4);
	public static final int MVCH_CONFIG_ERROR = (MVCH_ERROR_BASE + 5);
	public static final int MVCH_INIT_CUDA_ERROR = (MVCH_ERROR_BASE + 6);

	public static final int MVCH_NO_IDLE_PROCESSER = (MVCH_ERROR_BASE + 100);
	public static final int MVCH_CONNECT_ERROR = (MVCH_ERROR_BASE + 101);
	public static final int MVCH_SAME_TASKID_RUNNING = (MVCH_ERROR_BASE + 102);
}
