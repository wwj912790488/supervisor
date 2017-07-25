package com.arcsoft.supervisor.repository.settings.impl;

import com.arcsoft.supervisor.repository.settings.HostRepository;
import com.arcsoft.supervisor.utils.app.App;
import org.springframework.stereotype.Repository;

import java.io.StringWriter;

/**
 * Implement SystemServiceImpl for centOS.
 *
 * @author hxiang
 */
@Repository
public class HostRepositoryImplRHEL implements HostRepository {

    public void reboot() {
        String cmd = "reboot";

        try {
            StringWriter sw = new StringWriter();
            App.syncExec(null, cmd, sw);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void shutdown() {
        String cmd = "shutdown -P 0";

        try {
            StringWriter sw = new StringWriter();
            App.syncExec(null, cmd, sw);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
