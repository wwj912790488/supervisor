package com.arcsoft.supervisor.web.api;

/**
 * Created by wwj on 2017/3/14.
 */
public class ApiErrorCode {

    public static final int API_SUCCESS = 0;
    public static final int API_UNKNOWN_ERROR = -1;
    public static final int API_CHANNEL_IS_EXISTS= 1001; //isChannelExists
    public static final int API_CHANNEL_NAME_ISEXISTS = 1002;//ChannelNameExists
    public static final int API_SCREEN_NOT_EXISTS = 1003;//API_SCREEN_NOT_EXISTS
    public static final int API_CHANNEL_FINDBYID_NOT_EXISTS=1004;

    public static final int API_TASK_DELETE_FAILED = 1006;
    public static final int TASK_CHECK_STATUS_FAILED = 1007;
    public static final int NO_SERVER_GROUP = 2001;

}
