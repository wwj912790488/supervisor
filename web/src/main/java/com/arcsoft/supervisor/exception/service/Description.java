package com.arcsoft.supervisor.exception.service;

/**
 * A {@code Description} for {@link BusinessException} to holds some
 * info of exception.
 *
 * @author zw.
 */
public interface Description {

    /**
     * Returns the code.
     *
     * @return the code
     */
    public int getCode();

    /**
     * Returns the translator key.
     *
     * @return the translator key or {@code null} if there is no key for translator
     */
    public String getTranslatorKey();

    /**
     * Returns the string representation of the object.
     *
     * @return the string representation of the object
     */
    public String toString();

}
