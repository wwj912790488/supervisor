package com.arcsoft.supervisor.service.log;

import com.arcsoft.supervisor.model.domain.log.ServiceLog;
import com.arcsoft.supervisor.service.log.impl.ServiceLogQueryParams;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

/**
 * Interface for handling logic of <tt>serviceLog</tt>.
 *
 * @author jt.
 */
public interface ServiceLogService {

    public ServiceLog findByid(Integer id);
    /**
     * Saves the log.
     *
     * @param log the log to be save
     */
    public void save(ServiceLog log);

    /**
     * Removes a serviceLog.
     *
     * @param serviceLog the serviceLog instance will be remove
     */
    public void delete(ServiceLog serviceLog);

    /**
     * Removes a serviceLog with the specify <tt>id</tt> identify.
     *
     * @param id
     */
    public void deleteById(Integer id);

    public void deleteByIds(List<ServiceLog> logs);

    public void delete(ServiceLogQueryParams params);

    /**
     * Paginates with specific {@code params} and {@code request}.
     *
     * @param params the query parameters instance
     * @param request the page request
     * @return all of entities matching the given {@code params} nad {@code request}
     */
    public Page<ServiceLog> paginate(ServiceLogQueryParams params, PageRequest request);
    
    public List<ServiceLog> findAll(ServiceLogQueryParams params);
    
    public Long getCount(ServiceLogQueryParams params);

}
