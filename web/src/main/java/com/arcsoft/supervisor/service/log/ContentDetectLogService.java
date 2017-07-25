package com.arcsoft.supervisor.service.log;

import com.arcsoft.supervisor.exception.service.BusinessExceptionDescription;
import com.arcsoft.supervisor.model.domain.log.ContentDetectLog;
import com.arcsoft.supervisor.model.vo.task.cd.ContentDetectResult;
import com.arcsoft.supervisor.service.log.impl.ChannelsContentDetectQueryParams;
import com.arcsoft.supervisor.service.log.impl.ContentDetectQueryParams;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

/**
 * @author zw.
 */
public interface ContentDetectLogService {

    public void save(ContentDetectResult result);

    void deleteByChannelId(Integer id);
 
    public void deleteByIds(List<ContentDetectLog> contentDetectLogs); 
    
    public long getCount();

    public ContentDetectLog findLatestOne();
    
    public ContentDetectLog getById(long id);

    public void updateVideoFilePathAndOffset(long id, long offset, String path);

    public void cleanVideoFilePathDeleted(String path);

    public BusinessExceptionDescription updateConfirmDate(long id);

    /**
     * Paginates with specific {@code params} and {@code pageRequest}.
     *
     * @param params the query parameters instance
     * @param pageRequest the page request
     * @return all of entities matching the given {@code params} nad {@code pageRequest}
     */
    public Page<ContentDetectLog> paginate(ContentDetectQueryParams params, PageRequest pageRequest);
    
    public List<ContentDetectLog> findAll(ContentDetectQueryParams params);

    public List<ContentDetectLog> findAll(ChannelsContentDetectQueryParams params);

    public List<ContentDetectLog> findByAllType(ChannelsContentDetectQueryParams params);

    public List<ContentDetectLog> getByChannelName(String channelName); 
    
    public void delete(ContentDetectQueryParams params);
    
    public Long getCount(ContentDetectQueryParams params);
    
 //   public int getCountByChannelName(String channelName);   

}
