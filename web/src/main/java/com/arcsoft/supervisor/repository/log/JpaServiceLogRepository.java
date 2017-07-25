package com.arcsoft.supervisor.repository.log;

import com.arcsoft.supervisor.model.domain.log.ServiceLog;
import com.arcsoft.supervisor.service.log.impl.ServiceLogQueryParams;

import java.util.List;

public interface JpaServiceLogRepository {
	public void delete(ServiceLogQueryParams params);
	public List<ServiceLog> find(ServiceLogQueryParams params);
}
