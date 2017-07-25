package com.arcsoft.supervisor.service.profile.impl;

import com.arcsoft.supervisor.exception.service.BusinessExceptionDescription;
import com.arcsoft.supervisor.model.domain.task.Profile;
import com.arcsoft.supervisor.service.converter.Converter;
import com.arcsoft.supervisor.service.profile.ProfileService;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author zw.
 */
public abstract class AbstractProfileService<T extends Profile, D> implements ProfileService<T, D> {

    private final JpaRepository<T, Integer> repository;
    private final Converter<D, T> converter;

    public AbstractProfileService(JpaRepository<T, Integer> repository, Converter<D, T> converter) {
        this.repository = repository;
        this.converter = converter;
    }

    @Override
    public T save(D dto) {
        T newEntity;
        try {
            newEntity = converter.doForward(dto);
        } catch (Exception e) {
            throw BusinessExceptionDescription.CONVERT_INPUT_ARGUMENTS_FAILED.withException(e);
        }
        return repository.save(newEntity);
    }

    @Override
    public void delete(int id) {
        T entity = repository.findOne(id);
        if (entity != null) {
            repository.delete(entity);
        }
    }

    @Override
    public void deleteAll(List<Integer> ids) {
        List<T> entities = FluentIterable.from(ids)
                .transform(new Function<Integer, T>() {
                    @Nullable
                    @Override
                    public T apply(Integer id) {
                        return repository.findOne(id);
                    }
                })
                .filter(new Predicate<T>() {
                    @Override
                    public boolean apply(@Nullable T input) {
                        return input != null;
                    }
                })
                .toList();
        repository.delete(entities);
    }

    @Override
    public D find(int id) {
        T entity = repository.findOne(id);
        if (entity != null) {
            try {
                return converter.doBack(entity);
            } catch (Exception e) {
                throw BusinessExceptionDescription.CONVERT_INPUT_ARGUMENTS_FAILED.withException(e);
            }
        }
        return null;
    }

    @Override
    public Page<T> paginate(PageRequest pageRequest) {
        return repository.findAll(pageRequest);
    }

    @Override
    public List<D> findAll() {
        List<T> entities = repository.findAll();
        return FluentIterable.from(entities)
                .transform(new Function<T, D>() {
                    @Nullable
                    @Override
                    public D apply(@Nullable T entity) {
                        try {
                            return converter.doBack(entity);
                        } catch (Exception e) {
                            throw BusinessExceptionDescription.ERROR.withException(e);
                        }
                    }
                })
                .toList();
    }

    @Override
    public List<T> findAllProfile() {
        return repository.findAll();
    }

}
