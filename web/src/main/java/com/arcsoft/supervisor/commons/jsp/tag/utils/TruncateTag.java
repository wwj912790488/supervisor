package com.arcsoft.supervisor.commons.jsp.tag.utils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

/**
 * A truncate tag implementation to truncate the given string with rules.
 *
 * @author zw.
 */
public class TruncateTag extends SimpleTagSupport {

    /**
     * The string to be truncated.
     */
    private String value;

    /**
     * The max length of value.
     */
    private int maxLength;

    /**
     * The index of start.
     */
    private int start = 0;

    /**
     * The index of end.
     */
    private int end = 0;
    /**
     * The string to be append to.
     */
    private String append;


    @Override
    public void doTag() throws JspException, IOException {
        int length = value.length();
        if (append != null && append.length() >= maxLength){
            throw new IllegalArgumentException("The length of append string is too long.");
        }
        if (append != null && start == 0 && end == 0){
            end = maxLength - append.length();
        }
        end = end == 0 ? length : end;
        getJspContext().getOut().write(maxLength >= length ? value : (value.substring(start, end) + append));
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public void setAppend(String append) {
        this.append = append;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }
}
