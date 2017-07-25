package com.arcsoft.supervisor.utils.osx;

import com.arcsoft.supervisor.utils.Network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A network implementations for mac os x system.The class is not
 * a completed implementations it is just for test under os x system.
 *
 * @author zw.
 */
public class OsxNetwork extends Network {

    @Override
    public String getLocalIp() {
        return "127.0.0.1";
    }

    @Override
    public List<String> getLocalIps() {
        return new ArrayList<String>();
    }

    @Override
    public boolean addIp(String name, String ip, String mask) {
        return false;
    }

    @Override
    public boolean deleteIp(String name, String ip, String mask) {
        return false;
    }

    @Override
    public boolean checkEth(String name) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getGatewayWithIp(String ip) throws IOException {
        return "192.168.1.1";
    }
}
