package com.arcsoft.supervisor.service.m3u8;

import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.List;

/**
 * Defines the functional of {@code m3u8} file to support parse,create and filter.
 *
 * @author zw.
 */
public interface M3u8 {

    /**
     * Parses from a multiple bitrate m3u8 file.
     *
     * @param m3u8Path the path of multiple bitrate m3u8 file path
     * @return {@code List<M3u8Info>}
     */
    public List<M3u8Info> parse(String m3u8Path);

    /**
     * Parses the given child {@code m3u8Path} to {@code List<PlayItem>}.
     *
     * @param m3u8Path the child m3u8 file path
     * @return the items of {@code PlayItem}
     */
    public List<PlayItem> parseFromChild(String m3u8Path);

    /**
     * Creates the child m3u8 file with given {@code playItems}.
     *
     * @param playItems the items
     * @return created m3u8 file path or {@code null} if failed
     */
    public String create(List<PlayItem> playItems);

    /**
     * Creates the child m3u8 file base on a {@code childM3u8Path} and given {@code startTime}
     * and {@code endTime}.
     *
     * @param childM3u8Path the child m3u8 file path
     * @param startTime     the start time in millis
     * @param endTime       the end time in millis
     * @return a pair object contains m3u8 path and matched items between {@code startTime} and {@code endTime}
     * or {@code null} if failed to create m3u8 file or can't find any items between time period
     */
    public ImmutablePair<String, List<PlayItem>> createFromChildM3u8ByTimePeriod(String childM3u8Path, long startTime, long endTime);

    /**
     * Returns items base on {@code playItems} with given {@code startTime} and {@code endTime}.
     *
     * @param playItems the items will be used to search
     * @param startTime the start time in millis
     * @param endTime   the end time in millis
     * @return matched items
     */
    public List<PlayItem> getPlayItemsByTimePeriod(List<PlayItem> playItems, long startTime, long endTime);

}
