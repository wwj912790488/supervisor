package com.arcsoft.supervisor.agent.config;

import com.arcsoft.supervisor.utils.ConfigVarProperties;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Application configuration.
 * 
 * @author fjli
 */
public class AppConfig {

	private static Logger log = Logger.getLogger(AppConfig.class);
	private static ConfigVarProperties appConfig;

	/**
	 * Load configuration from file.
	 * 
	 * @throws java.io.IOException if load configuration failed.
	 */
	public static void load() throws IOException {
		String configFile = System.getProperty("agent.config");
		if (configFile == null)
			throw new IOException("The agent configuration file has not specified.");

		InputStream is = null;
		try {
			// Open the configuration file.
			log.info("load agent configuration from file: " + configFile);
			is = new FileInputStream(configFile);

			// Load configuration from properties.
			appConfig = new ConfigVarProperties();
			appConfig.load(is);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * Get string value with the specified key.
	 * 
	 * @param key - the specified option key
	 * @return returns the string value with the specified key. Returns null if
	 *         the key is not set.
	 */
	public static String getString(String key) {
		return appConfig.getString(key);
	}

	/**
	 * Get string value with the specified key.
	 * 
	 * @param key - the specified option key
	 * @param defaultValue - the default value
	 * @return returns the string value with the specified key. Returns the
	 *         default value if the key is not set.
	 */
	public static String getString(String key, String defaultValue) {
		return appConfig.getString(key, defaultValue);
	}

	/**
	 * Get integer value with the specified key.
	 * 
	 * @param key - the specified option key
	 * @return returns the integer value with the specified key. Returns null if
	 *         the key is not set.
	 */
	public static Integer getInt(String key) {
		return appConfig.getInt(key);
	}

	/**
	 * Get integer value with the specified key.
	 * 
	 * @param key - the specified option key
	 * @param defaultValue - the default value
	 * @return returns the integer value with the specified key. Returns the
	 *         default value if the key is not set.
	 */
	public static Integer getInt(String key, int defaultValue) {
		return appConfig.getInt(key, defaultValue);
	}

}
