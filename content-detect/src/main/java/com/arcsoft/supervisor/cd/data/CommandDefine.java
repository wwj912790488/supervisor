package com.arcsoft.supervisor.cd.data;

public class CommandDefine {
	public static final int COMMAND_APP_QUIT 				= 0x00000001;
	public static final int COMMAND_TASK_START 				= 0x00000002;
	public static final int COMMAND_TASK_STOP 				= 0x00000003;
	public static final int COMMAND_GET_PROGRESS 			= 0x00000004;
	public static final int COMMAND_GET_TASKINFO			= 0x00000005;
	public static final int COMMAND_THUMBNAIL				= 0x00000006;
	
	public static final int COMMAND_RETURN 					= 0x00010001;
	public static final int COMMAND_TASK_COMPLETE 			= 0x00010002;
	public static final int COMMAND_TASK_PROGRESS 			= 0x00010003;
	public static final int COMMAND_CHECK_RESULT 			= 0x00010004;
	public static final int COMMAND_CHECK_IP_ADDRESS		= 0x00010005;
	public static final int COMMAND_CHECK_STREAM_ERROR		= 0x00010006;
	public static final int COMMAND_DETECT_RESULT			= 0x00010007;
}
