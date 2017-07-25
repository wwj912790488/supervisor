package com.arcsoft.supervisor.commons.jsp.tag.profile;

import com.arcsoft.supervisor.utils.app.Environment;

/**
 * sartf profile tag.
 *
 * @author zw.
 */
public class SartfProfileTag extends AbstractProfileTagSupport {

    @Override
    protected boolean isInProfile() {
        return Environment.getProfiler().isSartf();
    }
}
