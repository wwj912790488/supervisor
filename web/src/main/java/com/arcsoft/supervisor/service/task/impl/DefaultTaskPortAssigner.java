package com.arcsoft.supervisor.service.task.impl;

import com.arcsoft.supervisor.model.domain.task.TaskPort;
import com.arcsoft.supervisor.model.domain.task.TaskPort.PortType;
import com.arcsoft.supervisor.model.vo.task.TaskType;
import com.arcsoft.supervisor.repository.task.TaskPortRepository;
import com.arcsoft.supervisor.service.ServiceSupport;
import com.arcsoft.supervisor.service.task.TaskPortAssigner;
import com.arcsoft.supervisor.service.task.TaskPortTypeStrategy;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * An implementation of {@link TaskPortAssigner} base on database to assign port number.
 * <p><b>Notes: This implementation is thread safe.</b></p>
 *
 * @author zw.
 */
@Service
public class DefaultTaskPortAssigner extends ServiceSupport implements TaskPortAssigner {

    private final AtomicInteger portNumber;

    private int startPort = 10000;

    private TaskPortTypeStrategy portTypeStrategy;

    @Autowired
    public DefaultTaskPortAssigner(TaskPortRepository taskPortRepository) {
        Integer maxPortNumber = taskPortRepository.findMaxPortNumber();
        this.portNumber = new AtomicInteger(maxPortNumber == null ? startPort : maxPortNumber);
    }

    public void setStartPort(int startPort) {
        this.startPort = startPort;
    }

    @Autowired
    @Override
    public void setTaskPortTypeStrategy(
            @Qualifier("defaultTaskPortTypeStrategy") TaskPortTypeStrategy portTypeStrategy) {
        this.portTypeStrategy = portTypeStrategy;
    }

    @Override
    public int getPort() {
        return portNumber.addAndGet(1);
    }

    /**
     * {@inheritDoc}
     *
     * @param taskType the type of task
     * @return Items of TaskPort.The items is decision by taskType.Contains two item of TaskPort, one for
     * screen type and other is mobile type if taskType is compose task otherwise contains sd and hd type of
     * item for rtsp type.
     */
    @Override
    public List<TaskPort> getTaskPorts(TaskType taskType) {
        return Lists.transform(portTypeStrategy.create(taskType), new Function<PortType, TaskPort>() {
            @Nullable
            @Override
            public TaskPort apply(PortType type) {
                return new TaskPort(type, getPort());
            }
        });
    }
}
