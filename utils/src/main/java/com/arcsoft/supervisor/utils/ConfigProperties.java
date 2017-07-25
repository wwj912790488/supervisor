package com.arcsoft.supervisor.utils;

import java.util.Properties;

/**
 * Configuration properties.
 * 
 * @author fjli
 */
public class ConfigProperties extends Properties {

	private static final long serialVersionUID = 68642461516390127L;

	/**
	 * Creates an empty property list with no default values.
	 */
	public ConfigProperties() {
	}

	/**
	 * Creates an empty property list with the specified defaults.
	 * 
	 * @param defaults - the defaults.
	 */
	public ConfigProperties(Properties defaults) {
		super(defaults);
	}

	/**
	 * Get boolean value with the specified key.
	 * 
	 * @param key - the specified option key
	 * @return returns the boolean value with the specified key. Returns null if
	 *         the key is not set.
	 */
	public Boolean getBoolean(String key) {
		String value = getProperty(key);
		if (value != null)
			return Boolean.valueOf(value);
		return null;
	}

	/**
	 * Get long value with the specified key.
	 * 
	 * @param key - the specified option key
	 * @param defaultValue - the default value
	 * @return returns the boolean value with the specified key. Returns the
	 *         default value if the key is not set.
	 */
	public Boolean getBoolean(String key, boolean defaultValue) {
		Boolean value = getBoolean(key);
		if (value != null)
			return value;
		return defaultValue;
	}

	/**
	 * Get double value with the specified key.
	 * 
	 * @param key - the specified option key
	 * @return returns the double value with the specified key. Returns null if
	 *         the key is not set.
	 */
	public Double getDouble(String key) {
		String value = getProperty(key);
		if (value != null)
			return Double.valueOf(value);
		return null;
	}

	/**
	 * Get double value with the specified key.
	 * 
	 * @param key - the specified option key
	 * @param defaultValue - the default value
	 * @return returns the double value with the specified key. Returns the
	 *         default value if the key is not set.
	 */
	public Double getDouble(String key, double defaultValue) {
		Double value = getDouble(key);
		if (value != null)
			return value;
		return defaultValue;
	}

	/**
	 * Get float value with the specified key.
	 * 
	 * @param key - the specified option key
	 * @return returns the float value with the specified key. Returns null if
	 *         the key is not set.
	 */
	public Float getFloat(String key) {
		String value = getProperty(key);
		if (value != null)
			return Float.valueOf(value);
		return null;
	}

	/**
	 * Get float value with the specified key.
	 * 
	 * @param key - the specified option key
	 * @param defaultValue - the default value
	 * @return returns the float value with the specified key. Returns the
	 *         default value if the key is not set.
	 */
	public Float getFloat(String key, float defaultValue) {
		Float value = getFloat(key);
		if (value != null)
			return value;
		return defaultValue;
	}

	/**
	 * Get integer value with the specified key.
	 * 
	 * @param key - the specified option key
	 * @return returns the integer value with the specified key. Returns null if
	 *         the key is not set.
	 */
	public Integer getInt(String key) {
		String value = getProperty(key);
		if (value != null)
			return Integer.decode(value);
		return null;
	}

	/**
	 * Get integer value with the specified key.
	 * 
	 * @param key - the specified option key
	 * @param defaultValue - the default value
	 * @return returns the integer value with the specified key. Returns the
	 *         default value if the key is not set.
	 */
	public Integer getInt(String key, int defaultValue) {
		Integer value = getInt(key);
		if (value != null)
			return value;
		return defaultValue;
	}

	/**
	 * Get long value with the specified key.
	 * 
	 * @param key - the specified option key
	 * @return returns the long value with the specified key. Returns null if
	 *         the key is not set.
	 */
	public Long getLong(String key) {
		String value = getProperty(key);
		if (value != null)
			return Long.decode(value);
		return null;
	}

	/**
	 * Get long value with the specified key.
	 * 
	 * @param key - the specified option key
	 * @param defaultValue - the default value
	 * @return returns the long value with the specified key. Returns the
	 *         default value if the key is not set.
	 */
	public Long getLong(String key, long defaultValue) {
		Long value = getLong(key);
		if (value != null)
			return value;
		return defaultValue;
	}

	/**
	 * Get string value with the specified key.
	 * 
	 * @param key - the specified option key
	 * @return returns the string value with the specified key. Returns null if
	 *         the key is not set.
	 */
	public String getString(String key) {
		return getProperty(key);
	}

	/**
	 * Get string value with the specified key.
	 * 
	 * @param key - the specified option key
	 * @param defaultValue - the default value
	 * @return returns the string value with the specified key. Returns the
	 *         default value if the key is not set.
	 */
	public String getString(String key, String defaultValue) {
		return getProperty(key, defaultValue);
	}

	/**
	 * Set boolean value for the specified key.
	 * 
	 * @param key - the specified option key
	 * @param value - the specified option value
	 */
	public void setBoolean(String key, boolean value) {
		setProperty(key, String.valueOf(value));
	}

	/**
	 * Set double value for the specified key.
	 * 
	 * @param key - the specified option key
	 * @param value - the specified option value
	 */
	public void setFloat(String key, double value) {
		setProperty(key, String.valueOf(value));
	}

	/**
	 * Set float value for the specified key.
	 * 
	 * @param key - the specified option key
	 * @param value - the specified option value
	 */
	public void setFloat(String key, float value) {
		setProperty(key, String.valueOf(value));
	}

	/**
	 * Set int value for the specified key.
	 * 
	 * @param key - the specified option key
	 * @param value - the specified option value
	 */
	public void setInt(String key, int value) {
		setProperty(key, String.valueOf(value));
	}

	/**
	 * Set long value for the specified key.
	 * 
	 * @param key - the specified option key
	 * @param value - the specified option value
	 */
	public void setLong(String key, long value) {
		setProperty(key, String.valueOf(value));
	}

	/**
	 * Set string value for the specified key.
	 * 
	 * @param key - the specified option key
	 * @param value - the specified option value
	 */
	public void setString(String key, String value) {
		setProperty(key, value);
	}

}
