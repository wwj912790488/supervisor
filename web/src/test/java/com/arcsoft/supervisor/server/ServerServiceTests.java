package com.arcsoft.supervisor.server;

import com.arcsoft.supervisor.ProductionTestSupport;
import com.arcsoft.supervisor.service.server.ServerService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

/**
 * @author zw.
 */
public class ServerServiceTests extends ProductionTestSupport {

    @Autowired
    private ServerService serverService;

    @Test
    public void tests(){
        String[] ids = {"1", "2", "3"};
        serverService.getByIds(Arrays.asList(ids));
    }


}
