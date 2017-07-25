package com.arcsoft.supervisor.cluster.net;

import java.util.HashMap;

/**
 * Connection options.
 * 
 * @author fjli
 */
public class ConnectOptions {

	/**
	 * Option for local binding IP address.
	 */
	public static final int OPTION_BIND_ADDR = 1;

	/**
	 * Option for local binding port.
	 */
	public static final int OPTION_BIND_PORT = 2;

	/**
	 * Option for connect timeout.
	 */
	public static final int OPTION_CONNECT_TIMEOUT = 3;

	/**
	 * Option for socket read timeout.
	 */
	public static final int OPTION_READ_TIMEOUT = 4;

	/**
	 * Option for disable Nagle's algorithm for this connection. For TCP only.
	 */
	public static final int OPTION_TCP_NODELAY = 5;

	private HashMap<Integer, Object> options = new HashMap<Integer, Object>();

	/**
	 * Set integer value option.
	 * 
	 * @param option - the specified option
	 * @param value - the value of the option
	 */
	public void setInt(int option, int value) {
		options.put(option, value);
	}

	/**
	 * Set string value option.
	 * 
	 * @param option - the specified option
	 * @param value - the value of the option
	 */
	public void setString(int option, String value) {
		options.put(option, value);
	}

	/**
	 * Returns true if this options contains the specified option.
	 * 
	 * @param option - the specified option
	 */
	public boolean containsOption(int option) {
		return options.containsKey(option);
	}

	/**
	 * Get integer value of the specified option.
	 * 
	 * @param option - the specified option
	 */
	public int getInt(int option, int defaultValue) {
		Object value = options.get(option);
		if (value == null)
			return defaultValue;
		if (value instanceof Integer) {
			return ((Integer) value).intValue();
		} else if (value instanceof String) {
			try {
				return Integer.parseInt((String)value);
			} catch(NumberFormatException e) {
				return defaultValue;
			}
		} else {
			return defaultValue;
		}
	}

	/**
	 * Get string value of the specified option.
	 * 
	 * @param option - the specified option
	 */
	public String getString(int option) {
		Object value = options.get(option);
		if (value == null)
			return null;
		if (value instanceof String) {
			return (String) value;
		} else {
			return value.toString();
		}
	}

}
