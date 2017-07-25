package com.arcsoft.supervisor.repository.log;

import com.arcsoft.supervisor.model.domain.log.SystemLog;
import com.arcsoft.supervisor.service.log.impl.SystemLogQueryParams;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SystemLogRepositoryImpl implements JpaSystemLogRepository {
	
	@PersistenceContext
    private EntityManager em;
	
	@Override
	public void delete(SystemLogQueryParams params) {
		CriteriaBuilder criteriaBuilder= em.getCriteriaBuilder();
	    CriteriaDelete<SystemLog> criteriaDelete = criteriaBuilder.createCriteriaDelete(SystemLog.class);

	    Root<SystemLog> root = criteriaDelete.from(SystemLog.class);
	    List<Predicate> predicates = buildQueryPredicates(params,
				criteriaBuilder, root);
	    
		criteriaDelete.where(predicates.toArray(new Predicate[] {}));
		
		em.createQuery(criteriaDelete).executeUpdate();

	}

	@Override
	public List<SystemLog> find(SystemLogQueryParams params) {
		CriteriaBuilder criteriaBuilder= em.getCriteriaBuilder();
	    CriteriaQuery<SystemLog> criteriaQuery = criteriaBuilder.createQuery(SystemLog.class);

	    Root<SystemLog> root = criteriaQuery.from(SystemLog.class);
	    List<Predicate> predicates = buildQueryPredicates(params,
				criteriaBuilder, root);
	    
		criteriaQuery.where(predicates.toArray(new Predicate[] {}));
		
		TypedQuery<SystemLog> q = em.createQuery(criteriaQuery);
		return q.getResultList();
	}
	
	private List<Predicate> buildQueryPredicates(SystemLogQueryParams params,
			CriteriaBuilder criteriaBuilder, Root<SystemLog> root) {
		List<Predicate> predicates = new ArrayList<Predicate>();
	    if (params.getStartTime() != null && params.getEndTime() != null){
	    	predicates.add(criteriaBuilder.and(criteriaBuilder.greaterThan(root.<Date>get("realDateTime"), params.getStartTime()), 
	    			criteriaBuilder.lessThan(root.<Date>get("realDateTime"), params.getEndTime())));           
        }

        if (params.getFuncType() != null && params.getFuncType() != 0){
            predicates.add(criteriaBuilder.equal(root.<Integer>get("funcType"), params.getFuncType()));
        }

        if (StringUtils.isNotBlank(params.getOperationInfo())){
        	predicates.add(criteriaBuilder.like(root.<String>get("operationInfo"), "%" + params.getOperationInfo() +"%"));
        }
		return predicates;
	}

}
