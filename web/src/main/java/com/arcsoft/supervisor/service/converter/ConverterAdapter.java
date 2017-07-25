package com.arcsoft.supervisor.service.converter;

/**
 * Adaptor class for {@link Converter}.
 *
 * @author zw.
 */
public abstract class ConverterAdapter<S, T> implements Converter<S, T> {

    @Override
    public T doForward(S source) throws Exception{
        return null;
    }

    @Override
    public S doBack(T source) throws Exception{
        return null;
    }

}
