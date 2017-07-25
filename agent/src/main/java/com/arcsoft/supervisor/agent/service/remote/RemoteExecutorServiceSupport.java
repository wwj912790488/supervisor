package com.arcsoft.supervisor.agent.service.remote;

/**
 * Remote executor service support.
 * 
 * @author fjli
 */
public abstract class RemoteExecutorServiceSupport {

	protected RemoteExecutorService remoteExecutorService;

	public void setRemoteExecutorService(RemoteExecutorService remoteExecutorService) {
		this.remoteExecutorService = remoteExecutorService;
	}

}
