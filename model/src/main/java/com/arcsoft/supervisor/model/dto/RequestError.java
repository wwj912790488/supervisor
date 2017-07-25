package com.arcsoft.supervisor.model.dto;

public class RequestError {
	private Integer code;
	
	public static final int WallNameExistErrorCode = 1001;
	public static final int ScreenTaskRunningErrorCode = 1101;
	public static final int WallSettingOptimisticLockErrorCode = 1102;
	public static final int WallSettingNotExistsErrorCode = 1103;
	
	public RequestError() {}
	
	public RequestError(int code) {
		this.code = code;
	}
	
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	
}
