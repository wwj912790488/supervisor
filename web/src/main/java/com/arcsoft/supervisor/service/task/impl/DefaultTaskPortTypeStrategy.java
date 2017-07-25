package com.arcsoft.supervisor.service.task.impl;

import com.arcsoft.supervisor.model.domain.task.TaskPort.PortType;
import com.arcsoft.supervisor.model.vo.task.TaskType;
import com.arcsoft.supervisor.service.task.TaskPortTypeStrategy;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 *
 * Default implementation of {@link TaskPortTypeStrategy}.The implementation will create <tt>SCREEN, MOBILE<tt/>
 * types for compose task, create <tt>SD, HD</tt> types for channel task.
 *
 * @author zw.
 */
@Service("defaultTaskPortTypeStrategy")
public class DefaultTaskPortTypeStrategy implements TaskPortTypeStrategy {

    @Override
    public List<PortType> create(TaskType taskType) {
        return taskType.isComposeType() ? Arrays.asList(PortType.SCREEN, PortType.MOBILE)
                : Arrays.asList(PortType.SD, PortType.HD);
    }
}
