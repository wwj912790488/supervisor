package com.arcsoft.supervisor.commons.jsp.tag.profile;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

/**
 * Abstract support class of profile for subclass extends.
 *
 * @author zw.
 */
public abstract class AbstractProfileTagSupport extends SimpleTagSupport {

    @Override
    public void doTag() throws JspException, IOException {
        if (isInProfile()) {
            getJspBody().invoke(getJspContext().getOut());
        }
    }

    /**
     * Checks profile is matched or not.
     *
     * @return {@code true} if profile is matched
     */
    protected abstract boolean isInProfile();
}
