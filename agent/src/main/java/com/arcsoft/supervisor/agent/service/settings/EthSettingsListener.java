package com.arcsoft.supervisor.agent.service.settings;

import java.util.Properties;

/**
 * Notify when Ethernet settings changed.
 * 
 * @author fjli
 */
public interface EthSettingsListener {

	/**
	 * Notify when settings changed.
	 * 
	 * @param settings - the network settings.
	 */
	void ethSettingsChanged(Properties settings);

}
