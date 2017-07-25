package com.arcsoft.supervisor.agent.service.task.resource;

import com.arcsoft.supervisor.agent.service.task.TranscodingTrackerResource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A rtsp task tracker resource implementation.
 *
 * @author zw.
 */
public class RtspTranscodingTrackerResource implements TranscodingTrackerResource<List<String>> {

    private List<String> streamPath;

    public RtspTranscodingTrackerResource() {
    }

    public RtspTranscodingTrackerResource(String[] streamPaths){
        this(Arrays.asList(streamPaths));
    }

    public RtspTranscodingTrackerResource(List<String> streamPath) {
        this.streamPath = streamPath;
    }

    @Override
    public List<String> getResource() {
        return this.streamPath == null ? Collections.<String>emptyList() : this.streamPath;
    }

    @Override
    public void setResource(List<String> resource) {
        this.streamPath = resource;
    }
}
