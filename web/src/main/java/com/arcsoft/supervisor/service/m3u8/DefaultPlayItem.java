package com.arcsoft.supervisor.service.m3u8;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author zw.
 */
public class DefaultPlayItem implements PlayItem {

    private long duration;
    private Path path;
    private long startTime;
    private long endTime;

    @Override
    public long getDuration() {
        return duration;
    }

    @Override
    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public Path getPath() {
        return path;
    }

    @Override
    public void setPath(String path) {
        this.path = Paths.get(path);
    }

    @Override
    public long getStartTime() {
        return startTime;
    }

    @Override
    public void setStartTime(long time) {
        this.startTime = time;
    }

    @Override
    public long getEndTime() {
        return endTime;
    }

    @Override
    public void setEndTime(long time) {
        this.endTime = time;
    }

}
