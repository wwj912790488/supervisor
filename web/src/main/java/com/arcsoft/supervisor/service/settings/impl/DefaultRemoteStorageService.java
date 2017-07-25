package com.arcsoft.supervisor.service.settings.impl;

import com.arcsoft.supervisor.cluster.action.settings.storage.*;
import com.arcsoft.supervisor.exception.server.RemoteException;
import com.arcsoft.supervisor.model.domain.server.Server;
import com.arcsoft.supervisor.model.domain.settings.Storage;
import com.arcsoft.supervisor.service.remote.RemoteExecutorServiceSupport;
import com.arcsoft.supervisor.service.settings.RemoteStorageService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Default implementation for {@link RemoteStorageService}.
 *
 * @author zw.
 */
@Service
public class DefaultRemoteStorageService extends RemoteExecutorServiceSupport implements RemoteStorageService {


    @Override
    public Map<String, String> getRemoteMounted(Server agent) {
        FindRemoteMountedStorageRequest request = new FindRemoteMountedStorageRequest();
        FindRemoteMountedStorageResponse response = (FindRemoteMountedStorageResponse) remoteExecutorService
                .remoteExecute(request, agent);
        return response.getStorageMap();
    }

    @Override
    public void mountStorage(Server agent, Storage s) {
        MountStorageRequest request = new MountStorageRequest();
        request.setStorage(s);
        MountStorageResponse response = (MountStorageResponse) remoteExecutorService
                .remoteExecute(request, agent);
        if (!response.isSuccess()){
            throw new RemoteException(agent, response.getErrorCode());
        }

    }

    @Override
    public void umountStorage(Server agent, Storage s) {
        UnmountStorageRequest request = new UnmountStorageRequest();
        request.setStorage(s);
        UnmountStorageResponse response = (UnmountStorageResponse) remoteExecutorService
                .remoteExecute(request, agent);
        if (!response.isSuccess()){
            throw new RemoteException(agent, response.getErrorCode());
        }

    }

    @Override
    public void addRemoteStorage(Server agent, Storage st) {
        AddStorageRequest request = new AddStorageRequest();
        request.setStorage(st);
        AddStorageResponse response = (AddStorageResponse) remoteExecutorService
                .remoteExecute(request, agent);
        if (!response.isSuccess()){
            throw new RemoteException(agent, response.getErrorCode());
        }
        st.setId(response.getStorage().getId());
    }

    @Override
    public void delRemoteStorage(Server agent, Integer id) {
        DeleteStorageRequest request = new DeleteStorageRequest();
        request.setId(id);
        DeleteStorageResponse response = (DeleteStorageResponse) remoteExecutorService
                .remoteExecute(request, agent);
        if (!response.isSuccess()){
            throw new RemoteException(agent, response.getErrorCode());
        }
    }

    @Override
    public List<Storage> findAllRemoteStorages(Server agent) {
        FindStorageRequest request = new FindStorageRequest();
        request.setType(FindStorageRequest.SearchType.REMOETE);
        FindStorageResponse response = (FindStorageResponse) remoteExecutorService
                .remoteExecute(request, agent);
        return response.getStorageList();
    }

    @Override
    public Storage getRemoteStorage(Server agent, Integer id) {
        Storage ret = null;
        FindStorageRequest request = new FindStorageRequest();
        request.setId(id);
        request.setType(FindStorageRequest.SearchType.REMOETE);
        FindStorageResponse response = (FindStorageResponse) remoteExecutorService
                .remoteExecute(request, agent);
        if (response.getStorageList().size() > 0){
            ret = response.getStorageList().get(0);
        }
        return ret;
    }

    @Override
    public Storage getRemoteStorageByName(Server agent, String name) {
        Storage ret = null;
        FindStorageRequest request = new FindStorageRequest();
        request.setName(name);
        request.setType(FindStorageRequest.SearchType.REMOETE);
        FindStorageResponse response = (FindStorageResponse) remoteExecutorService
                .remoteExecute(request, agent);
        if (response.getStorageList().size() > 0){
            ret = response.getStorageList().get(0);
        }
        return ret;
    }

    @Override
    public void updateStorage(Server agent, Storage storage) {
        UpdateStorageRequest request = new UpdateStorageRequest();
        request.setStorage(storage);
        UpdateStorageResponse response = (UpdateStorageResponse) remoteExecutorService
                .remoteExecute(request, agent);
        if (!response.isSuccess()){
            throw new RemoteException(agent, response.getErrorCode());
        }

    }

}
