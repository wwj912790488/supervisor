package com.arcsoft.supervisor.cluster.net;

import java.util.concurrent.ScheduledFuture;

/**
 * Network monitor task.
 * 
 * @author fjli
 */
class NetworkMonitorTask {

	private String eth;
	private ScheduledFuture<?> future;
	private Boolean state;

	public NetworkMonitorTask(String eth) {
		this.eth = eth;
	}

	public String getEth() {
		return eth;
	}

	public ScheduledFuture<?> getFuture() {
		return future;
	}

	public void setFuture(ScheduledFuture<?> future) {
		this.future = future;
	}

	public Boolean getState() {
		return state;
	}

	public void setState(Boolean state) {
		this.state = state;
	}

}
