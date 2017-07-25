package com.arcsoft.supervisor.repository.settings;

/**
 * Service for system control.
 * 
 * @author hxiang
 */
public interface HostRepository {
	public void reboot();
	public void shutdown();
}
