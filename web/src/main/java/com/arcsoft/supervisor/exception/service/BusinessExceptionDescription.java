package com.arcsoft.supervisor.exception.service;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * Defines all of {@link Description}s.
 *
 * @author zw.
 */
public enum BusinessExceptionDescription implements Description{

    OK(0),
    URL_CONNECT_TIMEOUT(90),
    HOST_UNREACHABLE(91),
    ERROR(100),
    INVALID_ARGUMENTS(101),
    CONVERT_INPUT_ARGUMENTS_FAILED(102),
    RECORD_LOCKED(103),

    /////////////////////////
    //
    // Defines description for task
    //
    ////////////////////////

    /**
     * The task is not existed.
     */
    TASK_NOT_EXIST(1),
    /**
     * There is no available server to run the task.
     */
    TASK_NO_AVAILABLE_SERVER(104),
    /**
     * The screen config of compose task is not existed.
     */
    TASK_NO_SCREEN_CONFIG(105),
    /**
     * The wall position of screen of compose task is not existed.
     */
    TASK_WALL_POSITION_NOT_EXIST(106),
    /**
     * The ops server of compose task is not existed.
     */
    TASK_OPS_SERVER_NOT_EXIST(107),
    /**
     * Starts or stops timeout.
     */
    TASK_START_OR_STOP_TIMEOUT(108),
    /**
     * The storage of record task is not existed.
     */
    TASK_STORAGE_NOT_EXIST(109),

    TASK_USER_OPS_NOT_BIND(110),

    /**
     * The task status is not running.
     */
    TASK_NOT_RUNNING(110),

    /**
     * Failed to assign port for task.
     */
    TASK_INVALID_RELOAD(111),
	
	    /**
     * Failed to assign port for task.
     */
    TASK_PORT_ASSIGN_FAILED(112),

    TASK_OUTPUT_INVALID(113),

    TASK_OUTPUT_CONFLICT(114),
    TASK_CANNOT_RUNNING(115),

    /////////////////////////
    //
    // Defines description for channel
    //
    ////////////////////////

    /**
     * Indicates the channel is not existed.
     */
    CHANNEL_NOT_EXIST(2101),
    CHANNEL_NOT_SUPPORT_MOBILE(2102),

    /////////////////////////
    //
    // Defines description for screen
    //
    ////////////////////////

    /**
     * Indicates the screen is not existed.
     */
    SCREEN_NOT_EXISTS(1),
    SCREEN_NOTCONFIG_MOBILE(1002),
    /**
     * Indicate the screen not bind ops
     * */
    SCREEN_NOT_BIND_OPS(1003),
    /**
     * Indicates the ops device is already registered.
     */
    OPS_ALREADY_REGISTERED(101),
    /**
     * Indicates the input json of ops is incorrect.
     */
    OPS_INPUT_INCORRECT(102),
    /**
     * Indicates failed to do update for ops.
     */
    OPS_UPDATE_FAILED(101),
    /**
     * Indicates the ops device is not registered.
     */
    OPS_UNREGISTERED(101),
    /**
     * Indicates the ops belongs screen is deleted.
     */
    OPS_SCREEN_DELETED(103),

    /**
     * Indicates the user of registered is exist.
     */
    USER_REGISTER_EXISTS(1),
    /**
     * Indicates the name or password is incorrect.
     */
    USER_LOGIN_NAME_OR_PASSWORD_INCORRECT(1),
    /**
     * Indicates failed to do login.
     */
    USER_LOGOUT_FAILED(1),
    /**
     * Indicate the token is invalid
     */
    INVALID_USER_TOKEN(1104),
    /**
     * Indicates the wall is not exist.
     */
    WALL_NOT_EXISTS(1),

    WALL_NAME_EXISTS(1001),

    WALL_SCREEN_TASK_RUNNING(1101),

    WALL_SETTING_OPTIMISTIC_LOCK(1102),

    WALL_SETTING_NOT_EXISTS(1103),

    WALL_POSITION_WITH_RUNNING_TASK_CANT_DELETE(1104),

    STORAGE_EXISTED(4001),

    /**
     * Indicates failed to execute the shell command of route operate. May be the route parameter is invalid or system
     * failed to execute the shell command.
     */
    FAILED_EXECUTE_SHELL(5001),

    /**
     * Indicates the remote server is not available.
     */
    SERVER_NOT_AVAILABLE(6001),

    /**
     * Some exception occurs in remote peer.
     */
    SERVER_REMOTE_EXCEPTION(6002),

    /**
     * Indicates the system is not initialize.
     */
    SERVER_SYSTEM_NOT_INITIALIZE(6003),

    /////////////////////////
    //
    // Defines description for rtsp
    //
    ////////////////////////

    /**
     * Indicates the publish url of rtsp server is not exist.
     */
    RTSP_SERVER_PUBLISH_URL_NOT_EXIST(7001),

    /**
     * Indicates the storage path of rtsp server is not exist.
     */
    RTSP_SERVER_STORAGE_PATH_NOT_EXIST(7002),

    /**
     * Indicates the ip address of rtsp server is empty .
     */
    RTSP_SERVER_IP_IS_EMPTY(7003),
    
    RTSP_PUBLISHER_IP_NOT_MATCH(7004),
    
    USER_CONFIG_INCOMPLETE(8001),

    SAVE_SDP_FAILED(9005),
    
    SDP_NOT_FOUND(9006),
    
    SETUP_PACKAGE_NOT_FOUND(9020),
    
    SAVE_SETUP_PACKAGE_FAILED(9021),

    LOG_NOT_EXIST(10001),
    LOG_ALREADY_CONFIRMED(10002),

    /*mosaic controller code*/
    MOSAIC_INVALID_TOKEN(1101),
    MOSAIC_SCREEN_NOT_EXIST(101),
    MOSAIC_INVALID_PARAM(103),
    MOSAIC_SCREEN_NOT_INITIALIZED(104),
    MOSAIC_TOKEN_EXPIRED(105),
    MOSAIC_NOT_SUPPORT(1100),
    MOSAIC_UPDATE_LAYOUT_FAILED(1101),
    MOSAIC_TOOMANY_OUTPUT(1102),
    MOSAIC_RESOLUTION_NOT_MATCH(1103),
    MOSAIC_POSITION_OUTRANGE(1104),
    MOSAIC_INVALID_POSITION(1105),
    MOSAIC_POSITION_INTERSECT(1106),
    MOSAIC_INVALID_CHANNEL_ID(1107),
    MOSAIC_APICALL_TOOFREQUENT(1108)
    ;

    private final Description description;

    BusinessExceptionDescription(int code){
        this(code, null);
    }

    BusinessExceptionDescription(int code, String translatorKey){
        this.description = new SimpleDescription(code, translatorKey);
    }

    @Override
    public int getCode() {
        return description.getCode();
    }

    @Override
    public String getTranslatorKey() {
        return description.getTranslatorKey();
    }

    public BusinessException exception(){
        return withException(null);
    }

    public BusinessException withException(Throwable th){
        return th == null ? BusinessException.create(this) : BusinessException.wrap(this, th);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BusinessExceptionDescription{");
        sb.append(name()).append(",");
        sb.append("code=").append(description.getCode());
        if (StringUtils.isNotBlank(description.getTranslatorKey())){
            sb.append(", translatorKey='").append(description.getTranslatorKey()).append('\'');
        }
        sb.append('}');
        return sb.toString();
    }

    private class SimpleDescription extends AbstractDescription{

        private SimpleDescription(int code) {
            super(code);
        }

        private SimpleDescription(int code, String translatorKey) {
            super(code, translatorKey);
        }
    }

}
