package com.arcsoft.supervisor.exception.service;

import org.apache.commons.lang3.StringUtils;

/**
 * This class providers a skeletal implementation of the {@link Description}
 * interface to minimize the effort required to implement this interface.
 *
 * @author zw.
 */
public abstract class AbstractDescription implements Description {

    /**
     * A integer value code.
     */
    private final int code;

    /**
     * The key of translator of i18n.
     */
    private final String translatorKey;

    protected AbstractDescription(int code) {
        this(code, null);
    }

    protected AbstractDescription(int code, String translatorKey) {
        this.code = code;
        this.translatorKey = translatorKey;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getTranslatorKey() {
        return translatorKey;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(getClass().getSimpleName() + "{");
        sb.append("code=").append(code);
        if (StringUtils.isNotBlank(translatorKey)){
            sb.append(", translatorKey='").append(translatorKey).append('\'');
        }
        sb.append('}');
        return sb.toString();
    }

}
