package com.arcsoft.supervisor.utils;

/**
 * This class describes the current user information.
 * 
 * @author fjli
 */
public class User {

	/**
	 * Returns the name of the current user.
	 */
	public String getName() {
		return System.getProperty("user.name");
	}

	/**
	 * Returns the home path of the current user.
	 */
	public String getHomePath() {
		return System.getProperty("user.home");
	}

	/**
	 * Returns the country of the current user.
	 */
	public String getCountry() {
		return System.getProperty("user.country");
	}

	/**
	 * Returns the language of the current user.
	 */
	public String getLanguage() {
		return System.getProperty("user.language");
	}

}
