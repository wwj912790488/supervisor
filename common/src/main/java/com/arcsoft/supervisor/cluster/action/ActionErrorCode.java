package com.arcsoft.supervisor.cluster.action;

/**
 * Error codes for actions.
 * 
 * @author fjli
 */
public interface ActionErrorCode {

	/**
	 * Action is executed without error.
	 */
	public static final int SUCCESS = 0x00000000;

	/**
	 * Unknown error.
	 */
	public static final int UNKNOWN_ERROR = 0xFFFFFFFF;

	/**
	 * Create heart beat failed.
	 */
	public static final int CREATE_HEART_BEAT_FAILED = 0x80001001;

	/**
	 * Indicate the live agent is already bound.
	 */
	public static final int ALREADY_BOUND = 0x80001002;

	/**
	 * Indicate unbind failed.
	 */
	public static final int UNBIND_FAILED = 0x80001003;

	/**
	 * Indicate the slave is not available.
	 */
	public static final int SLAVE_NOT_AVAILABLE = 0x80001004;

	/**
	 * Indicate the master already unbind by commander.
	 */
	public static final int NO_BINDING_INFO = 0x80001005;

	/**
	 * Indicate the binding information not match.
	 */
	public static final int BINDING_INFO_NOT_MATCH = 0x80001006;

	/**
	 * Indicate the action failed because of the network error.
	 */
	public static final int NETWORK_ERROR_DETECTED = 0x80001007;

	/**
	 * Indicate that to run linux shell failed.
	 */
	public static final int RUN_SHELL_FAILED = 0x80002001;

	/**
	 * Indicate the io error.
	 */
	public static final int IO_ERROR = 0x80002002;

	/**
	 * Indicate greater than task count limit of license
	 */
	public static final int GREATER_THAN_MAX_TASK_COUNT = 0x80003001;

	/**
	 * Indicate greater than output count limit of license
	 */
	public static final int GREATER_THAN_MAX_OUTPUT_COUNT = 0x80003002;

	/**
	 * Indicate greater than hdoutput count limit of license
	 */
	public static final int GREATER_THAN_MAX_HDOUTPUT_COUNT = 0x80003003;

	/**
	 * Indicate greater than sdoutput count limit of license
	 */
	public static final int GREATER_THAN_MAX_SDOUTPUT_COUNT = 0x80003004;
	
	/**
	 * Indicate currently the task already running
	 */
	public static final int TASK_ALREADY_RUNNING = 0x80003005;
	
	/**
	 * Indicate currently the task's output paths not exists
	 */
	public static final int TASK_OUTPUT_PATH_NOT_EXISTS = 0x80003006;

	/**
	 * Indicate the license file is invalid.
	 */
	public static final int INVALID_LICENSE = 0x80004001;

    /**
     * The storage is already existed.
     */
    public static final int STORAGE_NAME_EXISTED = 0x80005001;

}
