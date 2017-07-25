package com.arcsoft.supervisor.task;

import com.arcsoft.supervisor.ProductionTestSupport;
import com.arcsoft.supervisor.service.task.gpu.GpuLoadBalanceManager;
import com.arcsoft.supervisor.service.task.gpu.GpuLoadBalanceManager.GpuDecodeAndEncodeConfig;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author zw.
 */
public class GpuLoadBalanceManagerTests extends ProductionTestSupport{

    @Autowired
    private GpuLoadBalanceManager gpuLoadBalanceManager;

    @Test
    public void testAcquireGpuItems() {
        GpuDecodeAndEncodeConfig gpuDecodeAndEncodeConfig = gpuLoadBalanceManager.acquireGpuItems(1, 4, 36, "test-server",-1);
        GpuDecodeAndEncodeConfig gpuDecodeAndEncodeConfig2 = gpuLoadBalanceManager.acquireGpuItems(1, 3, 36, "test-server",-1);
        Assert.assertEquals(
                gpuDecodeAndEncodeConfig.getAssignedIndexPairOfInputAndDecodeGpu(),
                gpuDecodeAndEncodeConfig2.getAssignedIndexPairOfInputAndDecodeGpu()
        );
    }


}
