package com.arcsoft.supervisor.service.task.gpu;

import java.util.List;
import java.util.Map;

/**
 * {@code TaskGpuUsage} holds usage rate of each gpu to correspond server.
 *
 * @author zw.
 */
public class TaskGpuUsage {

    private final int taskId;
    private final List<GpuUsage> gpuUsages;
    private Map<String, Integer> assignedIndexPairOfInputAndGpu;

    @Override
    public String toString() {
        return "TaskGpuUsage{" +
                "taskId=" + taskId +
                ", gpuUsages=" + gpuUsages +
                ", assignedIndexPairOfInputAndGpu=" + assignedIndexPairOfInputAndGpu +
                '}';
    }

    public TaskGpuUsage(int taskId, int server_gpus) {
        this.taskId = taskId;
        this.gpuUsages = GpuUsage.getDefaultGpuUsages(server_gpus);
    }

    public int getTaskId() {
        return taskId;
    }

    public GpuUsage getUsageByIndex(int index) {
        return this.gpuUsages.get(index);
    }

    public List<GpuUsage> getGpuUsages() {
        return gpuUsages;
    }

    public Map<String, Integer> getAssignedIndexPairOfInputAndGpu() {
        return assignedIndexPairOfInputAndGpu;
    }

    public void setAssignedIndexPairOfInputAndGpu(Map<String, Integer> assignedIndexPairOfInputAndGpu) {
        this.assignedIndexPairOfInputAndGpu = assignedIndexPairOfInputAndGpu;
    }
}
