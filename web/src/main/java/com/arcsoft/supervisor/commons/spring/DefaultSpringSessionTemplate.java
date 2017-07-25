package com.arcsoft.supervisor.commons.spring;

import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.jpa.EntityManagerFactoryAccessor;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

/**
 * Default implementation for execute {@link SessionCallBack} in session.
 *
 * @author zw.
 */
public class DefaultSpringSessionTemplate extends EntityManagerFactoryAccessor implements SessionTemplate {


    @Override
    public <T> T execute(SessionCallBack<T> action) {
        boolean participate = false;
        if (TransactionSynchronizationManager.hasResource(getEntityManagerFactory())) {
            // Do not modify the EntityManager: just set the participate flag.
            participate = true;

        } else {
            try {
                EntityManager em = createEntityManager();
                EntityManagerHolder emHolder = new EntityManagerHolder(em);
                TransactionSynchronizationManager.bindResource(getEntityManagerFactory(), emHolder);
            } catch (PersistenceException ex) {
                throw new DataAccessResourceFailureException("Could not create JPA EntityManager", ex);
            }
        }

        try {

            return action.doInSession();

        } finally {
            if (!participate){
                EntityManagerHolder emHolder = (EntityManagerHolder)
                        TransactionSynchronizationManager.unbindResource(getEntityManagerFactory());
                EntityManagerFactoryUtils.closeEntityManager(emHolder.getEntityManager());
            }
        }
    }

}
