package com.arcsoft.supervisor.repository.system.impl;

import com.arcsoft.supervisor.model.domain.system.Sequence;
import com.arcsoft.supervisor.repository.system.SequenceRepository;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * @author zw.
 */
@Repository
public class SequenceRepositoryImpl implements SequenceRepository {


    @PersistenceContext
    private EntityManager em;

    @Override
    public long incrementAndGet(String key) {
        Query query = em.createNativeQuery("INSERT INTO sequence(`key`, `value`) " +
                "VALUES (:key, 1) " +
                "ON DUPLICATE KEY UPDATE value = value + 1");
        query.setParameter("key", key);
        int rows = query.executeUpdate();
        if (rows > 0) {
            Sequence sequence = em.find(Sequence.class, key, LockModeType.PESSIMISTIC_READ);
            return sequence.getValue();
        }
        return -1;
    }
}
