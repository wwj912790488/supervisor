package com.arcsoft.supervisor.utils;

import java.util.Properties;

/**
 * Converter for variables in configurations.
 * 
 * @author fjli
 */
public final class ConfigVarsConverter {

	private static final String DELIM_START = "${";
	private static final char DELIM_STOP = '}';
	private static final int DELIM_START_LEN = 2;
	private static final int DELIM_STOP_LEN = 1;

	/**
	 * ConfigVarsConverter is a static class.
	 */
	private ConfigVarsConverter() {
	}

	public static String getProperty(String key, Properties props) {
		String value = props.getProperty(key);
		if (value == null)
			return null;
		try {
			return replaceVars(value, props);
		} catch (IllegalArgumentException e) {
			return value;
		}
	}

	public static String replaceVars(String val, Properties props) throws IllegalArgumentException {
		StringBuffer sbuf = new StringBuffer();
		int i = 0;
		int j, k;

		while (true) {
			j = val.indexOf(DELIM_START, i);
			if (j == -1) {
				// no more variables
				if (i == 0) {
					// this is a simple string
					return val;
				} else {
					// add the tail string which contails no variables and return the result.
					sbuf.append(val.substring(i, val.length()));
					return sbuf.toString();
				}
			} else {
				sbuf.append(val.substring(i, j));
				k = val.indexOf(DELIM_STOP, j);
				if (k == -1) {
					throw new IllegalArgumentException('"' + val
							+ "\" has no closing brace. Opening brace at position " + j + '.');
				} else {
					j += DELIM_START_LEN;
					String key = val.substring(j, k);
					// first try in System properties
					String replacement = System.getProperty(key);
					// then try props parameter
					if (replacement == null && props != null) {
						replacement = props.getProperty(key);
					}
					if (replacement != null) {
						String recursiveReplacement = replaceVars(replacement, props);
						sbuf.append(recursiveReplacement);
					}
					i = k + DELIM_STOP_LEN;
				}
			}
		}
	}

}
