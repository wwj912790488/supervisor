package com.arcsoft.supervisor.service.task.processor;

import com.arcsoft.supervisor.cluster.action.BaseRequest;
import com.arcsoft.supervisor.cluster.action.BaseResponse;
import com.arcsoft.supervisor.cluster.action.task.StopRequest;
import com.arcsoft.supervisor.exception.service.BusinessException;
import com.arcsoft.supervisor.exception.service.BusinessExceptionDescription;
import com.arcsoft.supervisor.model.domain.server.Server;
import com.arcsoft.supervisor.model.domain.server.ServerFunction;
import com.arcsoft.supervisor.model.domain.server.ServerType;
import com.arcsoft.supervisor.model.domain.task.Task;
import com.arcsoft.supervisor.model.domain.task.TaskPort;
import com.arcsoft.supervisor.model.vo.task.AbstractRtspParams;
import com.arcsoft.supervisor.model.vo.task.TaskStatus;
import com.arcsoft.supervisor.model.vo.task.TaskType;
import com.arcsoft.supervisor.repository.server.ServerJpaRepository;
import com.arcsoft.supervisor.repository.task.TaskRepository;
import com.arcsoft.supervisor.service.remote.RemoteExecutorServiceSupport;
import com.arcsoft.supervisor.service.settings.RtspConfigurationService;
import com.arcsoft.supervisor.service.task.ServerLoadBalance;
import com.arcsoft.supervisor.service.task.TaskProcessor;
import com.arcsoft.supervisor.service.task.gpu.GpuLoadBalanceManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

/**
 * @author zw.
 */
public abstract class AbstractTaskProcessorSupport extends RemoteExecutorServiceSupport implements TaskProcessor {

    @Autowired
    protected ServerLoadBalance loadBalance;

    @Autowired
    protected TaskRepository taskRepository;

    @Autowired
    protected ServerJpaRepository serverRepository;

    @Autowired
    protected RtspConfigurationService rtspConfigurationService;

    @Autowired
    protected GpuLoadBalanceManager gpuLoadBalanceManager;


    @Override
    public void start(Task task) {
        Server server = StringUtils.isBlank(task.getServerId()) ? getServer(task)
                : serverRepository.getServer(task.getServerId());
        if (server == null || !server.isAlive()) {
            throw BusinessExceptionDescription.TASK_NO_AVAILABLE_SERVER.exception();
        }
        try {
            BaseResponse response = start(task, server);
            if (response.isSuccess() && StringUtils.isBlank(task.getServerId())) {
                task.setServerId(server.getId());
            }
        } catch (Exception e) {
            gpuLoadBalanceManager.releaseGpuItems(task.getId(), server.getId());
            throw e;
        }

    }

    protected abstract BaseResponse start(Task task, Server server);

    /**
     * Returns a server will be used to running the given {@code task}.
     *
     * @param task the task will be start
     * @return the server can running the given {@code task}.
     */
    protected Server getServer(Task task) {
        return loadBalance.getServer(ServerType.AGENT, getFunctionByTaskType(task.getTypeAsEnum()));
    }

    @Override
    public void stop(int taskId) {
        Task task = taskRepository.findOne(taskId);
        if (task != null && StringUtils.isNotEmpty(task.getServerId())) {
            stop(task.getId(), serverRepository.getServer(task.getServerId()));
        }
    }

    @Override
    public void stop(int taskId, Server server) {
        stop(taskRepository.findOne(taskId), server);
    }

    private void stop(Task task, Server server) {
        if (task != null
                && StringUtils.isNotEmpty(task.getStatus())
                && TaskStatus.RUNNING.name().equals(task.getStatus())
                && server != null) {
            StopRequest request = new StopRequest();
            request.setTaskIds(Collections.singletonList(task.getId()));
            request.setTaskType(task.getTypeAsEnum());
            try {
                execute(request, server);
            } catch (Exception e) {
                throw BusinessExceptionDescription.ERROR.withException(e);
            }
        }
    }

    protected BaseResponse execute(BaseRequest request, Server server) {
        BaseResponse response = remoteExecutorService.remoteExecute(request, server);
        if (response != null) {
            return response;
        } else {
            throw new NullPointerException();
        }
    }

    protected ServerLoadBalance getLoadBalance() {
        return loadBalance;
    }

    protected ServerFunction getFunctionByTaskType(TaskType taskType) {
        switch (taskType) {
            case IP_STREAM_COMPOSE:
                return ServerFunction.IP_STREAM_COMPOSE;
            case RTSP:
                return ServerFunction.ENCODER;
            default:
                return null;
        }
    }

    @Override
    public void reload(Task task) {
        throw new UnsupportedOperationException("The task don't support reload.");
    }

    /**
     * Returns TaskPort base on task with given port type.
     *
     * @param task the task which contains TaskPort items
     * @param type the type of TaskPort
     * @return the type of TaskPort in task
     * @throws BusinessException with below
     *                           <ul>
     *                           <li>{@link BusinessExceptionDescription#TASK_PORT_ASSIGN_FAILED}</li>
     *                           </ul>
     */
    protected TaskPort getTaskPortWithPortType(Task task, TaskPort.PortType type) {
        TaskPort taskPort = task.getTaskPortByType(type);
        if (taskPort == null) {
            throw BusinessExceptionDescription.TASK_PORT_ASSIGN_FAILED.exception();
        }
        return taskPort;
    }

    protected void setCommonRtspParams(AbstractRtspParams rtspParams) {
        rtspParams.setRtspHostIp(rtspConfigurationService.getIp());
        rtspParams.setRtspStoragePath(rtspConfigurationService.getStoragePath());
    }

}
