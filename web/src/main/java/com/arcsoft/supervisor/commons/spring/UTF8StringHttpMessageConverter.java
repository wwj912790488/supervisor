package com.arcsoft.supervisor.commons.spring;

import org.springframework.http.converter.StringHttpMessageConverter;

import java.nio.charset.Charset;


/**
 * @author zw.
 */
public class UTF8StringHttpMessageConverter extends StringHttpMessageConverter {

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    public UTF8StringHttpMessageConverter() {
        super(DEFAULT_CHARSET);
    }
}
