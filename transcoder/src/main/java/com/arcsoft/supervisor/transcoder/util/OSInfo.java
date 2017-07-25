package com.arcsoft.supervisor.transcoder.util;

import java.io.File;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Bing
 */
public class OSInfo {

    public static final int WINDOW = 0x10;
    public static final int LINUX = 0x20;
    public static final int CENTOS = 0x21;
    public static final int UBUNTU = 0x22;

    private static int os = 0;
    private static int gpuCount = -1;

    /**
     * @return window, centos or ubuntu
     */
    public static String getOSName() {
        switch (getOS()) {
            case WINDOW:
                return "window";
            case CENTOS:
                return "centos";
            case UBUNTU:
                return "ubuntu";
            default:
                break;
        }
        return "";
    }

    public static int getOS() {
        if (os == 0) {
            String osname = System.getProperty("os.name");
            if (osname == null)
                osname = "";
            osname = osname.toLowerCase();
            if (osname.indexOf("win") != -1) {
                os = WINDOW;
            } else if (osname.indexOf("linux") != -1) {
                os = isCentOS() ? CENTOS : UBUNTU;
            }
        }
        return os;
    }

    public static boolean isRuntimeOSSupported() {
        return getOS() == CENTOS || getOS() == UBUNTU;
    }

    public static int getGPUCount() {
        if (gpuCount == -1) {
            gpuCount = getGPUCountFromCmd();
        }
        return gpuCount;
    }

    public static String getDeviceSerialNumber() {
        String cmd;
        cmd = "dmidecode -s system-serial-number";
        StringWriter out = new StringWriter();
        SystemExecutor.execOSCommand(cmd, out);
        return out.toString();
    }

    private static boolean isCentOS() {
        File f = new File("/etc/centos-release");
        return f.exists();
    }

    /**
     * @return 0 or gpu count
     */
    private static int getGPUCountFromCmd() {
        StringWriter out = new StringWriter();
        SystemExecutor.execOSCommand("nvidia-smi -L", out);
        return parseGPUCount(out.toString());
    }

    private static int parseGPUCount(String output) {

        //[root@ArcVideo ~]# nvidia-smi -L
        //GPU 0: GRID K340 (UUID: GPU-5074e01a-03cb-8783-c7e9-eed9c1da8454)
        //GPU 1: GRID K340 (UUID: GPU-5009afbc-a45d-4f20-f405-6cf06ea8c126)
        //GPU 2: GRID K340 (UUID: GPU-3edf8cb2-5300-5825-eaf4-279688e4453f)
        //GPU 3: GRID K340 (UUID: GPU-2e9fe0c8-95c3-7e2d-ba3a-36dc4ed2fe88)
        int count = 0;
        Pattern tk = Pattern.compile("^\\s*GPU\\s?(\\d+)");
        String[] res = output.split("[\\r\\n]+");

        for (int i = 0; i < res.length; i++) {
            String line = res[i].trim();
            Matcher m = tk.matcher(line);
            if (m.find()) {
                count++;
            }
        }

        return count;
    }

}
