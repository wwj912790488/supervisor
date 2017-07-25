package com.arcsoft.supervisor.repository.task;

import com.arcsoft.supervisor.model.domain.task.TaskPort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author zw.
 */
public interface TaskPortRepository extends JpaRepository<TaskPort, Integer> {

    @Query("select max(p.portNumber) from TaskPort p")
    Integer findMaxPortNumber();

}
