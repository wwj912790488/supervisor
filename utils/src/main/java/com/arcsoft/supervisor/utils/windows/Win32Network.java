package com.arcsoft.supervisor.utils.windows;

import com.arcsoft.supervisor.utils.Network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class describes the network relation information on windows system.
 * 
 * @author fjli
 */
public class Win32Network extends Network {

	@Override
	public String getLocalIp() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			return "127.0.0.1";
		}
	}

	@Override
	public List<String> getLocalIps() {
		return new ArrayList<String>();
	}

	@Override
	public boolean addIp(String name, String ip, String mask) {
		// TODO: add ip on windows
		return false;
	}

	@Override
	public boolean deleteIp(String name, String ip, String mask) {
		// TODO: delete ip on windows
		return false;
	}

	@Override
	public boolean checkEth(String name) throws IOException {
		// TODO: check network interface state
		throw new UnsupportedOperationException();
	}

    @Override
    public String getGatewayWithIp(String ip) throws IOException {
        return "192.168.1.1"; //just for test
    }

}
