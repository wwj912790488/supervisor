package com.arcsoft.supervisor.model.domain.settings;

import java.util.List;


/**
 * 
 * @author zw
 */
public class OutputPathValidateResult {
	
	private boolean success = false;
	
	private List<String> nonMountedPaths;
	
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	
	public List<String> getNonMountedPaths() {
		return nonMountedPaths;
	}

	
	public void setNonMountedPaths(List<String> nonMountedPaths) {
		this.nonMountedPaths = nonMountedPaths;
	}


	
	

}
