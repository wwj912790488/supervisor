package com.arcsoft.supervisor.repository.server;

import com.arcsoft.supervisor.model.domain.server.ComponentType;
import com.arcsoft.supervisor.model.domain.server.Server;
import com.arcsoft.supervisor.model.domain.server.ServerComponent;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ServerComponentRepository extends CrudRepository<ServerComponent, Integer> {
	
	public List<ServerComponent> findByServer(Server server);
	
	public ServerComponent findByServerAndName(Server server, String name);
	
	public List<ServerComponent> findByTypeOrderByServerDescIdAsc(ComponentType type);
}
