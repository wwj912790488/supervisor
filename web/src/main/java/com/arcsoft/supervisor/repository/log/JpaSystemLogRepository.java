package com.arcsoft.supervisor.repository.log;

import com.arcsoft.supervisor.model.domain.log.SystemLog;
import com.arcsoft.supervisor.service.log.impl.SystemLogQueryParams;

import java.util.List;

public interface JpaSystemLogRepository {
	public void delete(SystemLogQueryParams params);
	public List<SystemLog> find(SystemLogQueryParams params);
}
