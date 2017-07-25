package com.arcsoft.supervisor.service.task;

import com.arcsoft.supervisor.model.domain.task.TaskPort.PortType;
import com.arcsoft.supervisor.model.vo.task.TaskType;

import java.util.List;

/**
 * A strategy interface for create {@link PortType} by {@link TaskType} which
 * used for {@link TaskPortAssigner}.
 *
 * @author zw.
 */
public interface TaskPortTypeStrategy {

    /**
     * Creates the items of <tt>PortType</tt> with given <tt>taskType</tt>.
     *
     * @param taskType the taskType of task
     * @return the created items of <tt>PortType</tt>.
     */
    List<PortType> create(TaskType taskType);

}
