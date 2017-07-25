package com.arcsoft.supervisor.service.converter;

/**
 * Defines contracts to doForward {@code S} to {@code T}
 *
 * @author zw.
 */
public interface Converter<S, T> {

    /**
     * Converts the source to {@code T}.
     *
     * @param source object to be converted
     * @return {@code T}
     * @throws Exception if failed to do convert
     */
    T doForward(S source) throws Exception;

    /**
     * Converts the given source to {@code S}.
     *
     * @param source object to be converted
     * @return {@code S}
     * @throws Exception if failed to do convert
     */
    S doBack(T source) throws Exception;



}
