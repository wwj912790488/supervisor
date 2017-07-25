package com.arcsoft.supervisor.service.log.impl;

import com.arcsoft.supervisor.model.domain.log.QSystemLog;
import com.arcsoft.supervisor.model.domain.log.SystemLog;
import com.arcsoft.supervisor.repository.log.SystemLogRepository;
import com.arcsoft.supervisor.service.TransactionSupport;
import com.arcsoft.supervisor.service.log.SystemLogService;
import com.mysema.query.BooleanBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * A {@link com.arcsoft.supervisor.service.log.SystemLogService} implementation.
 *
 * @author jt.
 */
@Service
public class DefaultSystemLogServiceImpl implements SystemLogService, TransactionSupport {

    @Autowired
    private SystemLogRepository systemLogRepository;


    @Override
    public SystemLog findByid(Integer id) {
        return systemLogRepository.findOne(id);
    }

    @Override
    public SystemLog add(String log) {
        SystemLog newlog = new SystemLog(log);
        systemLogRepository.save(newlog);
        return newlog;
    }
    
    @Override
    public SystemLog add(String dateTime,String userName, Integer funcType, String operationInfo, String operationResult) {
        SystemLog newlog = new SystemLog(dateTime,userName,funcType,operationInfo,operationResult);
        systemLogRepository.save(newlog);
        return newlog;
    }


    @Override
    public void delete(SystemLog systemLog) {
        systemLogRepository.delete(systemLog);
    }

    @Override
    public void deleteById(Integer id) {
        try{
        	systemLogRepository.delete(id);
        }catch (EmptyResultDataAccessException e){
            //Ignore this exception if the entity not exists.
        }

    }
    
    @Override
    public void deleteByIds(List<SystemLog> systemLogs) {
    	systemLogRepository.deleteInBatch(systemLogs);
    }

    
    public Integer getCount() {
    	return (int)systemLogRepository.count();
    }

    @Override
    public Page<SystemLog> paginate(SystemLogQueryParams params, PageRequest pageRequest) {
        if (params == null){
            return systemLogRepository.findAll(pageRequest);
        }
        BooleanBuilder builder = constructQueryBuilder(params);

        return systemLogRepository.findAll(builder, pageRequest);
    }
    
    @Override
    public List<SystemLog> findAll(SystemLogQueryParams params) {
    	if(params == null) {
    		return systemLogRepository.findAll();
    	}

        return systemLogRepository.find(params);
    }
    
    @Override
    public Long getCount(SystemLogQueryParams params) {
    	if(params == null) {
    		return systemLogRepository.count();
    	}
    	BooleanBuilder builder = constructQueryBuilder(params);
    	
    	return systemLogRepository.count(builder);
    }

	private BooleanBuilder constructQueryBuilder(SystemLogQueryParams params) {
		QSystemLog qSystemLog = QSystemLog.systemLog;
        BooleanBuilder builder = new BooleanBuilder();
        if (params.getStartTime() != null && params.getEndTime() != null){
            builder.and(qSystemLog.realDateTime.between(params.getStartTime(), params.getEndTime()));
        }

        if (StringUtils.isNotBlank(params.getOperationInfo())){
            builder.and(qSystemLog.operationInfo.like("%" + params.getOperationInfo() +"%"));
        }

        if (params.getFuncType() != null && params.getFuncType() != 0){
            builder.and(qSystemLog.funcType.eq(params.getFuncType()));
        }
		return builder;
	}

	@Override
	public void delete(SystemLogQueryParams params) {
		systemLogRepository.delete(params);
	}

}
