package com.arcsoft.supervisor.service.log.impl;

import com.arcsoft.supervisor.model.domain.log.QServiceLog;
import com.arcsoft.supervisor.model.domain.log.ServiceLog;
import com.arcsoft.supervisor.repository.log.ServiceLogRepository;
import com.arcsoft.supervisor.service.TransactionSupport;
import com.arcsoft.supervisor.service.log.ServiceLogService;
import com.mysema.query.BooleanBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @author jt.
 * @author zw
 */
@Service
public class DefaultServiceLogServiceImpl implements ServiceLogService, TransactionSupport {

    @Autowired
    private ServiceLogRepository serviceLogRepository;

    @Override
    public void save(ServiceLog log) {
        serviceLogRepository.save(log);
    }

    @Override
    public void delete(ServiceLog serviceLog) {
        serviceLogRepository.delete(serviceLog);
    }

    @Override
    public ServiceLog findByid(Integer id) {
        return  serviceLogRepository.findOne(id);
    }

    @Override
    public void deleteById(Integer id) {
        try{
        	serviceLogRepository.delete(id);
        }catch (EmptyResultDataAccessException e){
            //Ignore this exception if the entity not exists.
        }
    }
    
    @Override
    public void deleteByIds(List<ServiceLog> serviceLogs) {
    	serviceLogRepository.deleteInBatch(serviceLogs);
    }
    
    @Override
    public void delete(ServiceLogQueryParams params) {
    	serviceLogRepository.delete(params);
    }

    @Override
    public Page<ServiceLog> paginate(ServiceLogQueryParams params, PageRequest request) {
        if (params == null){
            return serviceLogRepository.findAll(request);
        }

        BooleanBuilder queryBuilder = constructQueryBuilder(params);

        return serviceLogRepository.findAll(queryBuilder.getValue(), request);
    }

	private BooleanBuilder constructQueryBuilder(ServiceLogQueryParams params) {
		QServiceLog qServiceLog = QServiceLog.serviceLog;

        BooleanBuilder queryBuilder = new BooleanBuilder();

        if (params.getStartTime() != null && params.getEndTime() != null){
            queryBuilder.and(qServiceLog.time.between(params.getStartTime(), params.getEndTime()));
        }

        if (params.getModule() != null && params.getModule() != -1){
            queryBuilder.and(qServiceLog.module.eq(params.getModule()));
        }

        if (params.getLevel() != null && params.getLevel() != -1){
            queryBuilder.and(qServiceLog.level.eq(params.getLevel()));
        }

        if (StringUtils.isNotBlank(params.getDescription())){
            queryBuilder.and(qServiceLog.description.like("%"+ params.getDescription() +"%"));
        }
		return queryBuilder;
	}
    
    @Override
    public List<ServiceLog> findAll(ServiceLogQueryParams params) {
    	if (params == null){
            return serviceLogRepository.findAll();
        }

        return serviceLogRepository.find(params);
    }
    
    @Override
    public Long getCount(ServiceLogQueryParams params) {
    	if(params == null) {
    		return serviceLogRepository.count();
    	}
    	
    	BooleanBuilder queryBuilder = constructQueryBuilder(params);

        return serviceLogRepository.count(queryBuilder);
    }
}
