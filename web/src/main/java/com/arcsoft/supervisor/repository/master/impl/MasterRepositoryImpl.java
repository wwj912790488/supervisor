package com.arcsoft.supervisor.repository.master.impl;


import com.arcsoft.supervisor.model.domain.master.Master;
import com.arcsoft.supervisor.repository.master.MasterRepositoryFlag;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by wwj on 2016/6/30.
 */
@Repository
public  class MasterRepositoryImpl  implements MasterRepositoryFlag {

    @PersistenceContext
    private EntityManager em;

    @Override
    @SuppressWarnings("unchecked")
    public Master findByFlag() {
        Query query = em.createNativeQuery("select * from master where flag='1'", Master.class);
        List result = query.getResultList();
        if (result!=null){
          Master m=(Master) result.get(0);
            return  m;
        }

        return  null;
    }

    @Override
    public Master findById( int mid ) {
        Query query = em.createNativeQuery("select * from master where id="+mid, Master.class);
        List result = query.getResultList();
        if (result!=null){
            Master m=(Master) result.get(0);
            return  m;
        }
        return  null;
    }

    @Override
    @Transactional
    public void delete( int mid ) {
        Master m=em.find(Master.class,mid);
        em.remove(m);

    }
}
