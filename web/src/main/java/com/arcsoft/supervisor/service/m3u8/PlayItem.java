package com.arcsoft.supervisor.service.m3u8;

import java.nio.file.Path;

/**
 * A {@code PlayItem} holds information of each ts file.
 *
 * @author zw.
 */
public interface PlayItem {

    /**
     * Returns the duration of media.
     *
     */
    public long getDuration();

    public void setDuration(long duration);

    /**
     * Returns the path of media.
     */
    public Path getPath();

    public void setPath(String path);

    /**
     * Returns the start time of file as millis.
     *
     * @return the millis.
     */
    public long getStartTime();

    public void setStartTime(long time);


    public long getEndTime();

    public void setEndTime(long time);


}
