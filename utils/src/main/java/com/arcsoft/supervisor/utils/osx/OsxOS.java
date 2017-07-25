package com.arcsoft.supervisor.utils.osx;

import com.arcsoft.supervisor.utils.OS;
import com.arcsoft.supervisor.utils.app.App;
import com.arcsoft.supervisor.utils.app.ShellException;

/**
 * @author zw.
 */
public class OsxOS extends OS {

    @Override
    public String getHostName() {
        try {
            return App.runShell("hostname").get(0);
        } catch (ShellException e) {

        }
        return null;
    }

    @Override
    public String getProcessorId() {
        return null;
    }

    @Override
    public String getBaseBoardId() {
        return null;
    }

    @Override
    public String getSystemUUID() {
        try {
            return App.runShell("ioreg -d2 -c IOPlatformExpertDevice | grep IOPlatformUUID | awk -F '=' '{print $2}'")
                    .get(0).replaceAll("\"", "");
        } catch (ShellException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getDeviceName() {
        return null;
    }
}
