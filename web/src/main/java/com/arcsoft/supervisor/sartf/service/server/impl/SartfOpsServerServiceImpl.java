package com.arcsoft.supervisor.sartf.service.server.impl;

import com.arcsoft.supervisor.commons.profile.Sartf;
import com.arcsoft.supervisor.model.domain.server.SartfOpsServer;
import com.arcsoft.supervisor.sartf.repository.server.SartfOpsServerRepository;
import com.arcsoft.supervisor.service.server.impl.AbstractOpsServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Sartf
public class SartfOpsServerServiceImpl extends AbstractOpsServerService<SartfOpsServer> {

    @Autowired
    public SartfOpsServerServiceImpl(SartfOpsServerRepository opsServerRepository) {
        super(opsServerRepository);
    }
}
