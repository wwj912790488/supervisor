package com.arcsoft.supervisor.service.m3u8;

import java.util.List;

/**
 * A m3u8 parser to parse the input to specified type of {@code T}.
 *
 * @param <T> the type of result
 * @author zw
 */
public interface M3u8ElementParser<T> {

    /**
     * Parses the given {@code lines} to {@code T}.
     *
     * @param lines      the line text of m3u8 file
     * @param baseFolder the   base folder
     * @return the parsed result
     */
    public T parse(List<String> lines, String baseFolder);

}
