package com.arcsoft.supervisor.service.task.impl;

import com.arcsoft.supervisor.cluster.action.task.TaskProcessDetectRequest;
import com.arcsoft.supervisor.cluster.action.task.TaskProcessDetectResponse;
import com.arcsoft.supervisor.exception.StartTaskException;
import com.arcsoft.supervisor.exception.service.BusinessException;
import com.arcsoft.supervisor.exception.service.BusinessExceptionDescription;
import com.arcsoft.supervisor.model.domain.server.Server;
import com.arcsoft.supervisor.model.domain.task.Task;
import com.arcsoft.supervisor.model.vo.task.TaskStatus;
import com.arcsoft.supervisor.model.vo.task.TaskType;
import com.arcsoft.supervisor.repository.task.TaskRepository;
import com.arcsoft.supervisor.service.remote.RemoteExecutorServiceSupport;
import com.arcsoft.supervisor.service.server.ServerService;
import com.arcsoft.supervisor.service.task.TaskExecutor;
import com.arcsoft.supervisor.service.task.TaskProcessor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Map;
/**
 * @author zw.
 */
@Service
public class DefaultTaskExecutorImpl extends RemoteExecutorServiceSupport implements TaskExecutor {

    @Resource(name = "taskProcessorMap")
    private Map<TaskType, TaskProcessor> taskProcessorMap;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ServerService serverService;

    @Transactional
    @Override
    public void start(int taskId) throws StartTaskException {
        start(taskRepository.findOne(taskId));
    }

    @Transactional
    @Override
    public void stop(int taskId) {
        stop(taskRepository.findOne(taskId));
    }

    @Transactional
    public void start(Task task) throws StartTaskException {
        checkTaskNotNull(task);
    //    task.setStatus(TaskStatus.STARTING.toString());
        taskProcessorMap.get(task.getTypeAsEnum()).start(task);
    }

    /**
     * Checks the {@code task}.
     * @param task the task to be check
     * @throws BusinessException Thrown with {@link BusinessExceptionDescription#TASK_NOT_EXIST}
     */
    private void checkTaskNotNull(Task task){
        if (task == null){
            throw BusinessExceptionDescription.TASK_NOT_EXIST.exception();
        }
    }

    @Transactional
    public void stop(Task task) {
        checkTaskNotNull(task);
        if (!isTaskRunning(task.getId())) {
            if (task.isStatusEqual(TaskStatus.RUNNING)){
                task.setStatus(TaskStatus.STOP.toString());
            }
//            if (StringUtils.isNotBlank(task.getServerId())){
//                task.setServerId(null);
//            }
        } else {
            taskProcessorMap.get(task.getTypeAsEnum()).stop(task.getId());
        }
    }

    @Override
    public void stop(int taskId, Server server) {
        Task task = taskRepository.findOne(taskId);
        checkTaskNotNull(task);
        taskProcessorMap.get(task.getTypeAsEnum()).stop(task.getId(), server);
    }


    @Transactional
    @Override
    public void reload(Task task) {
        checkTaskNotNull(task);
        if (!task.isStatusEqual(TaskStatus.RUNNING)) {
            throw BusinessExceptionDescription.TASK_NOT_RUNNING.exception();
        }
        taskProcessorMap.get(task.getTypeAsEnum()).reload(task);
    }

    @Transactional
    @Override
    public void reload(int taskId) {
        reload(taskRepository.findOne(taskId));
    }

    @Override
    public boolean isTaskRunning(int taskId) {
        Task task = taskRepository.getOne(taskId);
        checkTaskNotNull(task);
        if (StringUtils.isBlank(task.getServerId())
                || (!task.isStatusEqual(TaskStatus.RUNNING) && !task.isStatusEqual(TaskStatus.DISCONNECTED))) {
            return false;
        }
        Server server = serverService.getServer(task.getServerId());
        if (server == null) {
            return false;
        }
        TaskProcessDetectRequest request = new TaskProcessDetectRequest();
        request.setTaskId(taskId);
        request.setTaskType(task.getTypeAsEnum());
        try {
            TaskProcessDetectResponse response = (TaskProcessDetectResponse) remoteExecutorService.remoteExecute(request, server);
            return response.isSuccess() && response.isProcessExists();
        } catch (Exception e) {

        }
        return false;
    }

}
