package com.arcsoft.supervisor.repository.server;

import com.arcsoft.supervisor.model.domain.server.Server;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

/**
 * A jpa interface to addition some convenient method.
 *
 * @author zw.
 */
public interface ServerJpaRepository extends ServerRepository,JpaRepository<Server, String> {

    public Server findByIpAndPort(String ip, int port);

    public List<Server> findByJoinedAndTypeAndAlive(boolean joined, int type, boolean alived);

    public List<Server> findByJoinedAndType(boolean joined, int type);

    public List<Server> findByIdIn(Collection<String> ids);

    public Page<Server> findByJoinedTrue(Pageable pageable);

    public List<Server> findByJoinedTrueAndAliveTrueAndType(int type);
    
    public Server findByJoinedTrueAndAliveTrueAndId(String id);
}
