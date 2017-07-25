package com.arcsoft.supervisor.service.log;

import com.arcsoft.supervisor.model.domain.log.SystemLog;
import com.arcsoft.supervisor.service.log.impl.SystemLogQueryParams;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

/**
 * Interface for handling logic of <tt>systemLog</tt>.
 *
 * @author jt.
 */
public interface SystemLogService {

    public SystemLog findByid(Integer id);
    /**
     * Register a systemLog
     */
    public SystemLog add( String systemLog) ;

    
    public SystemLog add(String dateTime,String userName, Integer funcType,String operationInfo, String operationResult);
    /**
     * Removes a systemLog.
     *
     * @param systemLog the systemLog instance will be remove
     */
    public void delete(SystemLog systemLog);

    /**
     * Removes a systemLog with the specify <tt>id</tt> identify.
     *
     * @param id
     */
    public void deleteById(Integer id);
    
    public void deleteByIds(List<SystemLog> logs);
    
    public void delete(SystemLogQueryParams params);

    public Integer getCount();

    /**
     * Paginates with specific {@code params} and {@code pageRequest}.
     *
     * @param params the query parameters instance
     * @param pageRequest the page request
     * @return all of entities matching the given {@code params} nad {@code pageRequest}
     */
    public Page<SystemLog> paginate(SystemLogQueryParams params, PageRequest pageRequest);
    
    public List<SystemLog> findAll(SystemLogQueryParams params);
    
    public Long getCount(SystemLogQueryParams params);

}
