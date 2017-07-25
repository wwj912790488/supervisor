package com.arcsoft.supervisor.service.settings;

/**
 * Service for host setting
 * 
 * @author hxiang
 */
public interface LocalHostService {

	void reboot() throws Exception;

	void shutdown() throws Exception;

}
