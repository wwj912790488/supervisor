package com.arcsoft.supervisor.utils;

import com.arcsoft.supervisor.utils.app.App;
import com.arcsoft.supervisor.utils.app.ShellException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;

/**
 * This is a helper class for network relation operations.
 * 
 * @author fjli
 */
public abstract class NetworkHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkHelper.class);

    private static final String GET_NETMASK_COMMAND = "cat /etc/sysconfig/network-scripts/ifcfg-%s | grep NETMASK | awk -F '=' '{print $2}'";

	/**
	 * Returns the local IP address.
	 */
	public static String getLocalIp() {
		return SystemHelper.net.getLocalIp();
	}

	/**
	 * Returns the local IP addresss.
	 */
	public static List<String> getLocalIps() {
		return SystemHelper.net.getLocalIps();
	}

	/**
	 * Convert MAC address from bytes to string.
	 * 
	 * @param hwAddr - the MAC address
	 * @return the string represent the MAC address.
	 */
	public static String toMacString(byte[] hwAddr) {
		return StringHelper.toHexString(hwAddr, '-').toUpperCase();
	}

	/**
	 * Get the network interface by MAC address string.
	 * 
	 * @param macString - the string of MAC address.
	 * @return the NetworkInterface represents this network.
	 */
	public static NetworkInterface getInterfaceByMacAddr(String macString) {
		try {
			Enumeration<NetworkInterface> inetfs = NetworkInterface.getNetworkInterfaces();
			while (inetfs.hasMoreElements()) {
				NetworkInterface inetf = inetfs.nextElement();
				byte[] hwAddr = inetf.getHardwareAddress();
				if (hwAddr == null)
					continue;
				String macAddr = toMacString(hwAddr);
				if (macString.equalsIgnoreCase(macAddr))
					return inetf;
			}
		} catch(SocketException e) {
		}
		return null;
	}

	/**
	 * Get the network interface by the specified IP address.
	 * 
	 * @param ip - the host address.
	 * @return the NetworkInterface represents this network.
	 */
	public static NetworkInterface getInterfaceByHostAddr(String ip) {
		try {
			InetAddress addr = InetAddress.getByName(ip);
			if (addr != null)
				return NetworkInterface.getByInetAddress(addr);
		} catch(UnknownHostException e) {
		} catch(SocketException e) {
		}
		return null;
	}

	/**
	 * Get host address from the specified network interface.
	 * 
	 * @param inetf - the specified network interface
	 * @return the host address.
	 */
	public static String getHostAddress(NetworkInterface inetf) {
		Enumeration<InetAddress> addrs = inetf.getInetAddresses();
		while (addrs.hasMoreElements()) {
			InetAddress addr = addrs.nextElement();
			if (!addr.isLinkLocalAddress() && !addr.isLoopbackAddress()) {
				return addr.getHostAddress();
			}
		}
		return null;
	}

	/**
	 * Get host address from the network with the specified MAC address.
	 * 
	 * @param macString - the string of MAC address. 
	 * @return the host address.
	 */
	public static String getHostAddressByMacAddr(String macString) {
		NetworkInterface inetf = getInterfaceByMacAddr(macString);
		if (inetf == null)
			return null;
		return getHostAddress(inetf);
	}

	/**
	 * Get MAC address from the network with the specified host address.
	 * 
	 * @param ip - the specified host address
	 * @return the MAC address.
	 */
	public static String getMacAddrByHostAddress(String ip) {
		NetworkInterface intef = getInterfaceByHostAddr(ip);
		if (intef == null)
			return null;
		byte[] hwAddr = null;
		try {
			hwAddr = intef.getHardwareAddress();
		} catch (SocketException e) {
			return null;
		}
		if (hwAddr != null)
			return NetworkHelper.toMacString(hwAddr);
		return null;
	}

	/**
	 * Get the MAC address which the local IP address is binding.
	 */
	public static String getLocalMacAddr() {
		return getMacAddrByHostAddress(NetworkHelper.getLocalIp());
	}

	/**
	 * Test whether the given string is IP address or not.
	 * 
	 * @param ip - the given string to be tested
	 * @return true if it is IP address, otherwise false.
	 */
	public static boolean isIpAddress(String ip) {
		if (ip != null)
			return ip.matches("^(((25[0-5])|(((2[0-4])|([0-1]\\d)|(\\d))?\\d))\\.){3}((25[0-5])|(((2[0-4])|([0-1]\\d)|(\\d))?\\d))$");
		return false;
	}

	/**
	 * Convert mask text format to prefix.
	 * 
	 * @param mask - the mask of text format
	 * @return the mask prefix.
	 */
	public static int toMaskPrefix(String mask) {
		String[] parts = mask.split("\\.");
		int ret = 0;
		for (String part : parts) {
			int msk = 0x80;
			int netmask = Integer.decode(part);
			while (msk != 0) {
				if ((netmask & msk) > 0)
					ret++;
				msk >>= 1;
			}
		}
		return ret;
	}

	/**
	 * Convert mask prefix to mask text format.
	 * <p>
	 * 255.255.224.0 => 19
	 * 
	 * @param prefix - the mask prefix
	 * @return the mask of text format.
	 */
	public static String toMaskText(int prefix) {
		int address = 0xffffffff << (32 - prefix);
		String text = "";
		for (int i = 0; i < 4; i++) {
			if (!text.isEmpty())
				text = "." + text;
			text = (address & 0xff) + text;
			address >>= 8;
		}
		return text;
	}

	/**
	 * Add IP to the specified network interface.
	 * 
	 * @param name - the specified network interface
	 * @param ip - the IP address to be added
	 * @param mask - the mask to be added (e.g. 255.255.255.0, 24)
	 * @return true if add success.
	 */
	public static boolean addIp(String name, String ip, String mask) {
		return SystemHelper.net.addIp(name, ip, mask);
	}

	/**
	 * Delete IP from the specified network interface.
	 * 
	 * @param name - the specified network interface
	 * @param ip - the IP address to be deleted
	 * @param mask - the mask to be deleted (e.g. 255.255.255.0, 24)
	 * @return true if delete success.
	 */
	public static boolean deleteIp(String name, String ip, String mask) {
		return SystemHelper.net.deleteIp(name, ip, mask);
	}

	/**
	 * Check network connection state.
	 * 
	 * @param name - the specified network interface
	 * @return true if it is connected, otherwise false.
	 * @throws IOException - if execute command failed.
	 */
	public static boolean checkEth(String name) throws IOException {
		return SystemHelper.net.checkEth(name);
	}

    /**
     * Retrieves the netmask with ip address.
     *
     * @param ip the ip address
     * @return empty string or actual netmask
     */
    public static String getNetmaskWithIp(String ip){
        NetworkInterface netit = getInterfaceByHostAddr(ip);
        if (netit != null){
            String command = String.format(GET_NETMASK_COMMAND, netit.getName());
            try {
                List<String> results = App.runShell(command);
                return results.size() > 0 ? results.get(0).replaceAll("\"", "") : "";
            } catch (ShellException e) {
                LOGGER.error("Failed to execute the command: " + command, e);
            }
        }
        return "";
    }

}
