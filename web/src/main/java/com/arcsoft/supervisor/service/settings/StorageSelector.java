package com.arcsoft.supervisor.service.settings;

import com.arcsoft.supervisor.model.domain.settings.Storage;

/**
 * An storage selector.The selector will be select a storage to use.
 *
 * @author zw.
 */
public interface StorageSelector {


    /**
     * Selects an storage.
     *
     * @return a instance of storage or <code>null</code> if their is nothing can be use
     */
    public Storage select();

}
