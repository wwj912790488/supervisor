package com.arcsoft.supervisor.commons.spring;

/**
 * Template class that simplifies programmatic in session.
 *
 * @author zw.
 */
public interface SessionTemplate {

    public <T> T execute(SessionCallBack<T> action);

}
