package com.arcsoft.supervisor.repository.system;

import java.util.HashMap;

/**
 * System settings repository.
 * 
 * @author fjli
 * @author zw
 */
public interface SystemRepository {

	/**
	 * Get system settings.
	 */
	HashMap<String, String> getSettings();

	/**
	 * Save system settings.
	 * 
	 * @param settings - the settings to be set
	 */
	void saveSettings(HashMap<String, String> settings);

	public void autoDeleteContentLogs(long endTime);

}
