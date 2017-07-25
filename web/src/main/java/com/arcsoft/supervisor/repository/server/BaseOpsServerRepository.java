package com.arcsoft.supervisor.repository.server;

import com.arcsoft.supervisor.model.domain.server.AbstractOpsServer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * @author zw.
 */
@NoRepositoryBean
public interface BaseOpsServerRepository<T extends AbstractOpsServer> extends JpaRepository<T, String> {
}
