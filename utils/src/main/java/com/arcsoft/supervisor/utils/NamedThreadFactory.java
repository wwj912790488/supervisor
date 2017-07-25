package com.arcsoft.supervisor.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A named {@link ThreadFactory} implementation for assign a custom name for thread.
 * <p>The named strategy is {@code threadNamePrefix-thread-threadNumber}.The threadNumber
 * will increment one by one.
 *
 * @author zw.
 */
public class NamedThreadFactory implements ThreadFactory {

    private final AtomicInteger threadNumber;
    private final String threadNamePrefix;

    private NamedThreadFactory(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
        this.threadNumber = new AtomicInteger(1);
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, threadNamePrefix + "-thread-" + threadNumber.getAndIncrement());
    }


    /**
     * A factory method to create a new instance of {@link NamedThreadFactory}.
     *
     * @param threadNamePrefix the prefix of thread name
     * @return a new instance of {@link NamedThreadFactory}
     */
    public static NamedThreadFactory create(String threadNamePrefix) {
        return new NamedThreadFactory(threadNamePrefix);
    }
}
