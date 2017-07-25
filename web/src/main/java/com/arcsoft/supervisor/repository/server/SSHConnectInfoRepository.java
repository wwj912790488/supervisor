package com.arcsoft.supervisor.repository.server;

import com.arcsoft.supervisor.model.domain.server.SSHConnectInfo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by wwj on 2017/6/21.
 */
public interface SSHConnectInfoRepository extends JpaRepository<SSHConnectInfo,Long> {
     SSHConnectInfo findByIp(String ip);

}
