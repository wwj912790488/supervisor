package com.arcsoft.supervisor.service.profile;

import com.arcsoft.supervisor.model.domain.task.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

/**
 * Interface for generic methods of Profile.
 *
 * @param <T> the entity class
 * @param <D> the dto of entity
 */
public interface ProfileService<T extends Profile, D> {

    T save(D dto);

    void delete(int id);

    void deleteAll(List<Integer> ids);

    D find(int id);

    Page<T> paginate(PageRequest pageRequest);

    List<D> findAll();

    List<T> findAllProfile();
}
