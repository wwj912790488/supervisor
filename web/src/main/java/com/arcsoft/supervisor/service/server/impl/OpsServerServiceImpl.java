package com.arcsoft.supervisor.service.server.impl;

import com.arcsoft.supervisor.commons.profile.Production;
import com.arcsoft.supervisor.model.domain.server.OpsServer;
import com.arcsoft.supervisor.repository.server.OpsServerRepository;
import com.arcsoft.supervisor.service.server.OpsServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Default implementation for {@link OpsServerService}.
 *
 * @author zw.
 */
@Service
@Production
public class OpsServerServiceImpl extends AbstractOpsServerService<OpsServer> {


    @Autowired
	public OpsServerServiceImpl(OpsServerRepository opsServerRepository) {
		super(opsServerRepository);
	}
}
