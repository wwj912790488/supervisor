package com.arcsoft.supervisor.model.vo.task;

public class MediaCheckType {
	static public final int CHECK_TYPE_BLACK_FIELD_INDEX 		= 0;
	static public final int  CHECK_TYPE_GREEN_FIELD_INDEX		= 1;
	static public final int  CHECK_TYPE_STATIC_FRAME_INDEX		= 2;
	// CHECK_TYPE_COLOR_BAR not: support
	static public final int  CHECK_TYPE_COLOR_BAR_INDEX			= 3;
	static public final int  CHECK_TYPE_MOSAIC_INDEX			= 4;
	
	static public final int  CHECK_TYPE_STREAM_INTERRUPT_INDEX	= 31;

	static public final int  CHECK_TYPE_MUTE_THRESHOLD_INDEX	= 32;//静音
	static public final int  CHECK_TYPE_VOLUME_LOW_INDEX		= 33;
	static public final int  CHECK_TYPE_VOLUME_LOUD_INDEX		= 34;
	static public final int  CHECK_TYPE_VOLUME_LOUDN_INDEX		= 35;
	static public final int  CHECK_TYPE_TONE_INDEX				= 36;
	static public final int  CHECK_TYPE_BREAK_INDEX				= 37; //爆音
	static public final int  CHECK_TYPE_TEST_INDEX				= 38;
	static public final int  CHECK_TYPE_WHITE_INDEX				= 39;
	static public final int  CHECK_TYPE_BROWN_INDEX				= 40;

	// Defines type for signal detect

	public static final int SIGNAL_STREAM_CCERROR = 27; //CC错误
	public static final int SIGNAL_STREAM_NOAUDIO = 28; //Audio丢失
	public static final int SIGNAL_STREAM_NOVIDEO = 29; //Video丢失
	public static final int SIGNAL_STREAM_INTERRUPT = 30; //信源中断

}
