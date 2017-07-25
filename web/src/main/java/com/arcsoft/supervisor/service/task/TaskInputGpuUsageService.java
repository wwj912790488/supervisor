package com.arcsoft.supervisor.service.task;

import com.arcsoft.supervisor.service.task.gpu.TaskGpuUsage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface TaskInputGpuUsageService {
    public void updateTaskInputGpuUsage(Integer taskId, Map<String, Integer> paris);

    public void removeTaskInputGpuUsage(int taskId);

    public ConcurrentHashMap<String,ConcurrentHashMap<Integer,TaskGpuUsage>> getGpuUsageStatistic();
}
