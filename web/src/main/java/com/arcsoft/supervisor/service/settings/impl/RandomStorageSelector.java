package com.arcsoft.supervisor.service.settings.impl;

import com.arcsoft.supervisor.model.domain.settings.Storage;
import com.arcsoft.supervisor.service.settings.LocalStorageService;
import com.arcsoft.supervisor.service.settings.StorageSelector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

/**
 * A random storage selector of {@link StorageSelector}.This implementation will
 * random select a storage to use.
 *
 * @author zw.
 */
@Service("randomStorageSelector")
public class RandomStorageSelector implements StorageSelector {

    @Autowired
    @Qualifier("storageService")
    private LocalStorageService localStorageService;

    @Override
    public Storage select() {
        List<Storage> allOfStorages = localStorageService.findAllRemoteStorages();
        if (!allOfStorages.isEmpty()) {
            return allOfStorages.get(
                    getRandomNumber(allOfStorages.size())
            );
        }
        return null;
    }

    private int getRandomNumber(int maxNumber) {
        return new Random().nextInt(maxNumber);
    }

}
