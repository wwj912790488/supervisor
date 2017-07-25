package com.arcsoft.supervisor.utils;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.List;

/**
 * This class describes the network relation information.
 *
 * @author fjli
 */
public abstract class Network {

    /**
     * Returns the local IP address.
     */
    public abstract String getLocalIp();

    /**
     * Returns the local IP addresss.
     */
    public abstract List<String> getLocalIps();

    /**
     * Add IP to the specified network interface.
     *
     * @param name - the specified network interface
     * @param ip   - the IP address to be added
     * @param mask - the mask to be added
     * @return true if add success.
     */
    public abstract boolean addIp(String name, String ip, String mask);

    /**
     * Delete IP from the specified network interface.
     *
     * @param name - the specified network interface
     * @param ip   - the IP address to be deleted
     * @param mask - the mask to be deleted
     * @return true if delete success.
     */
    public abstract boolean deleteIp(String name, String ip, String mask);

    /**
     * Check network connection state.
     *
     * @param name - the specified network interface
     * @return true if it is connected, otherwise false.
     * @throws IOException                   if execute command failed.
     * @throws UnsupportedOperationException if does not support
     */
    public abstract boolean checkEth(String name) throws IOException;

    /**
     * Retrieves the gateway of the specified ip address.
     *
     * @param ip the ip address
     * @return the gateway of ip address
     * @throws IOException
     */
    public abstract String getGatewayWithIp(String ip) throws IOException;

}
