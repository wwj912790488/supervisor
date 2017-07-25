package com.arcsoft.supervisor.service.task;

import com.arcsoft.supervisor.model.domain.task.TaskPort;
import com.arcsoft.supervisor.model.vo.task.TaskType;

import java.util.List;

/**
 * Assign available port for task to use.
 *
 * @author zw.
 */
public interface TaskPortAssigner {


    /**
     * Sets the {@link TaskPortTypeStrategy}.It will used for {@link #getTaskPorts(TaskType)}
     * to do port generate logic.
     *
     * @param portTypeStrategy the instance of {@link TaskPortTypeStrategy}
     */
    void setTaskPortTypeStrategy(TaskPortTypeStrategy portTypeStrategy);

    /**
     * Returns an available port.
     *
     * @return the port number
     */
    int getPort();

    /**
     * Returns items of {@link TaskPort} with given <code>taskType</code>
     *
     * @param taskType the type of task
     * @return Items of <code>TaskPort</code>
     */
    List<TaskPort> getTaskPorts(TaskType taskType);

}
