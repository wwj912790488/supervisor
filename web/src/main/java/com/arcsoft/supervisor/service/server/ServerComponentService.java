package com.arcsoft.supervisor.service.server;

import com.arcsoft.supervisor.model.domain.server.Server;
import com.arcsoft.supervisor.model.domain.server.ServerComponent;

import java.util.List;

public interface ServerComponentService {
	public ServerComponent save(ServerComponent component);
	
	public List<ServerComponent> getByServer(Server server);
	
	public List<ServerComponent> getSdiOutputs();
	
}
