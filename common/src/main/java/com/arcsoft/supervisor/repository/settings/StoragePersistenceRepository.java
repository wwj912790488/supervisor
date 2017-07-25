package com.arcsoft.supervisor.repository.settings;

import com.arcsoft.supervisor.model.domain.settings.Storage;

import java.util.List;

/**
 * This service used to persist storage.
 *
 * @author hxiang
 */

public interface StoragePersistenceRepository {

    Storage get(Integer id);

    Integer save(Storage storage);

    boolean update(Storage storage);

    void delete(Integer id);

    public List<Storage> get();
}