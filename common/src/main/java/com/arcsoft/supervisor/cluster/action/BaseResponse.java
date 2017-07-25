package com.arcsoft.supervisor.cluster.action;

import com.arcsoft.supervisor.cluster.app.Response;

import javax.xml.bind.annotation.XmlTransient;

/**
 * Base response.
 * 
 * @author fjli
 */
public abstract class BaseResponse implements Response {

	/**
	 * The default error code is unknown error.
	 */
	private int errorCode = ActionErrorCode.UNKNOWN_ERROR;

	@Override
	@XmlTransient
	public int getMessageType() {
		return Actions.TYPE_RESPONSE;
	}

	/**
	 * Set the specified error code.
	 * 
	 * @param errorCode - the error code
	 */
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * Returns the error code.
	 */
	public int getErrorCode() {
		return errorCode;
	}

	/**
	 * Test the error code is succeed or not.
	 * @return
	 */
	@XmlTransient
	public boolean isSuccess() {
		return errorCode == ActionErrorCode.SUCCESS;
	}

}
