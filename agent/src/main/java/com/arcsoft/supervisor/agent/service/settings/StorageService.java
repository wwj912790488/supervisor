package com.arcsoft.supervisor.agent.service.settings;

import com.arcsoft.supervisor.cluster.action.ActionErrorCode;
import com.arcsoft.supervisor.cluster.action.ActionHandler;
import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseResponse;
import com.arcsoft.supervisor.cluster.action.settings.storage.*;
import com.arcsoft.supervisor.cluster.app.ActionException;
import com.arcsoft.supervisor.cluster.app.Request;
import com.arcsoft.supervisor.cluster.app.Response;
import com.arcsoft.supervisor.exception.ObjectAlreadyExistsException;
import com.arcsoft.supervisor.model.domain.settings.Storage;
import com.arcsoft.supervisor.service.settings.LocalStorageService;
import com.arcsoft.supervisor.utils.app.ShellException;

import java.util.ArrayList;
import java.util.List;

/**
 * This service processes all storage relation requests.
 *
 * @author hxiang
 */
public class StorageService implements ActionHandler {

    private LocalStorageService localStorageService;

    public void setLocalStorageService(LocalStorageService localStorageService) {
        this.localStorageService = localStorageService;
    }

    @Override
    public int[] getActions() {
        return new int[]{
                Actions.STORAGE_FIND,
                Actions.STORAGE_FIND_REMOTE_MOUNTED,
                Actions.STORAGE_ADD,
                Actions.STORAGE_DELETE,
                Actions.STORAGE_MOUNT,
                Actions.STORAGE_UNMOUNT,
                Actions.STORAGE_UPDATE
        };
    }

    @Override
    public Response execute(Request request) throws ActionException {
        if (request instanceof FindStorageRequest) {
            return findStorage((FindStorageRequest) request);
        } else if (request instanceof FindRemoteMountedStorageRequest) {
            return findRemoteMountedStorage((FindRemoteMountedStorageRequest) request);
        } else if (request instanceof AddStorageRequest) {
            return addStorage((AddStorageRequest) request);
        } else if (request instanceof DeleteStorageRequest) {
            return deleteStorage((DeleteStorageRequest) request);
        } else if (request instanceof MountStorageRequest) {
            return mountStorage((MountStorageRequest) request);
        } else if (request instanceof UnmountStorageRequest) {
            return unMountStorage((UnmountStorageRequest) request);
        } else if (request instanceof UpdateStorageRequest) {
            return updateStorage((UpdateStorageRequest) request);
        }
        return null;
    }

    /**
     * @param request -update the storage
     * @return response -true or false
     */
    private Response updateStorage(UpdateStorageRequest request) {
        UpdateStorageResponse resp = new UpdateStorageResponse();
        resp.setErrorCode(ActionErrorCode.SUCCESS);
        try {
            localStorageService.updateStorage(request.getStorage());
        } catch (ShellException e) {
            resp.setErrorCode(ActionErrorCode.RUN_SHELL_FAILED);
        } catch (Exception e) {
            resp.setErrorCode(ActionErrorCode.UNKNOWN_ERROR);
        }
        return resp;
    }

    /**
     * @param request-find storage which is mounted
     * @return response -the list of storage.
     * @throws ShellException
     */
    private Response findRemoteMountedStorage(FindRemoteMountedStorageRequest request) {
        FindRemoteMountedStorageResponse resp = new FindRemoteMountedStorageResponse();
        resp.setErrorCode(ActionErrorCode.SUCCESS);
        try {
            resp.setStorageMap(localStorageService.getRemoteMounted());
        } catch (ShellException e) {
            resp.setErrorCode(ActionErrorCode.RUN_SHELL_FAILED);
        } catch (Exception e) {
            resp.setErrorCode(ActionErrorCode.UNKNOWN_ERROR);
        }
        return resp;
    }

    /**
     * @param request -find storage
     * @return response -storage list
     */
    private Response findStorage(FindStorageRequest request) {
        FindStorageResponse resp = new FindStorageResponse();
        resp.setErrorCode(ActionErrorCode.SUCCESS);
        if (request.getType() == FindStorageRequest.SearchType.REMOETE) {
            if (request.getId() != null) {
                List<Storage> list = new ArrayList<Storage>();
                Storage storage = localStorageService.getRemoteStorage(request.getId());
                if (storage != null)
                    list.add(storage);
                resp.setStorageList(list);
            } else if (request.getName() != null) {
                List<Storage> list = new ArrayList<Storage>();
                Storage storage = localStorageService.getRemoteStorageByName(request.getName());
                if (storage != null)
                    list.add(storage);
                resp.setStorageList(list);
            } else {
                resp.setStorageList(localStorageService.findAllRemoteStorages());
            }
        } else if (request.getType() == FindStorageRequest.SearchType.LOCAL) {
            if (request.getId() != null) {
                // List<Storage> list = new ArrayList<Storage>();
                // Storage storage = dao.getLocalStorage(request.getId());
                // if (storage != null)
                // list.add(storage);
                // resp.setStorageList(list);
            } else if (request.getName() != null) {
                // List<Storage> list = new ArrayList<Storage>();
                // Storage storage = dao.getLoaclStorageByName(request.getName());
                // if (storage != null)
                // list.add(storage);
                // resp.setStorageList(list);
            } else {
                // resp.setStorageList(storageDao.getStorages());
            }
        }
        return resp;
    }

    /**
     * @param request --including the storage which will be unmounted
     * @return response --the result of unmounting storage
     */
    private Response unMountStorage(UnmountStorageRequest request) {
        BaseResponse resp = new UnmountStorageResponse();
        resp.setErrorCode(ActionErrorCode.SUCCESS);
        try {
            localStorageService.umountStorage(request.getStorage());
        } catch (ShellException e) {
            resp.setErrorCode(ActionErrorCode.RUN_SHELL_FAILED);
        } catch (Exception e) {
            resp.setErrorCode(ActionErrorCode.UNKNOWN_ERROR);
        }
        return resp;
    }

    /**
     * @param request -including the storage which will be mounted
     * @return response -the result of mounting storage
     */
    private Response mountStorage(MountStorageRequest request) {
        BaseResponse resp = new MountStorageResponse();
        resp.setErrorCode(ActionErrorCode.SUCCESS);
        try {
            localStorageService.mountStorage(request.getStorage());
        } catch (ShellException e) {
            resp.setErrorCode(ActionErrorCode.RUN_SHELL_FAILED);
        } catch (Exception e) {
            resp.setErrorCode(ActionErrorCode.UNKNOWN_ERROR);
        }
        return resp;
    }

    /**
     * @param request -including the storage which will be deleted
     * @return response -the result of deleting storage
     */
    private Response deleteStorage(DeleteStorageRequest request) {
        BaseResponse resp = new DeleteStorageResponse();
        resp.setErrorCode(ActionErrorCode.SUCCESS);
        try {
            localStorageService.delRemoteStorage(request.getId());
        } catch (Exception e) {
            resp.setErrorCode(ActionErrorCode.UNKNOWN_ERROR);
        }
        return resp;
    }

    /**
     * @param request -including the storage which will be added
     * @return response -the result of adding storage
     */
    private Response addStorage(AddStorageRequest request) {
        AddStorageResponse resp = new AddStorageResponse();
        resp.setErrorCode(ActionErrorCode.SUCCESS);
        try {
            Storage storage = request.getStorage();
            localStorageService.addRemoteStorage(storage);
            resp.setStorage(storage);
        } catch (Exception e) {
            resp.setErrorCode(e instanceof ObjectAlreadyExistsException ? ActionErrorCode.STORAGE_NAME_EXISTED
                    : ActionErrorCode.UNKNOWN_ERROR);
        }
        return resp;
    }

}
