package com.arcsoft.supervisor.service.server.impl;

import com.arcsoft.supervisor.commons.profile.Production;
import com.arcsoft.supervisor.model.domain.server.OpsServer;
import org.springframework.stereotype.Service;

/**
 * Default implementation for {@link com.arcsoft.supervisor.service.server.OpsServerOperator}
 * to communicate with <code>ops server</code>.
 *
 * @author zw.
 */
@Service
@Production
public class DefaultOpsServerOperator extends AbstractOpsServerOperator<OpsServer> {

    @Override
    protected OpsServer getOpsServer(String ip, String port) {
        return new OpsServer(ip, port);
    }
}
