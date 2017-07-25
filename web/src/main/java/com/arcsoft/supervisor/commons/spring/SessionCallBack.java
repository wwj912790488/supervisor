package com.arcsoft.supervisor.commons.spring;

/**
 *
 * Callback interface for execute code in session.Used with {@link SessionTemplate}'s
 * {@code execute} method, often as anonymous class within a method implementation.
 *
 * @author zw.
 */
public interface SessionCallBack<T> {

    public T doInSession();

}
