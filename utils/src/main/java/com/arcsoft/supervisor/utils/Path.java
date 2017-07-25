package com.arcsoft.supervisor.utils;

/**
 * This class describes the path relation information.
 * 
 * @author fjli
 */
public class Path {

	/**
	 * Returns path separator.
	 */
	public String getPathSeparator() {
		return System.getProperty("path.separator");
	}

	/**
	 * Returns line separator.
	 */
	public String getLineSeparator() {
		return System.getProperty("line.separator");
	}

	/**
	 * Returns file separator.
	 */
	public String getFileSeparator() {
		return System.getProperty("file.separator");
	}

	/**
	 * Return the current path.
	 */
	public String getCurrentPath() {
		return System.getProperty("user.dir");
	}

	/**
	 * Returns the temporary path.
	 */
	public String getTempPath() {
		return System.getProperty("java.io.tmpdir");
	}

}
