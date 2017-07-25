package com.arcsoft.supervisor.sartf.service.server.impl;

import com.arcsoft.supervisor.commons.json.JsonMapper;
import com.arcsoft.supervisor.commons.profile.Sartf;
import com.arcsoft.supervisor.model.domain.server.SartfOpsServer;
import com.arcsoft.supervisor.model.dto.rest.server.OpsServerChannel;
import com.arcsoft.supervisor.sartf.service.server.SartfOpsServerOperator;
import com.arcsoft.supervisor.service.server.impl.AbstractOpsServerOperator;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
@Sartf
public class SartfOpsServerOperatorImpl extends AbstractOpsServerOperator<SartfOpsServer> implements SartfOpsServerOperator {

    @Override
    public void start(final String id, final String ip, final String port, final String url) {
        getPool().execute(new Runnable() {
            @Override
            public void run() {
                logger.info("Ready to send start request to ops [ip={}, url={}]", ip, url);
                try {
                    String result = doPost(getStartPath(), ip, port, JsonMapper.getMapper().writeValueAsBytes(OpsServerChannel.build(id, ip, port, url)));
                    logger.info("Get result [{}] of start [ip={}] with data [url={}]", new Object[]{result, ip, url});
                } catch (IOException e) {
                    logger.error("Failed to do start operation on OpsServer [ip=" + ip + "], exception: " + e.getMessage());
                }

            }
        });
    }

    @Override
    public void stop(final String ip, final String port) {
        getPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    doPost(getStopPath(), ip, port, new byte[0]);
                } catch (IOException e) {
                    logger.error("Failed to do stop operation on the OpsServer [ip=" + ip + "], exception: " + e.getMessage());
                }
            }
        });
    }


    @Override
    protected SartfOpsServer getOpsServer(String ip, String port) {
        return new SartfOpsServer(ip, port);
    }
}
