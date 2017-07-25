package com.arcsoft.supervisor.service.remote;

import com.arcsoft.supervisor.service.ServiceSupport;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Remote executor service support.
 * 
 * @author fjli
 */
public abstract class RemoteExecutorServiceSupport extends ServiceSupport {

    @Autowired
	protected RemoteExecutorService remoteExecutorService;

	public void setRemoteExecutorService(RemoteExecutorService remoteExecutorService) {
		this.remoteExecutorService = remoteExecutorService;
	}

}
