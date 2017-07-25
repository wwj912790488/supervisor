package com.arcsoft.supervisor.sartf.service.server;

import com.arcsoft.supervisor.model.domain.server.SartfOpsServer;
import com.arcsoft.supervisor.service.server.OpsServerOperator;


public interface SartfOpsServerOperator extends OpsServerOperator<SartfOpsServer> {

    void start(String id, String ip, String port, String url);

    void stop(String ip, String port);

}
