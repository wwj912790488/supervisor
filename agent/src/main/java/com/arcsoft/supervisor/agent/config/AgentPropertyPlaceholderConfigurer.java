package com.arcsoft.supervisor.agent.config;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.util.Properties;


/**
 * Agent configuration for spring integration.
 * 
 * @author fjli
 */
public class AgentPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

	@Override
	protected String resolvePlaceholder(String placeholder, Properties props) {
		String value = AppConfig.getString(placeholder);
		if (value != null)
			return value;
		return super.resolvePlaceholder(placeholder, props);
	}

}
