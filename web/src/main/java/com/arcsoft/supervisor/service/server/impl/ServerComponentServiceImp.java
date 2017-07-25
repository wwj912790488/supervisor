package com.arcsoft.supervisor.service.server.impl;

import com.arcsoft.supervisor.cluster.action.ActionHandler;
import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.server.ListComponentRequest;
import com.arcsoft.supervisor.cluster.action.server.ListComponentResponse;
import com.arcsoft.supervisor.cluster.app.ActionException;
import com.arcsoft.supervisor.cluster.app.Request;
import com.arcsoft.supervisor.cluster.app.Response;
import com.arcsoft.supervisor.model.domain.server.ComponentType;
import com.arcsoft.supervisor.model.domain.server.Server;
import com.arcsoft.supervisor.model.domain.server.ServerComponent;
import com.arcsoft.supervisor.model.domain.settings.Component;
import com.arcsoft.supervisor.repository.server.ServerComponentRepository;
import com.arcsoft.supervisor.service.ServiceSupport;
import com.arcsoft.supervisor.service.TransactionSupport;
import com.arcsoft.supervisor.service.server.ServerComponentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ServerComponentServiceImp extends ServiceSupport implements ServerComponentService, ActionHandler, TransactionSupport {
	
	@Autowired
	private ServerComponentRepository componentRepository;
	
    private Map<String, List<ServerComponent>> serverComponentStatus = new ConcurrentHashMap<>();

	@Override
	public ServerComponent save(ServerComponent component) {
		return componentRepository.save(component);
	}

	@Override
	public List<ServerComponent> getByServer(Server server) {
		ArrayList<ServerComponent> allList = new ArrayList<>();
		List<ServerComponent> list = serverComponentStatus.get(server.getId());
		if(list != null) {
			allList.addAll(list);
		}
		List<ServerComponent> dblist = componentRepository.findByServer(server);
		if(dblist != null) {
			allList.addAll(dblist);
		}
		return allList;
	}
	
	@Override
	public List<ServerComponent> getSdiOutputs() {
		return componentRepository.findByTypeOrderByServerDescIdAsc(ComponentType.SDI);
	}
	
	@Override
    public int[] getActions() {
        return new int[]{
                Actions.LIST_COMPONENTS
        };
    }
	
	@Override
    @Transactional
    public Response execute(Request request) throws ActionException {
        if (request instanceof ListComponentRequest) {
        	return processListComponent((ListComponentRequest) request);
        }
        return null;
    }
	
    private ListComponentResponse processListComponent(ListComponentRequest request) {
    	ArrayList<ServerComponent> list = new ArrayList<>();
    	for(Component component : request.getComponents()) {
    		boolean unkownType = false;
    		ServerComponent serverComponent = new ServerComponent();
    		serverComponent.setName(component.getId());
    		serverComponent.setTotal(component.getTotal());
    		serverComponent.setUsage(component.getUsed());
    		
    		switch(component.getType()) {
    		case 0:
    			serverComponent.setType(ComponentType.MEMORY);
    			break;
    		case 1:
    			serverComponent.setType(ComponentType.CPU);
    			break;
    		case 2:
    			serverComponent.setType(ComponentType.GPU);
    			break;
    		case 3:
    			serverComponent.setType(ComponentType.NETWORK);
    			break;
    		default:
    			unkownType = true;
    		}
    		if(!unkownType) {
    			list.add(serverComponent);
    		}
    	}
    	serverComponentStatus.put(request.getId(), list);
    	return new ListComponentResponse();
    }

}
