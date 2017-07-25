package com.arcsoft.supervisor.recorder.data;

public class StreamErrorDefine {
	public static final int ERROR_TS_sync_loss = 0;
	public static final int ERROR_Sync_byte_error = 1;
	public static final int ERROR_PAT_error = 2;
	public static final int ERROR_Continuity_count_error = 3;
	public static final int ERROR_PMT_error = 4;
	public static final int ERROR_PID_error = 5;

	public static final int ERROR_Transport_error = 6;
	public static final int ERROR_CRC_error = 7;
	public static final int ERROR_PCR_repetition_error = 8;
	public static final int ERROR_PCR_discontinuity_indicator_error = 9;
	public static final int ERROR_PCR_accuracy_error = 10;
	public static final int ERROR_PTS_error = 11;
	public static final int ERROR_CAT_error = 12;

	public static final int ERROR_SI = 13; // 表间隔错误"
	public static final int ERROR_NIT_actual_error = 14;
	public static final int ERROR_NIT_other_error = 15;
	public static final int ERROR_SI_error = 16;
	public static final int ERROR_Buffor_error = 17;
	public static final int ERROR_Unreferenced_PID = 18;
	public static final int ERROR_SDT_actual_error = 19;
	public static final int ERROR_SDT_other_error = 20;
	public static final int ERROR_EIT_actual_error = 21;
	public static final int ERROR_EIT_other_error = 22;
	public static final int ERROR_EIT_PF_error = 23;
	public static final int ERROR_RST_error = 24;
	public static final int ERROR_TDT_error = 25;
	public static final int ERROR_Empty_buffer_error = 26;
	public static final int ERROR_Data_delay_error = 27;
}
