package com.arcsoft.supervisor.repository.settings.impl;

import com.arcsoft.supervisor.model.domain.settings.Storage;
import com.arcsoft.supervisor.repository.settings.StoragePersistenceRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;


/**
 * A implements to support <tt>CRUD</tt> storage with database.
 *
 * @author hxiang
 * @author zw
 */
public class StoragePersistenceInDbRepositoryImpl implements StoragePersistenceRepository {

    @PersistenceContext
    private EntityManager em;

	@Override
	public Storage get(Integer id) {
		return em.find(Storage.class, id);
	}

	@Override
	public Integer save(Storage storage) {
		em.persist(storage);
		return storage.getId();
	}

	@Override
	public boolean update(Storage storage) {
		em.merge(storage);
		return true;
	}

	@Override
	public void delete(Integer id) {
        Storage storage = get(id);
        if (storage != null){
            em.remove(storage);
        }
	}

	@Override
    @SuppressWarnings("unchecked")
	public List<Storage> get() {
		return em.createQuery("select s from Storage as s").getResultList();
	}
}
