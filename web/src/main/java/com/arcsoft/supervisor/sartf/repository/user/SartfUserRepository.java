package com.arcsoft.supervisor.sartf.repository.user;

import com.arcsoft.supervisor.commons.profile.Sartf;
import com.arcsoft.supervisor.model.domain.server.SartfOpsServer;
import com.arcsoft.supervisor.model.domain.user.SartfUser;
import com.arcsoft.supervisor.repository.user.BaseUserRepository;

@Sartf
public interface SartfUserRepository extends BaseUserRepository<SartfUser> {

    SartfUser findByOps(SartfOpsServer ops);

}
