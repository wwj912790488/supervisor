package com.arcsoft.supervisor.service.task.impl;

import com.arcsoft.supervisor.model.domain.server.Server;
import com.arcsoft.supervisor.model.domain.task.Task;
import com.arcsoft.supervisor.model.domain.task.TaskInputGpuUsage;
import com.arcsoft.supervisor.model.vo.task.TaskType;
import com.arcsoft.supervisor.repository.server.ServerJpaRepository;
import com.arcsoft.supervisor.repository.task.TaskInputGpuUsageRepository;
import com.arcsoft.supervisor.repository.task.TaskRepository;
import com.arcsoft.supervisor.service.TransactionSupport;
import com.arcsoft.supervisor.service.task.TaskInputGpuUsageService;
import com.arcsoft.supervisor.service.task.gpu.TaskGpuUsage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DefaultTaskInputGpuUsageService implements TaskInputGpuUsageService, TransactionSupport{
    private Logger logger = Logger.getLogger(DefaultTaskInputGpuUsageService.class);

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskInputGpuUsageRepository taskInputGpuUsageRepository;

    @Autowired
    private ServerJpaRepository serverJpaRepository;

    @Override
    public void updateTaskInputGpuUsage(Integer taskId, Map<String, Integer> pairs) {
        Task task = taskRepository.findOne(taskId);
        List<TaskInputGpuUsage> usages = task.getTaskInputGpuUsages();
        usages.clear();
        for(Map.Entry<String, Integer> pair : pairs.entrySet()) {
            TaskInputGpuUsage usage = new TaskInputGpuUsage();
            usage.setInput(Integer.parseInt(pair.getKey()));
            usage.setGpu(pair.getValue());
            usage.setTask(task);
            usages.add(usage);
        }
    }

    @Override
    public void removeTaskInputGpuUsage(int taskId) {
        Task task = taskRepository.findOne(taskId);
        List<TaskInputGpuUsage> usages = task.getTaskInputGpuUsages();
        usages.clear();
    }

    @Override
    public ConcurrentHashMap<String, ConcurrentHashMap<Integer, TaskGpuUsage>> getGpuUsageStatistic() {
        ConcurrentHashMap<String, ConcurrentHashMap<Integer, TaskGpuUsage>> statistic = new ConcurrentHashMap<>();
        try{
            List<Task> tasks = taskRepository.findRunningComposeTasksByTypes(TaskType.IP_STREAM_COMPOSE.getType(), TaskType.SDI_STREAM_COMPOSE.getType());
            for(Task task : tasks) {
                ConcurrentHashMap<Integer, TaskGpuUsage> serverUsage = statistic.get(task.getServerId());
                Server server = serverJpaRepository.getServer(task.getServerId());
                if(server==null)
                    continue;
                if(serverUsage == null) {
                    serverUsage = new ConcurrentHashMap<>();
                    statistic.put(task.getServerId(), serverUsage);
                }
                TaskGpuUsage taskUsage = new TaskGpuUsage(task.getId(), server.getGpus());
                setTaskGpuUsage(taskUsage, task.getTaskInputGpuUsages());
                serverUsage.put(task.getId(), taskUsage);
            }
        }catch (Exception e){
            logger.info(e.getMessage(),e);
        }

        return statistic;
    }

    private void setTaskGpuUsage(TaskGpuUsage taskUsage, List<TaskInputGpuUsage> taskInputGpuUsages) {
        Map<String, Integer> pairs = new HashMap<>();
        for(TaskInputGpuUsage usage: taskInputGpuUsages) {
            pairs.put(usage.getInput().toString(), usage.getGpu());
            taskUsage.getUsageByIndex(usage.getGpu() - 1).incrementAmountOfDecodedInputs(1);
        }
        taskUsage.setAssignedIndexPairOfInputAndGpu(pairs);

    }

}
