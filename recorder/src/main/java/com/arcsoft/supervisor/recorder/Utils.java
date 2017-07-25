package com.arcsoft.supervisor.recorder;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class Utils {
	public static int bytesToInt(byte[] buf, int index) {
		return buf[index + 3] & 0xff | (buf[index + 2] & 0xff) << 8 | (buf[index + 1] & 0xff) << 16
				| (buf[index] & 0xff) << 24;
	}

	public static void intToBytes(int val, byte[] array, int offset) {
		array[3 + offset] = (byte) (val & 0xff);
		array[2 + offset] = (byte) (val >> 8 & 0xff);
		array[1 + offset] = (byte) (val >> 16 & 0xff);
		array[offset] = (byte) (val >> 24 & 0xff);
	}

	public static boolean isIpPAdress(String str) {
		final String rex = "^((\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5]|[*])\\.){3}(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5]|[*])$";
		Pattern pattern = Pattern.compile(rex);
		return pattern.matcher(str).matches();
	}

	public static String toTimeString(long date) {
		String ret = "";
		int hh = (int) (date / 3600000);
		int mm = (int) ((date % 3600000) / 60000);
		int ss = (int) ((date % 60000) / 1000);
		int sss = (int) (date % 1000);
		ret += String.format("%02d:%02d:%02d.%03d", hh, mm, ss, sss);
		return ret;
	}

	public static String toLocalTime(long time) {
		String ret = "";
		Date date = new Date(time);
		SimpleDateFormat dateformat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSz");
		ret = dateformat1.format(date);

		return ret;

	}

	public static String getHostIp() {
		String ip = "";
		InetAddress addr;
		try {
			addr = InetAddress.getLocalHost();
			ip = addr.getHostAddress().toString();
		} catch (UnknownHostException e) {

			e.printStackTrace();
		}

		return ip;
	}

	public static boolean portIsFree(int port) {
		boolean ret = false;
		try {
			ServerSocket socket = new ServerSocket(port);
			socket.close();
			ret = true;
		} catch (IOException e) {
		}
		return ret;
	}

	public static String getLocalHostName() {

		String hostName;
		try {

			InetAddress addr = InetAddress.getLocalHost();
			hostName = addr.getHostName();
		} catch (Exception ex) {
			hostName = "";
		}

		return hostName;

	}

	public static boolean isLocalIp(String ip) {
		boolean ret = false;

		try {
			if (ip.compareTo("127.0.0.1") == 0) {
				return true;
			}
			String hostName = getLocalHostName();
			if (hostName.length() > 0) {
				InetAddress[] addrs = InetAddress.getAllByName(hostName);
				if (addrs.length > 0) {
					for (InetAddress addr : addrs) {
						if (ip.compareTo(addr.getAddress().toString()) == 0) {
							ret = true;
							break;
						}
					}

				}

			}

		} catch (Exception ex) {
		}

		return ret;
	}

	public static byte[] readFile(String pathName) {
		File file = new File(pathName);
		InputStream in = null;
		byte[] buffer = null;
		try {
			long fileLenth = file.length();
			buffer = new byte[(int) fileLenth];
			in = new FileInputStream(file);
			in.read(buffer);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return buffer;
	}

	public static void deleteFile(String pathName) {
		File file = new File(pathName);
		file.delete();
	}

	public static int detctTypeToCheckType(long detctType) {
		int index = 0;
		long value = detctType;
		do {
			if (value == 1) {
				break;
			}
			value = value >> 1;
			index++;
		} while (true);

		return index;
	}

}
