package com.arcsoft.supervisor.service.server.impl;

import com.arcsoft.supervisor.model.domain.server.AbstractOpsServer;
import com.arcsoft.supervisor.repository.server.BaseOpsServerRepository;
import com.arcsoft.supervisor.service.ServiceSupport;
import com.arcsoft.supervisor.service.TransactionSupport;
import com.arcsoft.supervisor.service.server.OpsServerService;

import java.io.IOException;
import java.net.*;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zw.
 */
public abstract class AbstractOpsServerService<T extends AbstractOpsServer> extends ServiceSupport implements OpsServerService<T>, TransactionSupport {

    public static final int PORT = 9;
    public static final char SEPARATOR = ':';

    private final BaseOpsServerRepository<T> opsServerRepository;

    public AbstractOpsServerService(BaseOpsServerRepository<T> opsServerRepository) {
        this.opsServerRepository = opsServerRepository;
    }

    public BaseOpsServerRepository<T> getOpsServerRepository() {
        return opsServerRepository;
    }

    @Override
    public void save(T opsServer) {
        opsServerRepository.save(opsServer);
    }

    @Override
    public T getById(String id) {
        return opsServerRepository.findOne(id);
    }

    @Override
    public List<T> findAll() {
        return opsServerRepository.findAll();
    }

    @Override
    public void delete(String id) {
        T server = getById(id);
        if (server != null){
            opsServerRepository.delete(id);
        }

    }


    @Override
    public void WakeOpsServer(T server) throws SocketException, IllegalArgumentException, UnknownHostException, IOException {
        byte[] broadcast = getBroadcastAddress(server.getIp(), server.getNetmask());
        send(server.getMac(), broadcast);
    }

    private static byte[] getBroadcastAddress(String ip, String mask) throws UnknownHostException, SocketException {
        byte[] address = InetAddress.getByName(ip).getAddress();
        byte[] maskAddress = InetAddress.getByName(mask).getAddress();
        for(int i = 0; i < address.length; i++) {
            address[i] = (byte) (address[i] | (~maskAddress[i]));
        }
        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        for(NetworkInterface netint : Collections.list(nets)) {
            if(netint.isUp()) {
                Enumeration<InetAddress> inetaddes = netint.getInetAddresses();

                for(InetAddress add : Collections.list(inetaddes)) {

                }
            }
        }
        return address;
    }

    private static void send(String mac, byte[] ip) throws UnknownHostException, SocketException, IOException, IllegalArgumentException
    {
        send(mac, ip, PORT);
    }

    private static void send(String mac, byte[] ip, int port) throws UnknownHostException, SocketException, IOException, IllegalArgumentException
    {
        // validate MAC and chop into array
        final String[] hex = validateMac(mac);

        // doForward to base16 bytes
        final byte[] macBytes = new byte[6];
        for(int i=0; i<6; i++) {
            macBytes[i] = (byte) Integer.parseInt(hex[i], 16);
        }

        final byte[] bytes = new byte[102];

        // fill first 6 bytes
        for(int i=0; i<6; i++) {
            bytes[i] = (byte) 0xff;
        }
        // fill remaining bytes with target MAC
        for(int i=6; i<bytes.length; i+=macBytes.length) {
            System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
        }

        // create socket to IP
        final InetAddress address = InetAddress.getByAddress(ip);
        final DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, port);
        final DatagramSocket socket = new DatagramSocket();
        socket.send(packet);
        socket.close();

    }

    public static String cleanMac(String mac) throws IllegalArgumentException
    {
        final String[] hex = validateMac(mac);

        StringBuffer sb = new StringBuffer();
        boolean isMixedCase = false;

        // check for mixed case
        for(int i=0; i<6; i++) {
            sb.append(hex[i]);
        }
        String testMac = sb.toString();
        if((testMac.toLowerCase().equals(testMac) == false) && (testMac.toUpperCase().equals(testMac) == false)) {
            isMixedCase = true;
        }

        sb = new StringBuffer();
        for(int i=0; i<6; i++) {
            // doForward mixed case to lower
            if(isMixedCase == true) {
                sb.append(hex[i].toLowerCase());
            }else{
                sb.append(hex[i]);
            }
            if(i < 5) {
                sb.append(SEPARATOR);
            }
        }
        return sb.toString();
    }

    private static String[] validateMac(String mac) throws IllegalArgumentException
    {
        // error handle semi colons
        mac = mac.replace(";", ":");

        // attempt to assist the user a little
        String newMac = "";

        if(mac.matches("([a-zA-Z0-9]){12}")) {
            // expand 12 chars into a valid mac address
            for(int i=0; i<mac.length(); i++){
                if((i > 1) && (i % 2 == 0)) {
                    newMac += ":";
                }
                newMac += mac.charAt(i);
            }
        }else{
            newMac = mac;
        }

        // regexp pattern match a valid MAC address
        final Pattern pat = Pattern.compile("((([0-9a-fA-F]){2}[-:]){5}([0-9a-fA-F]){2})");
        final Matcher m = pat.matcher(newMac);

        if(m.find()) {
            String result = m.group();
            return result.split("(\\:|\\-)");
        }else{
            throw new IllegalArgumentException("Invalid MAC address");
        }
    }
}
