package com.arcsoft.supervisor.repository.task;

import com.arcsoft.supervisor.model.domain.task.TaskInputGpuUsage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskInputGpuUsageRepository extends JpaRepository<TaskInputGpuUsage, Integer> {
}
