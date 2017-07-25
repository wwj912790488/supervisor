package com.arcsoft.supervisor.service.task.gpu;

import java.util.Map;

/**
 * Interface to manage gpu.
 *
 * @author zw.
 */
public interface GpuLoadBalanceManager {

    /**
     * The config of index of decode and encode gpu.
     */
    class GpuDecodeAndEncodeConfig {
        /**
         * Acquired index of input and decode gpu.The key is the index of input and the value is the
         * index of decode gpu.<p>The index of input should be start with 0 and the index of decode
         * gpu should be start with 1.</p>
         */
        private final Map<String, Integer> assignedIndexPairOfInputAndDecodeGpu;

        /**
         * The index of encode gpu.The value should be start with 0.
         */
        private final int indexOfEncodeGpu;

        public GpuDecodeAndEncodeConfig(Map<String, Integer> assignedIndexPairOfInputAndDecodeGpu, int indexOfEncodeGpu) {
            this.assignedIndexPairOfInputAndDecodeGpu = assignedIndexPairOfInputAndDecodeGpu;
            this.indexOfEncodeGpu = indexOfEncodeGpu;
        }

        public Map<String, Integer> getAssignedIndexPairOfInputAndDecodeGpu() {
            return assignedIndexPairOfInputAndDecodeGpu;
        }

        public int getIndexOfEncodeGpu() {
            return indexOfEncodeGpu;
        }
    }

    /**
     * Acquire gpu index for task on specified server base on <code>expectAmountOfGpu</code>
     * and <code>amountOfScreenPosition</code>.
     *
     * @param taskId the identify value of task
     * @param expectAmountOfGpu the amount of expected gpu
     * @param amountOfScreenPosition the amount of screen position
     * @return {@link GpuDecodeAndEncodeConfig}
     */
    GpuDecodeAndEncodeConfig acquireGpuItems(int taskId, int expectAmountOfGpu, int amountOfScreenPosition);

    /**
     * Acquire gpu index for task on specified server base on <code>expectAmountOfGpu</code>
     * and <code>amountOfScreenPosition</code>.
     *
     * @param taskId the identify value of task
     * @param expectAmountOfGpu the amount of expected gpu
     * @param amountOfScreenPosition the amount of screen position
     * @param serverId the identify value of server
     * @return {@link GpuDecodeAndEncodeConfig}
     */
    GpuDecodeAndEncodeConfig acquireGpuItems(int taskId, int expectAmountOfGpu, int amountOfScreenPosition, String serverId,int gpuStartIndex);

    /**
     *
     * Release acquired gpu index of given task on specified server.
     *
     * @param taskId the identify value of task
     */
    void releaseGpuItems(int taskId);

    /**
     * Release acquired gpu index of given task on specified server.
     *
     * @param taskId the identify value of task
     * @param serverId the identify value of server
     */
    void releaseGpuItems(int taskId, String serverId);

    int getGpuCount(String serverId);

}
