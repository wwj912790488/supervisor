package com.arcsoft.supervisor.service.settings;

import com.arcsoft.supervisor.model.domain.system.Configuration;

/**
 * Defines generic logically for {@link Configuration}.
 *
 * @author zw.
 */
public interface ConfigurationService<T extends Configuration> {

    /**
     * Saves or update the given configuration.
     *
     * @param t the configuration
     * @return the persisted configuration
     */
    T saveOrUpdate(T t);

    /**
     * Retrieves the first configuration.
     *
     * @return the first configuration or {@code null} if
     * hasn't any configurations
     */
    T find();


    /**
     * Retrieves the Configuration from cache.
     *
     * @return the cached configuration
     */
    T getFromCache();

}
