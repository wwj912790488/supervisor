package com.arcsoft.supervisor.service.task.gpu;

import com.arcsoft.supervisor.model.domain.server.Server;
import com.arcsoft.supervisor.model.domain.task.Task;
import com.arcsoft.supervisor.repository.task.TaskRepository;
import com.arcsoft.supervisor.service.server.ServerService;
import com.arcsoft.supervisor.service.task.TaskInputGpuUsageService;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Ordering;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation for {@link GpuLoadBalanceManager}.
 * <p>The class use an inner cache to holds all of gpu usage of servers.</p>
 *
 * @author zw.
 */
@Service
public class DefaultGpuLoadBalanceManager implements GpuLoadBalanceManager {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private ConcurrentHashMap<String, ConcurrentHashMap<Integer, TaskGpuUsage>> gpuUsageStatistic;

    private final TaskRepository taskRepository;

    @Autowired
    private TaskInputGpuUsageService taskInputGpuUsageService;

    @Autowired
    private ServerService serverService;

    /**
     * The value of index of first input item.
     */
    private static final String FIRST_INDEX_OF_INPUT = "0";

    @Autowired
    public DefaultGpuLoadBalanceManager(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
        this.gpuUsageStatistic = new ConcurrentHashMap<>();
    }

    @PostConstruct
    public void initGpuUsage() {
        gpuUsageStatistic = taskInputGpuUsageService.getGpuUsageStatistic();
        logger.debug("initialized gpu usage" + gpuUsageStatistic);
    }

    private static final Ordering<GpuUsage> amountOfDecodedInputsOrdering = new Ordering<GpuUsage>() {
        @Override
        public int compare(GpuUsage left, GpuUsage right) {
            return left.getAmountOfDecodedInputs() - right.getAmountOfDecodedInputs();
        }
    };

    private Task getTask(int taskId) {
        return taskRepository.findOne(taskId);
    }

    @Override
    public void releaseGpuItems(int taskId) {
        Task task = getTask(taskId);
        if (task != null && StringUtils.isNotBlank(task.getServerId())) {
            releaseGpuItems(taskId, task.getServerId());
        }
    }

    @Override
    public void releaseGpuItems(int taskId, String serverId) {
        ConcurrentHashMap<Integer, TaskGpuUsage> existedTaskGpuUsages = gpuUsageStatistic.get(serverId);
        if (existedTaskGpuUsages != null) {
            existedTaskGpuUsages.remove(taskId);
        }
        logger.debug("gpu usage after release gpu " + gpuUsageStatistic);
        taskInputGpuUsageService.removeTaskInputGpuUsage(taskId);
    }

    /**
     * {@inheritDoc}<br>
     *<b>Notes: This method is not absolute thread safe.You must keep one thread per task
     * id at the same time.</b>
     *
     * @param taskId the identify value of task
     * @param expectAmountOfGpu the amount of expected gpu
     * @param amountOfScreenPosition the amount of screen position
     * @return {@inheritDoc}
     */
    @Override
    public GpuDecodeAndEncodeConfig acquireGpuItems(int taskId, int expectAmountOfGpu, int amountOfScreenPosition) {
        Task task = taskRepository.findOne(taskId);
        return acquireGpuItems(taskId, expectAmountOfGpu, amountOfScreenPosition, task.getServerId(),task.getGpudIndex());
    }

    /**
     *
     * {@inheritDoc}<br>
     *<b>Notes: This method is not absolute thread safe.You must keep one thread per task
     * at the same time.</b>
     *
     * @param taskId the identify value of task
     * @param expectAmountOfGpu the amount os expected gpu
     * @param amountOfScreenPosition the amount of screen position
     * @param serverId the identify value of server
     * @return {@inheritDoc}
     */
    @Override
    public GpuDecodeAndEncodeConfig acquireGpuItems(int taskId, int expectAmountOfGpu, int amountOfScreenPosition, String serverId,int gpuStartIndex) {
        //Because the index of input and gpu can't change after assigned to a running task,
        //so we need retrieves assigned index pair of input and gpu from cache first.
        final Map<String, Integer> assignedIndexPairOfInputAndGpu = getAssignedIndexPairOfInputAndGpuFromCache(taskId, serverId);
        if (assignedIndexPairOfInputAndGpu != null) {
            taskInputGpuUsageService.updateTaskInputGpuUsage(taskId, assignedIndexPairOfInputAndGpu);
            return new GpuDecodeAndEncodeConfig(
                    assignedIndexPairOfInputAndGpu,
                    //Use the gpu index of first input index.
                    assignedIndexPairOfInputAndGpu.get(FIRST_INDEX_OF_INPUT) - 1
            );
        }
        Server server = serverService.getServer(serverId);
        int server_gpus = server.getGpus();
        final Map<String, Integer> indexPairOfInputAndGpu = assignGpuIndex(taskId, expectAmountOfGpu, amountOfScreenPosition, serverId, server_gpus,gpuStartIndex);

        TaskGpuUsage existedTaskGpuUsage = getTaskGpuUsage(serverId, taskId);
        if (existedTaskGpuUsage != null) {
            existedTaskGpuUsage.setAssignedIndexPairOfInputAndGpu(indexPairOfInputAndGpu);
        }
        taskInputGpuUsageService.updateTaskInputGpuUsage(taskId, indexPairOfInputAndGpu);
        logger.debug("gpu usage after acquire gpu " + gpuUsageStatistic);
        return new GpuDecodeAndEncodeConfig(
                indexPairOfInputAndGpu,
                //Use the gpu index of first input index.
                indexPairOfInputAndGpu.get(FIRST_INDEX_OF_INPUT) - 1
        );
    }

    @Override
    public int getGpuCount(String serverId)
    {
        int server_gpus = 0;
        try {
            Server server = serverService.getServer(serverId);
            server_gpus = server.getGpus();
        }catch (Exception e){
        }
        return server_gpus;
    }
    private Map<String, Integer> assignGpuIndex(int taskId, int expectAmountOfGpu, int amountOfScreenPosition, String serverId, int server_gpus,int gpuStartIndex) {
        List<Integer> loweredLoadGpuIndexItems = getLoweredLoadGpuIndex(expectAmountOfGpu, serverId, server_gpus,gpuStartIndex);
        Map<String, Integer> indexPairOfInputAndGpu = new HashMap<>();
        int loweredLoadItemIndex = 0;
        if(expectAmountOfGpu>loweredLoadGpuIndexItems.size())
            expectAmountOfGpu = loweredLoadGpuIndexItems.size();
        for (int idx = 0; idx < amountOfScreenPosition; idx++) {
            int index = loweredLoadGpuIndexItems.get(loweredLoadItemIndex);
            loweredLoadItemIndex = loweredLoadItemIndex < expectAmountOfGpu - 1 ? ++loweredLoadItemIndex : 0;
            addGpuUsage(taskId, index, 1, serverId, server_gpus);
            int actualGpuIndex = index + 1;//The decode index of gpu must start with 1.
            indexPairOfInputAndGpu.put(String.valueOf(idx), actualGpuIndex);
        }
        return indexPairOfInputAndGpu;
    }

    private Map<String, Integer> getAssignedIndexPairOfInputAndGpuFromCache(int taskId, String serverId) {
        TaskGpuUsage taskGpuUsage = getTaskGpuUsage(serverId, taskId);
        return taskGpuUsage == null ? null : taskGpuUsage.getAssignedIndexPairOfInputAndGpu();
    }

    private TaskGpuUsage getTaskGpuUsage(String serverId, int taskId) {
        final ConcurrentHashMap<Integer, TaskGpuUsage> existedTaskGpuUsages =
                getServerTaskGpuUsageStatisticFromUsageStatistic(serverId);
        return existedTaskGpuUsages.get(taskId);
    }

    private List<Integer> getLoweredLoadGpuIndex(int expectAmountOfGpu, String serverId, int server_gpus,int gpuStartIndex) {
        if(gpuStartIndex <0 || gpuStartIndex > 7){
            if (expectAmountOfGpu < server_gpus) {
                List<GpuUsage> sortedLowerUsedGpuUsages = calculateAndSortGpuUsagesOfServer(serverId, server_gpus);
                return FluentIterable.from(sortedLowerUsedGpuUsages.subList(0, expectAmountOfGpu))
                        .transform(GpuUsage.GPU_USAGE_TO_INTEGER)
                        .toList();
            } else {
                return GpuUsage.getDefaultGpuIndex(server_gpus);
            }
        }else{
            return GpuUsage.getDefaultGpuIndexByStart(server_gpus,gpuStartIndex);
        }
    }

    private void addGpuUsage(int taskId, int gpuIndex, int amountOfDecodedInputs, String serverId, int server_gpus) {
        final ConcurrentHashMap<Integer, TaskGpuUsage> existedTaskGpuUsages =
                getServerTaskGpuUsageStatisticFromUsageStatistic(serverId);
        TaskGpuUsage taskGpuUsage = existedTaskGpuUsages.get(taskId);
        if (taskGpuUsage == null) {
            final TaskGpuUsage newTaskGpuUsage = new TaskGpuUsage(taskId, server_gpus);
            TaskGpuUsage tempExistedTaskGpuUsage = existedTaskGpuUsages.putIfAbsent(taskId, newTaskGpuUsage);
            taskGpuUsage = tempExistedTaskGpuUsage == null ? newTaskGpuUsage : tempExistedTaskGpuUsage;
        }
        taskGpuUsage.getUsageByIndex(gpuIndex).incrementAmountOfDecodedInputs(amountOfDecodedInputs);
    }

    private List<GpuUsage> calculateAndSortGpuUsagesOfServer(String serverId, int server_gpus) {
        ConcurrentHashMap<Integer, TaskGpuUsage> taskGpuUsages = getServerTaskGpuUsageStatisticFromUsageStatistic(
                serverId
        );
        if (taskGpuUsages == null || taskGpuUsages.isEmpty()) {
            return GpuUsage.getDefaultGpuUsages(server_gpus);
        }
        Map<Integer, GpuUsage> allOfGpuUsages = new HashMap<>();
        for (TaskGpuUsage taskGpuUsage : taskGpuUsages.values()) {
            for (GpuUsage gpuUsage : taskGpuUsage.getGpuUsages()) {
                GpuUsage existedGpuUsage = allOfGpuUsages.get(gpuUsage.getIndex());
                if (existedGpuUsage != null) {
                    existedGpuUsage.incrementAmountOfDecodedInputs(gpuUsage.getAmountOfDecodedInputs());
                } else {
                    allOfGpuUsages.put(gpuUsage.getIndex(), gpuUsage.copy());
                }
            }
        }
        return amountOfDecodedInputsOrdering.sortedCopy(allOfGpuUsages.values());
    }

    private ConcurrentHashMap<Integer, TaskGpuUsage> getServerTaskGpuUsageStatisticFromUsageStatistic(String serverId) {
        ConcurrentHashMap<Integer, TaskGpuUsage> existedTaskGpuUsages = gpuUsageStatistic.get(serverId);
        if (existedTaskGpuUsages == null) {
            final ConcurrentHashMap<Integer, TaskGpuUsage> newTaskGpuUsages = new ConcurrentHashMap<>();
            ConcurrentHashMap<Integer, TaskGpuUsage> tempExistedTaskGpuUsages = gpuUsageStatistic.putIfAbsent(
                    serverId,
                    newTaskGpuUsages
            );
            existedTaskGpuUsages = tempExistedTaskGpuUsages == null ? newTaskGpuUsages : tempExistedTaskGpuUsages;
        }
        return existedTaskGpuUsages;
    }

}
