package com.arcsoft.supervisor.utils.windows;

import com.arcsoft.supervisor.utils.OS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;



/**
 * Gathering OS information on windows system.
 * 
 * @author fjli
 */
public class Win32OS extends OS {

	/**
	 * Returns host name.
	 */
	public String getHostName() {
		return System.getenv("COMPUTERNAME");
	}

	/**
	 * Returns processor id.
	 */
	public String getProcessorId() {
		return execWMIC("CPU", "ProcessorId");
	}

	/**
	 * Returns base board serial number.
	 */
	public String getBaseBoardId() {
		return execWMIC("BASEBOARD", "SerialNumber");
	}

	/**
	 * Returns system UUID.
	 */
	public String getSystemUUID() {
		return execWMIC("CSPRODUCT", "UUID");
	}

	/**
	 * Returns device name.
	 */
	public String getDeviceName() {
		return execWMIC("CSPRODUCT", "NAME");
	}

	/**
	 * Fetching some information from WMIC command.
	 * 
	 * @param model - the model name
	 * @param field - the field name
	 * @return Returns the value of the filed in model.
	 */
	private static String execWMIC(String model, String field) {
		String cmd = String.format("wmic %s get %s", model, field);
		Process p = null;
		try {
			p = Runtime.getRuntime().exec(cmd);
		} catch(IOException e) {
			return null;
		}
		BufferedReader reader = null;
		try {
			p.getOutputStream().flush();
			p.getOutputStream().close();
			reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			// skip first line, it is the result header.
			String line = reader.readLine();
			// read the first not empty line.
			while ((line = reader.readLine()) != null) {
				if (line.length() > 0) {
					return line.trim();
				}
			}
		} catch (IOException e) {
			// ignore exception
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}

}
