package com.arcsoft.supervisor.utils;

import com.arcsoft.supervisor.utils.linux.LinuxNetwork;
import com.arcsoft.supervisor.utils.linux.LinuxOS;
import com.arcsoft.supervisor.utils.osx.OsxNetwork;
import com.arcsoft.supervisor.utils.osx.OsxOS;
import com.arcsoft.supervisor.utils.windows.Win32Network;
import com.arcsoft.supervisor.utils.windows.Win32OS;

/**
 * A helper class for fetching system relation information, such as OS, user, path etc.
 * 
 * @author fjli
 */
public class SystemHelper {

	/**
	 * Get OS information.
	 */
	public static final OS os = createOSObject();

	/**
	 * Get current user information.
	 */
	public static final User user = new User();

	/**
	 * Get path information.
	 */
	public static final Path path = new Path();

	/**
	 * Get network information.
	 */
	public static final Network net = createNetWork();

	/**
	 * Create OS instance.
	 */
	private static OS createOSObject() {
		String name = System.getProperty("os.name");
		if (name != null) {
			name = name.toLowerCase();
			if (name.contains("windows")) {
				return new Win32OS();
			} else if (name.contains("linux")) {
				return new LinuxOS();
			} else if (name.contains("os x")){
                return new OsxOS();
            }
		}
		return null;
	}

	/**
	 * Create Network instance.
	 */
	private static Network createNetWork() {
		String name = System.getProperty("os.name");
		if (name != null) {
			name = name.toLowerCase();
			if (name.contains("windows")) {
				return new Win32Network();
			} else if (name.contains("linux")) {
				return new LinuxNetwork();
			} else if (name.contains("os x")){
                return new OsxNetwork();
            }
		}
		return null;
	}

	public static void main(String[] args) {
		long t = System.currentTimeMillis();
		System.err.println("        os name: " + os.getName());
		System.err.println("     os version: " + os.getVersion());
		System.err.println("os architecture: " + os.getArchitecture());
		System.err.println("      host name: " + os.getHostName());
		System.err.println("    device name: " + os.getDeviceName());
		System.err.println("         cpu id: " + os.getProcessorId());
		System.err.println("   baseboard id: " + os.getBaseBoardId());
		System.err.println("    system uuid: " + os.getSystemUUID());
		System.err.println("      user name: " + user.getName());
		System.err.println(" user home path: " + user.getHomePath());
		System.err.println("   user country: " + user.getCountry());
		System.err.println("  user language: " + user.getLanguage());
		System.err.println("   current path: " + path.getCurrentPath());
		System.err.println(" temporary path: " + path.getTempPath());
		System.err.println("       local ip: " + net.getLocalIp());
		System.err.println("time=" + (System.currentTimeMillis()-t));
	}

}
