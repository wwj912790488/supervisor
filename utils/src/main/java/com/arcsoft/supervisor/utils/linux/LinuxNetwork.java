package com.arcsoft.supervisor.utils.linux;

import com.arcsoft.supervisor.utils.Network;
import com.arcsoft.supervisor.utils.NetworkHelper;
import com.arcsoft.supervisor.utils.app.App;
import com.arcsoft.supervisor.utils.app.ShellException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;

/**
 * This class describes the network relation information on linux system.
 * 
 * @author fjli
 */
public class LinuxNetwork extends Network {

	private Logger log = LoggerFactory.getLogger(LinuxNetwork.class);
	private static final String flag = "Link detected:";
    private static final String GET_GATEWAY_COMMAND = "cat /etc/sysconfig/network-scripts/ifcfg-%s | grep GATEWAY | awk -F '=' '{print $2}'";

	@Override
	public String getLocalIp() {
		// eth0 > net0 > virt0, so we use alphabeta order.
		SortedMap<String, String> map = new TreeMap<String, String>();
		try {
			Enumeration<NetworkInterface> inetfs = NetworkInterface.getNetworkInterfaces();
			while (inetfs.hasMoreElements()) {
				NetworkInterface inetf = inetfs.nextElement();
				if (inetf.isLoopback() || !inetf.isUp() || inetf.isPointToPoint())
					continue;
				Enumeration<InetAddress> addrs = inetf.getInetAddresses();
				while (addrs.hasMoreElements()) {
					InetAddress addr = addrs.nextElement();
					if (!addr.isLinkLocalAddress() && !addr.isLoopbackAddress()) {
						map.put(inetf.getName(), addr.getHostAddress());
						break;
					}
				}
			}
			// returns the first address.
			if (!map.isEmpty())
				return map.get(map.firstKey());
		} catch(SocketException e) {
			// skip
		}
		return "127.0.0.1";
	}

	@Override
	public List<String> getLocalIps(){
		List<String> ipadds = new ArrayList<String>();

		try {
			Enumeration<NetworkInterface> inetfs = NetworkInterface.getNetworkInterfaces();
			while (inetfs.hasMoreElements()) {
				NetworkInterface inetf = inetfs.nextElement();
				if (inetf.isLoopback() || !inetf.isUp() || inetf.isPointToPoint() || inetf.isVirtual())
					continue;
				Enumeration<InetAddress> addrs = inetf.getInetAddresses();
				while (addrs.hasMoreElements()) {
					InetAddress addr = addrs.nextElement();
					if (!addr.isLinkLocalAddress() && !addr.isLoopbackAddress()) {
						ipadds.add(addr.getHostAddress());
						break;
					}
				}
			}
		} catch(SocketException e) {
			// skip
		}
		return ipadds;
	}

	@Override
	public boolean addIp(String name, String ip, String mask) {
		String prefix = mask;
		if (NetworkHelper.isIpAddress(mask))
			prefix = String.valueOf(NetworkHelper.toMaskPrefix(mask));
		if (executeCommand(String.format("ip addr add %s/%s dev %s", ip, prefix, name))) {
			executeCommand(String.format("arping -c 5 -U -I %s %s", name, ip));
			return true;
		}
		return false;
	}

	@Override
	public boolean deleteIp(String name, String ip, String mask) {
		String prefix = mask;
		if (NetworkHelper.isIpAddress(mask))
			prefix = String.valueOf(NetworkHelper.toMaskPrefix(mask));
		return executeCommand(String.format("ip addr del %s/%s dev %s", ip, prefix, name));
	}

	private boolean executeCommand(String cmd) {
		Process p = null;
		try {
			p = Runtime.getRuntime().exec(cmd);
			p.waitFor();
			int code = p.exitValue();
			if (code == 0) {
				return true;
			} else {
				log.error("execute command " + cmd + " failed, ret=" + code);
			}
		} catch (IOException | InterruptedException e) {
			log.error("execute command " + cmd + " failed.", e);
		} finally {
			if (p != null)
				p.destroy();
		}
		return false;
	}

	@Override
	public boolean checkEth(String eth) throws IOException {
		String command = "ethtool " + eth + " | grep \"" + flag + "\"";
		Process p = Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", command });
		try {
			p.waitFor();
		} catch (InterruptedException e) {
			throw new IOException(e);
		}
		BufferedReader reader = null;
		try {
            reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = reader.readLine();
			if (line != null) {
				int index = line.indexOf(flag);
				if (index != -1) {
					String result = line.substring(index + flag.length() + 1);
					if (result.equalsIgnoreCase("yes"))
						return true;
				}
			}
			return false;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}
	}

    @Override
    public String getGatewayWithIp(String ip) throws IOException {
        NetworkInterface neti = NetworkHelper.getInterfaceByHostAddr(ip);
        if (neti != null){
            String command = String.format(GET_GATEWAY_COMMAND, neti.getName());
            try {
                List<String> results = App.runShell(command);
                return results.size() > 0 ? results.get(0).replaceAll("\"", "") : "";
            } catch (ShellException e) {
                log.error("Failed to execute the command: " + command, e);
            }
        }
        return "";
    }

}
