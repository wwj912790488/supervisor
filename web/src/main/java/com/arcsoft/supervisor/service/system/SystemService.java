package com.arcsoft.supervisor.service.system;


import com.arcsoft.supervisor.model.domain.system.SystemSettings;

/**
 * System settings service.
 * 
 * @author fjli
 */
public interface SystemService {

	/**
	 * Indicate the system is initialized or not.
	 */
	boolean isSystemInited();

	/**
	 * Get system settings.
	 */
	SystemSettings getSettings();

	/**
	 * Save system settings.
	 * 
	 * @param settings - the settings to be set
	 */
	void saveSettings(SystemSettings settings);

}
