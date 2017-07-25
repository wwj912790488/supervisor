package com.arcsoft.supervisor.repository.log;

import com.arcsoft.supervisor.model.domain.log.ContentDetectLog;
import com.arcsoft.supervisor.service.log.impl.ChannelsContentDetectQueryParams;
import com.arcsoft.supervisor.service.log.impl.ContentDetectQueryParams;

import java.util.List;


/**
 * The extend repository of jpa implementation for content detect.
 *
 * @author zw.
 */
public interface JpaContentDetectLogRepository {

    public List<ContentDetectLog> findNeedGenerateM3u8LogsWithPeriod();
	public void delete(ContentDetectQueryParams params);
	public List<ContentDetectLog> find(ContentDetectQueryParams params);
	public List<ContentDetectLog> find(ChannelsContentDetectQueryParams params);
	public List<ContentDetectLog> findByAllType(ChannelsContentDetectQueryParams params);

}
