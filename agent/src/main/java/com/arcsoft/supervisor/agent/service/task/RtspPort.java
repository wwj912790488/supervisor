package com.arcsoft.supervisor.agent.service.task;

/**
 * Port class for <code>rtsp</code> task to holds ports.
 *
 * @author zw
 */
public class RtspPort {

    private final int sdPort;
    private final int hdPort;

    public RtspPort(int sdPort, int hdPort) {
        this.sdPort = sdPort;
        this.hdPort = hdPort;
    }

    public int getSdPort() {
        return sdPort;
    }

    public int getHdPort() {
        return hdPort;
    }
}
