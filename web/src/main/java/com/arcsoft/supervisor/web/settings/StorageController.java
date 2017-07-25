package com.arcsoft.supervisor.web.settings;

import com.arcsoft.supervisor.model.domain.server.Server;
import com.arcsoft.supervisor.model.domain.settings.Storage;
import com.arcsoft.supervisor.service.server.ServerService;
import com.arcsoft.supervisor.service.settings.LocalStorageService;
import com.arcsoft.supervisor.service.settings.RemoteStorageService;
import com.arcsoft.supervisor.utils.app.ShellException;
import com.arcsoft.supervisor.web.JsonResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller class for <tt>storage</tt>.
 *
 * @author zw.
 */
@Controller
public class StorageController extends SettingsControllerSupport {

    @Autowired
    private RemoteStorageService remoteStorageService;

    @Autowired
    @Qualifier("storageService")
    private LocalStorageService localStorageService;

    @Autowired
    private ServerService serverService;


    /**
     * Saves the specified storage.
     *
     * @param storage the storage instance to be add
     * @return a json string contains result
     */
    @RequestMapping(value = "/storage/{id}", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult addStorage(@RequestBody Storage storage, @PathVariable String id) {
        if (CLUSTER_SERVER_ID.equals(id)) {
            localStorageService.addRemoteStorage(storage);
        } else {
            remoteStorageService.addRemoteStorage(serverService.getServer(id), storage);
        }
        return JsonResult.fromSuccess().put("id", storage.getId());
    }


    /**
     * Updates the specified storage
     *
     * @param storage the storage to be updated
     * @param id the identify value of server
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/storage/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public JsonResult updateStorage(@RequestBody Storage storage, @PathVariable String id) throws Exception {
        if (CLUSTER_SERVER_ID.equals(id)){
            localStorageService.updateStorage(storage);
        } else {
            remoteStorageService.updateStorage(serverService.getServer(id), storage);
        }
        return JsonResult.fromSuccess().put("id", storage.getId());
    }

    /**
     * Mount the specified storage.
     *
     * @param storageId the storage identify value
     * @return a json string contains result
     */
    @RequestMapping(value = "/storage/{id}/{storageId}", method = RequestMethod.PUT)
    @ResponseBody
    public JsonResult mountStorage(@PathVariable Integer storageId, @PathVariable String id) throws Exception {
        if (CLUSTER_SERVER_ID.equals(id)) {
            localStorageService.mountStorage(localStorageService.getRemoteStorage(storageId));
        } else {
            Server server = serverService.getServer(id);
            remoteStorageService.mountStorage(server, remoteStorageService.getRemoteStorage(server, storageId));
        }
        return JsonResult.fromSuccess();
    }

    /**
     * Un-mount the specified storage.
     *
     * @param storageId
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/storage/{id}/{storageId}", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult umountStorage(@PathVariable Integer storageId, @PathVariable  String id) throws Exception {
        if (CLUSTER_SERVER_ID.equals(id)) {
            localStorageService.umountStorage(localStorageService.getRemoteStorage(storageId));
        } else {
            Server server = serverService.getServer(id);
            remoteStorageService.umountStorage(server, remoteStorageService.getRemoteStorage(server, storageId));
        }
        return JsonResult.fromSuccess();
    }

    /**
     * Returns the specified <code>storageId</code> represents storage.
     *
     * @param id the identify value of storage
     * @return the id represents storage instance
     */
    @RequestMapping(value = "/storage/{id}/{storageId}", method = RequestMethod.GET)
    @ResponseBody
    public Storage getStorage(@PathVariable Integer storageId, @PathVariable String id) {
        if (CLUSTER_SERVER_ID.equals(id)) {
            return localStorageService.getRemoteStorage(storageId);
        }
        return remoteStorageService.getRemoteStorage(serverService.getServer(id), storageId);
    }

    /**
     * Deletes storage with specified <code>storageId</code>.
     *
     * @param storageId the identify value of storage
     * @return a json string contains result
     */
    @RequestMapping(value = "/storage/{id}/{storageId}", method = RequestMethod.DELETE)
    @ResponseBody
    public JsonResult deleteStorage(@PathVariable Integer storageId, @PathVariable String id) throws ShellException {
        JsonResult result = JsonResult.fromSuccess();
        if (CLUSTER_SERVER_ID.equals(id)){
            localStorageService.delRemoteStorage(storageId);
        } else {
            remoteStorageService.delRemoteStorage(serverService.getServer(id), storageId);
        }
        return result;
    }

    /**
     * Returns all of storage as list.
     *
     * @return a view path will be render the result
     */
    @RequestMapping(value = "/storages/{id}", method = RequestMethod.GET)
    @ResponseBody
    public List<Storage> listStorage(@PathVariable String id) throws ShellException {
        if (CLUSTER_SERVER_ID.equals(id)) {
            List<Storage> allOfStorages = localStorageService.findAllRemoteStorages();
            setStoragesStatus(allOfStorages, localStorageService.getRemoteMounted());
            return allOfStorages;
        }
        Server server = serverService.getServer(id);
        List<Storage> allOfStorages = remoteStorageService.findAllRemoteStorages(server);
        setStoragesStatus(allOfStorages, remoteStorageService.getRemoteMounted(server));
        return allOfStorages;
    }

    private void setStoragesStatus(List<Storage> allOfStorages, Map<String, String> mountedStorages){
        for (Storage storage : allOfStorages){
            String mountedPath = mountedStorages.get(storage.getName());
            if (StringUtils.isNotBlank(mountedPath) && mountedPath.equals(storage.getPath())){
                storage.setMounted(true);
            }
        }
    }


    /**
     * Returns storage with the specified <code>name</code>.
     *
     * @param name the name of storage
     * @return a json string contains result
     */
    @RequestMapping(value = "/storage/getByName/{id}", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult getStorageByName(String name, @PathVariable String id) {
        Storage storage = CLUSTER_SERVER_ID.equals(id) ? localStorageService.getRemoteStorageByName(name)
                : remoteStorageService.getRemoteStorageByName(serverService.getServer(id), name);
        return JsonResult.fromSuccess()
                .put("isNameExisted", storage != null);
    }


}
