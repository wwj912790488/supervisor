package com.arcsoft.supervisor.cluster.app;

/**
 * Error codes.
 * 
 * @author fjli
 */
public interface ErrorCode {

	/**
	 * Unknown error.
	 */
	public static final int UNKNOWN = 0x00000000;

	/**
	 * Cannot convert request to data pack.
	 */
	public static final int CONVERT_REQUEST_FAILED = 0x00000001;

	/**
	 * Send request to agent failed.
	 */
	public static final int SEND_REQUEST_FAILED = 0x00000002;

	/**
	 * Cannot convert data pack to response.
	 */
	public static final int CONVERT_RESPONSE_FAILED = 0x00000003;

	/**
	 * The agent cannot find the action to execute the received request.
	 */
	public static final int ACTION_NOT_FOUND = 0x00000004;

	/**
	 * Indicate the response is null.
	 */
	public static final int NULL_RESPONSE = 0x00000005;

	/**
	 * Execute request to agent failed.
	 */
	public static final int EXECUTE_REQUEST_FAILED = 0x00000006;

}
