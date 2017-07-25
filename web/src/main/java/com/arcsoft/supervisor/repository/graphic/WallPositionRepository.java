package com.arcsoft.supervisor.repository.graphic;

import com.arcsoft.supervisor.model.domain.graphic.WallPosition;
import com.arcsoft.supervisor.model.domain.server.OpsServer;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author zw.
 */
public interface WallPositionRepository extends JpaRepository<WallPosition, Integer> {

	public void deleteById(Integer id);

    public WallPosition findByOpsServer(OpsServer server);

    public WallPosition findByOutput(String output);
}
