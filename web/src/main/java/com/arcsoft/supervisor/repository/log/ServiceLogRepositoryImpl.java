package com.arcsoft.supervisor.repository.log;

import com.arcsoft.supervisor.model.domain.log.ServiceLog;
import com.arcsoft.supervisor.service.log.impl.ServiceLogQueryParams;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ServiceLogRepositoryImpl implements JpaServiceLogRepository {

	@PersistenceContext
    private EntityManager em;
	
	@Override
	public void delete(ServiceLogQueryParams params) {
		CriteriaBuilder criteriaBuilder= em.getCriteriaBuilder();
	    CriteriaDelete<ServiceLog> criteriaDelete = criteriaBuilder.createCriteriaDelete(ServiceLog.class);

	    Root<ServiceLog> root = criteriaDelete.from(ServiceLog.class);
	    List<Predicate> predicates = buildQueryPredicates(params,
				criteriaBuilder, root);
	    
		criteriaDelete.where(predicates.toArray(new Predicate[] {}));
		
		em.createQuery(criteriaDelete).executeUpdate();
	}

	@Override
	public List<ServiceLog> find(ServiceLogQueryParams params) {
		CriteriaBuilder criteriaBuilder= em.getCriteriaBuilder();
	    CriteriaQuery<ServiceLog> criteriaQuery = criteriaBuilder.createQuery(ServiceLog.class);

	    Root<ServiceLog> root = criteriaQuery.from(ServiceLog.class);
	    List<Predicate> predicates = buildQueryPredicates(params,
				criteriaBuilder, root);
	    
		criteriaQuery.where(predicates.toArray(new Predicate[] {}));
		
		TypedQuery<ServiceLog> q = em.createQuery(criteriaQuery);
		return q.getResultList();
	}
	
	private List<Predicate> buildQueryPredicates(ServiceLogQueryParams params,
			CriteriaBuilder criteriaBuilder, Root<ServiceLog> root) {
		List<Predicate> predicates = new ArrayList<Predicate>();
	    if (params.getStartTime() != null && params.getEndTime() != null){
	    	predicates.add(criteriaBuilder.and(criteriaBuilder.greaterThan(root.<Date>get("time"), params.getStartTime()), 
	    			criteriaBuilder.lessThan(root.<Date>get("time"), params.getEndTime())));
            
        }

        if (params.getModule() != null && params.getModule() != -1){
            predicates.add(criteriaBuilder.equal(root.<Byte>get("module"), params.getModule()));
        }

        if (params.getLevel() != null && params.getLevel() != -1){
        	predicates.add(criteriaBuilder.equal(root.<Byte>get("level"), params.getLevel()));
        }

        if (StringUtils.isNotBlank(params.getDescription())){
        	predicates.add(criteriaBuilder.like(root.<String>get("description"), "%"+ params.getDescription() +"%"));
        }
		return predicates;
	}

}
