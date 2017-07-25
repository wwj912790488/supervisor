package com.arcsoft.supervisor.utils;


import java.util.Properties;

/**
 * Configurations which support variables.
 * 
 * @author fjli
 */
public class ConfigVarProperties extends ConfigProperties {

	private static final long serialVersionUID = 7065211909067249721L;

	/**
	 * Creates an empty property list with no default values.
	 */
	public ConfigVarProperties() {
	}

	/**
	 * Creates an empty property list with the specified defaults.
	 * 
	 * @param defaults - the defaults.
	 */
	public ConfigVarProperties(Properties defaults) {
		super(defaults);
	}

	/**
	 * Get for the default property with the specified key in this property list.
	 * 
	 * @param key - the specified property key
	 * @return the default property without variables replacement.
	 */
	public String getDefaultProperty(String key) {
		return super.getProperty(key);
	}

	@Override
	public String getProperty(String key) {
		String value = getDefaultProperty(key);
		if (value == null)
			return null;
		try {
			return ConfigVarsConverter.replaceVars(value, this);
		} catch (IllegalArgumentException e) {
			return value;
		}
	}

}
