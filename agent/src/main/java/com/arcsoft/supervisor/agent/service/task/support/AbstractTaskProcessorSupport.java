package com.arcsoft.supervisor.agent.service.task.support;

import com.arcsoft.supervisor.agent.service.task.TaskProcessor;

/**
 * Super support class of processor.
 *
 * @author zw.
 */
public abstract class AbstractTaskProcessorSupport implements TaskProcessor {

    @Override
    public byte[] getThumbnail(int taskId) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getTranscoderXml(int taskId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getProgressXml(int taskId) {
        throw new UnsupportedOperationException();
    }
}
