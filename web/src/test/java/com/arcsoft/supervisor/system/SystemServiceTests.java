package com.arcsoft.supervisor.system;

import com.arcsoft.supervisor.ProductionTestSupport;
import com.arcsoft.supervisor.cluster.ClusterType;
import com.arcsoft.supervisor.model.domain.system.SystemSettings;
import com.arcsoft.supervisor.service.cluster.ClusterService;
import com.arcsoft.supervisor.service.system.SystemService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author zw.
 */
public class SystemServiceTests extends ProductionTestSupport {

    @Autowired
    private SystemService systemService;

    @Autowired
    private ClusterService clusterService;

    @Test
    public void testSaveSystemSetting(){
        SystemSettings settings = new SystemSettings();
        settings.setClusterIp("239.8.8.1");
        settings.setClusterPort(8901);
        settings.setClusterType(ClusterType.CORE);
        settings.setTimeToLive(1);
        settings.setHeartbeatInterval(100);
        settings.setHeartbeatTimeout(2000);
        settings.setBindAddr("172.17.187.23");
        systemService.saveSettings(settings);

    }

    @Test
    public void testSearchNode(){
        try {
            clusterService.search(9000);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            TimeUnit.SECONDS.sleep(60);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
