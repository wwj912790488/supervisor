package com.arcsoft.supervisor.service.task.gpu;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * Object to represent usage of decoded inputs to correspond specified index of gpu.
 *
 * @author zw.
 */
public class GpuUsage {

    private final int index;
    private final AtomicInteger amountOfDecodedInputs;
    /**
     * A function convert to index items of gpu
     */
    static final Function<GpuUsage, Integer> GPU_USAGE_TO_INTEGER = new Function<GpuUsage, Integer>() {
        @Nullable
        @Override
        public Integer apply(GpuUsage input) {
            return input.getIndex();
        }
    };

    public GpuUsage(int index) {
        this.index = index;
        this.amountOfDecodedInputs = new AtomicInteger();
    }

    public int getIndex() {
        return index;
    }

    public void incrementAmountOfDecodedInputs(int amountOfInputs) {
        this.amountOfDecodedInputs.addAndGet(amountOfInputs);
    }

    public void decrementAmountOfDecodedInputs(int amountOfInputs) {
        for (;;) {
            int current = amountOfDecodedInputs.get();
            int next = current - amountOfInputs;
            if (amountOfDecodedInputs.compareAndSet(current, next)) {
                return;
            }
        }
    }

    public void resetAmountOfDecodedInputs(int amountOfInputs) {
        this.amountOfDecodedInputs.getAndSet(amountOfInputs);
    }

    public void resetAmountOfDecodedInputs() {
        resetAmountOfDecodedInputs(0);
    }

    public int getAmountOfDecodedInputs() {
        return amountOfDecodedInputs.get();
    }

    public GpuUsage copy() {
        GpuUsage gpuUsage = new GpuUsage(this.index);
        gpuUsage.incrementAmountOfDecodedInputs(getAmountOfDecodedInputs());
        return gpuUsage;
    }

    static List<GpuUsage> getDefaultGpuUsages(int server_gpus) {
        List<GpuUsage> gpuIndex = Lists.newArrayList();
        for (int index = 0 ; index < server_gpus ; index++)  {
            gpuIndex.add(new GpuUsage(index));
        }
        return ImmutableList.<GpuUsage>builder()
                .addAll(gpuIndex)
                .build();
    }

    @Override
    public String toString() {
        return "GpuUsage{" +
                "index=" + index +
                ", amountOfDecodedInputs=" + amountOfDecodedInputs +
                '}';
    }

    static List<Integer> getDefaultGpuIndex(int server_gpus) {
        List<Integer> gpuIndex = new ArrayList<>();
        for(int index = 0; index < server_gpus; index++) {
            gpuIndex.add(index);
        }
        return gpuIndex;
    }

    static List<Integer> getDefaultGpuIndexByStart(int server_gpus,int gpuStartIdx) {
        if(gpuStartIdx<0)
            return getDefaultGpuIndex(server_gpus);

        List<Integer> gpuIndex = new ArrayList<>();
        int actualIdx = gpuStartIdx%server_gpus;
        for(int index = actualIdx;index<server_gpus;index++){
            gpuIndex.add(index);
        }
        for(int index = 0; index < actualIdx; index++) {
            gpuIndex.add(index);
        }
        return gpuIndex;
    }
}
