package com.arcsoft.supervisor.service.m3u8;

import org.apache.commons.lang3.tuple.ImmutablePair;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * A default implementation of {@code M3u8Info}.
 *
 * @author zw.
 */
public class DefaultM3u8Info implements M3u8Info {

    private int programId;
    private int bandWidth;
    private ImmutablePair<Integer, Integer> resolution;
    private String codecs;
    private List<PlayItem> playItems;
    private Path path;
    private long startTime;


    @Override
    public int getProgramId() {
        return programId;
    }

    @Override
    public void setProgramId(int programId) {
        this.programId = programId;
    }

    @Override
    public int getBandWidth() {
        return bandWidth;
    }

    @Override
    public void setBandWidth(int bandWidth) {
        this.bandWidth = bandWidth;
    }

    @Override
    public ImmutablePair<Integer, Integer> getResolution() {
        return resolution;
    }

    @Override
    public void setResolution(int width, int height) {
        this.resolution = ImmutablePair.of(width, height);
    }

    @Override
    public String getCodecs() {
        return codecs;
    }

    @Override
    public void setCodecs(String codecs) {
        this.codecs = codecs;
    }

    @Override
    public List<PlayItem> getPlayItems() {
        return playItems;
    }

    @Override
    public void setPlayItems(List<PlayItem> itmes) {
        this.playItems = itmes;
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
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
