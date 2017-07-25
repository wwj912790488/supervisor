package com.arcsoft.supervisor.service.m3u8;

import org.apache.commons.lang3.tuple.ImmutablePair;

import java.nio.file.Path;
import java.util.List;

/**
 * A {@code M3u8Info} is a {@code m3u8} file which may be contains multiple bitrates.
 *
 * @author zw.
 */
public interface M3u8Info {

    /**
     * Returns the id of which program used.
     *
     * @return the id of program
     */
    public int getProgramId();

    /**
     * Sets program id.
     *
     * @param programId which program id used
     */
    public void setProgramId(int programId);

    /**
     * Returns the bandwidth.
     *
     * @return the value of bandwidth
     */
    public int getBandWidth();

    /**
     * Sets bandwidth.
     *
     * @param bandWidth
     */
    public void setBandWidth(int bandWidth);

    /**
     * Returns the resolution.
     *
     * @return the pair object contains resolution.The left value is width and right value is height
     */
    public ImmutablePair<Integer, Integer> getResolution();

    public void setResolution(int width, int height);

    /**
     * Returns the used codecs.
     *
     * @return the codecs string representing
     */
    public String getCodecs();

    public void setCodecs(String codecs);

    /**
     * Returns all of {@code PlayItem}.
     *
     * @return {@code PlayItem} as list.
     */
    public List<PlayItem> getPlayItems();

    public void setPlayItems(List<PlayItem> itmes);

    /**
     * Returns the path.
     *
     * @return the path of stream
     */
    public Path getPath();

    public void setPath(String path);

    /**
     * Returns the start time.
     *
     * @return the start time
     */
    public long getStartTime();

    public void setStartTime(long startTime);

}
