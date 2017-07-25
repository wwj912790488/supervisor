package com.arcsoft.supervisor.agent.service.task;

import com.arcsoft.supervisor.transcoder.ITranscodingTracker;

/**
 * A resource used for {@link ITranscodingTracker#setUserData(Object)} to holds
 * the resource during task lifecycle.
 *
 * @author zw.
 */
public interface TranscodingTrackerResource<T> {


    /**
     * Return the resource.
     */
    T getResource();

    /**
     * Sets resource using given <code>resource</code>
     *
     * @param resource the resource to be set
     */
    void setResource(T resource);

}
