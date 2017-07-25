package com.arcsoft.supervisor.utils.linux;

import com.arcsoft.supervisor.utils.OS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Gathering OS information on linux system.
 * 
 * @author fjli
 */
public class LinuxOS extends OS {

	/**
	 * Returns host name.
	 */
	public String getHostName() {
		return exec(new String[] {"hostname"});
	}

	/**
	 * Returns processor id.
	 */
	public String getProcessorId() {
		String str = exec(new String[] {"dmidecode -t processor"}, "ID");
		if (str == null)
			return null;
		int i = str.indexOf("ID:");
		str = str.substring(i + 3);
		String[] arr = str.split(" ");
		str = "";
		for (i = arr.length - 1; i >= 0; i--) {
			str += arr[i];
		}
		return str;
	}

	/**
	 * Returns base board serial number.
	 */
	public String getBaseBoardId() {
		return exec(new String[] {"/bin/sh", "-c", "dmidecode -s baseboard-serial-number | sed '/^#/ d'"});
	}

	/**
	 * Returns system UUID.
	 */
	public String getSystemUUID() {
		return exec(new String[] {"/bin/sh", "-c", "dmidecode -s system-uuid | sed '/^#/ d'"});
	}

	/**
	 * Returns device name.
	 */
	public String getDeviceName() {
		return exec(new String[] {"/bin/sh", "-c", "dmidecode -s system-product-name | sed '/^#/ d'"});
	}

	/**
	 * Execute the given command.
	 * 
	 * @param cmd - the command to be executed.
	 * @param grep - the grep argument
	 * @return Returns the first not empty line, if the grep exists, it should match the value.
	 */
	private String exec(String[] cmd, String... grep) {
		String filter = null;
		if (grep != null && grep.length > 0)
			filter = grep[0];
		Process p = null;
		try {
			p = Runtime.getRuntime().exec(cmd);
		} catch(IOException e) {
			return null;
		}
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (line.length() > 0) {
					if (filter != null && !line.contains(filter))
						continue;
					return line.trim();
				}
			}
		} catch (IOException e) {
			// ignore exception
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException e) {
			}
		}
		return null;
	}

}
