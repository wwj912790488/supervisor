package com.arcsoft.supervisor.service.settings.impl;

import com.arcsoft.supervisor.exception.ObjectAlreadyExistsException;
import com.arcsoft.supervisor.model.domain.settings.OutputPathValidateResult;
import com.arcsoft.supervisor.model.domain.settings.Storage;
import com.arcsoft.supervisor.repository.settings.StorageMountRepository;
import com.arcsoft.supervisor.repository.settings.StoragePersistenceRepository;
import com.arcsoft.supervisor.service.settings.LocalStorageService;
import com.arcsoft.supervisor.utils.NamedThreadFactory;
import com.arcsoft.supervisor.utils.app.ShellException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The implementation of LocalStorageService to add operations for storage.
 *
 * @author zw
 */
public class LocalStorageServiceImpl implements LocalStorageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalStorageServiceImpl.class);

    private StoragePersistenceRepository persistenceRepository;

    private StorageMountRepository mountRepository;

    public void initialize() {
        // Auto mount the storages;
        List<Storage> storages = persistenceRepository.get();
        if (storages.size() > 0) {
            ExecutorService es = Executors.newSingleThreadExecutor(NamedThreadFactory.create("LocalStorageService:" +
                    "initialize"));
            for (final Storage each : storages) {
                es.execute(new Runnable() {
                    public void run() {
                        try {
                            mountRepository.mount(each);
                        } catch (Exception e) {
                            LOGGER.error("Failed to do mount for " + each);
                        }
                    }
                });
            }
            es.shutdown();
        }
    }

    @Override
    public Map<String, String> getRemoteMounted() throws ShellException {
        return mountRepository.getMounted();
    }

    @Override
    public void mountStorage(Storage s) throws Exception {
        mountRepository.mount(s);
        if (getRemoteStorageByName(s.getName()) == null) { //Save it if not exists
            persistenceRepository.save(s);
        }
    }

    @Override
    public void umountStorage(Storage s) throws Exception {
        mountRepository.unmount(s, false);
    }

    @Override
    public void addRemoteStorage(Storage st) {
        Storage existedStorage = getRemoteStorageByName(st.getName());
        if (existedStorage != null) {
            throw new ObjectAlreadyExistsException("The " + st + " is existed.");
        }
        persistenceRepository.save(st);
    }

    @Override
    public void delRemoteStorage(Integer id) throws ShellException {
        Storage st = getRemoteStorage(id);
        if (st != null)
            mountRepository.unmount(st, true);
        persistenceRepository.delete(id);
    }

    @Override
    public List<Storage> findAllRemoteStorages() {
        return persistenceRepository.get();
    }

    @Override
    public Storage getRemoteStorage(Integer id) {
        return persistenceRepository.get(id);
    }

    @Override
    public Storage getRemoteStorageByName(String name) {
        Storage ret = null;
        for (Storage s : findAllRemoteStorages()) {
            if (s.getName().equals(name)) {
                ret = s;
                break;
            }
        }
        return ret;
    }

    @Override
    public void updateStorage(Storage s) throws Exception {
        Storage oldStorage = new Storage();
        Storage storage = persistenceRepository.get(s.getId());
        if (!s.getName().equals(storage.getName()) && getRemoteStorageByName(s.getName()) != null) {
            throw new ObjectAlreadyExistsException("The " + s + " is existed.");
        }
        oldStorage.setId(storage.getId());
        oldStorage.setName(storage.getName());
        oldStorage.setPath(storage.getPath());
        oldStorage.setUser(storage.getUser());
        oldStorage.setPwd(storage.getPwd());
        oldStorage.setType(storage.getType());
        if (!oldStorage.equals(s)) {
            // umount storage and delete folder.
            mountRepository.unmount(oldStorage, true);
            persistenceRepository.update(s);
        }
    }

    @Override
    public boolean isMounted(String path) throws ShellException {
        return this.mountRepository.isMounted(path);
    }

    @Override
    public OutputPathValidateResult isMounted(List<String> paths) throws ShellException {
        return this.mountRepository.isMounted(paths);
    }

    @Override
    public String getMountBaseDir() {
        return this.mountRepository.getMountBaseDir();
    }

    public void setPersistenceRepository(StoragePersistenceRepository persistenceRepository) {
        this.persistenceRepository = persistenceRepository;
    }

    public void setMountRepository(StorageMountRepository mountRepository) {
        this.mountRepository = mountRepository;
    }
}
