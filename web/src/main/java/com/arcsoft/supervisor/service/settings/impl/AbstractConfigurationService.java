package com.arcsoft.supervisor.service.settings.impl;

import com.arcsoft.supervisor.model.domain.system.Configuration;
import com.arcsoft.supervisor.service.ServiceSupport;
import com.arcsoft.supervisor.service.settings.ConfigurationService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Skeletal implementation for {@link ConfigurationService}.
 *
 * @author zw.
 */
public abstract class AbstractConfigurationService<T extends Configuration> extends ServiceSupport
        implements ConfigurationService<T> {

    private final JpaRepository<T, Integer> repository;
    private final Object lock;
    protected final AtomicReference<T> cachedConfiguration;

    protected AbstractConfigurationService(JpaRepository<T, Integer> repository) {
        this.repository = repository;
        this.lock = new Object();
        this.cachedConfiguration = new AtomicReference<>(this.getInitializedConfiguration());
    }


    @Override
    public T saveOrUpdate(T t) {
        synchronized (lock) {
            T entity = find();
            if (entity == null) {
                entity = repository.save(t);
            } else {
                BeanUtils.copyProperties(t, entity, "id");
            }
            cachedConfiguration.getAndSet(entity);
            return entity;
        }
    }

    @Override
    public T find() {
        List<T> configurations = repository.findAll(new Sort(Sort.Direction.DESC, "id"));
        return configurations.isEmpty() ? null : configurations.get(0);
    }

    @Override
    public T getFromCache() {
        return cachedConfiguration.get();
    }

    protected T getInitializedConfiguration() {
        return this.find();
    }
}
