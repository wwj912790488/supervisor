package com.arcsoft.supervisor.agent.service.task.resource;


import com.arcsoft.supervisor.agent.service.task.TranscodingTrackerResource;
import com.arcsoft.supervisor.transcoder.ITranscodingTracker;

import java.util.ArrayList;
import java.util.List;

/**
 * This class used to holds the {@link TranscodingTrackerResource} and pass in
 * {@link ITranscodingTracker#setUserData(Object)}.
 *
 * @author zw.
 */
public class TaskResourceHolder {

    /**
     * The resource items.
     */
    private List<TranscodingTrackerResource> resources = new ArrayList<>();

    public void addResource(TranscodingTrackerResource resource) {
        this.resources.add(resource);
    }

    public List<TranscodingTrackerResource> getResources() {
        return resources;
    }


    /**
     * Returns by given {@code type}.
     *
     * @param type the type of class
     * @return The first matched {@code TranscodingTrackerResource} of {@code type}
     */
    public TranscodingTrackerResource getByType(Class<?> type) {
        for (TranscodingTrackerResource resource : resources) {
            if (resource.getClass().isAssignableFrom(type)) {
                return resource;
            }
        }
        return null;
    }


    /**
     * Constructs a {@code TaskResourceHolder} use given {@code streamPaths}.
     *
     * @param streamPaths the path array of streams
     * @return the {@code TaskResourceHolder} contains given {@code streamPaths}
     */
    public static TaskResourceHolder constructRtspResource(String[] streamPaths) {
        TaskResourceHolder holder = new TaskResourceHolder();
        holder.addResource(new RtspTranscodingTrackerResource(streamPaths));
        return holder;
    }
}
