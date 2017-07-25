package com.arcsoft.supervisor.commons.jsp.tag.profile;

import com.arcsoft.supervisor.utils.app.Environment;
import org.apache.commons.lang3.StringUtils;

/**
 * Profile tag.
 *
 * @author zw.
 */
public class ProfileTag extends AbstractProfileTagSupport {

    private String[] profileArr;
    private Op op;

    public void setHasProfiles(String profileStr) {
        this.profileArr = StringUtils.defaultString(profileStr).split(",");
        op = Op.ALL;
    }

    public void setHasAnyProfile(String profileStr) {
        setHasProfiles(profileStr);
        op = Op.ANY;
    }

    public void setNothasProfiles(String profileStr) {
        setHasProfiles(profileStr);
        op = Op.NOTIN;
    }

    @Override
    protected boolean isInProfile() {
        boolean result = false;
        switch (op) {
            case ALL:
                result = Environment.getProfiler().hasProfiles(profileArr);
                break;
            case ANY:
                result = Environment.getProfiler().hasAnyProfile(profileArr);
                break;
            case NOTIN:
                result = !Environment.getProfiler().hasProfiles(profileArr);
                break;
        }
        return result;
    }

    protected enum Op {
        ALL,
        ANY,
        NOTIN
    }
}
