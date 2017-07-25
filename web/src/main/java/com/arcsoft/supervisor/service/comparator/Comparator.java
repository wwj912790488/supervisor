package com.arcsoft.supervisor.service.comparator;

/**
 *
 * A comparator interface to compare the <code>S</code> object is equal to <code>T</code> object.
 *
 * @author zw.
 */
public interface Comparator<S, T> {

    /**
     * Compares the <code>s</code> and <code>t</code>.
     *
     * @param s the object will be compare
     * @param t the target object of compare
     * @return <code>true</code> if the <code>s</code> is equal to <code>t</code>
     */
    public boolean compare(S s, T t);

}
