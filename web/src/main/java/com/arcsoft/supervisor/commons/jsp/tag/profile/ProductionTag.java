package com.arcsoft.supervisor.commons.jsp.tag.profile;

import com.arcsoft.supervisor.utils.app.Environment;

/**
 * Production profile tag.
 *
 * @author zw.
 */
public class ProductionTag extends AbstractProfileTagSupport {

    @Override
    protected boolean isInProfile() {
        return Environment.getProfiler().isProduction();
    }
}
