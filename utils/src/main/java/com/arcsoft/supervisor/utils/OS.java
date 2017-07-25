package com.arcsoft.supervisor.utils;

/**
 * This class describes the operator system information.
 * 
 * @author fjli
 */
public abstract class OS {

	/**
	 * Returns OS name.
	 */
	public String getName() {
		return System.getProperty("os.name");
	}

	/**
	 * Returns OS version.
	 */
	public String getVersion() {
		return System.getProperty("os.version");
	}

	/**
	 * Returns OS architecture.
	 * @return
	 */
	public String getArchitecture() {
		return System.getProperty("os.arch");
	}

	/**
	 * Returns host name.
	 */
	public abstract String getHostName();

	/**
	 * Returns processor id.
	 */
	public abstract String getProcessorId();

	/**
	 * Returns base board serial number.
	 */
	public abstract String getBaseBoardId();

	/**
	 * Returns system UUID.
	 */
	public abstract String getSystemUUID();

	/**
	 * Returns device name.
	 */
	public abstract String getDeviceName();

}
