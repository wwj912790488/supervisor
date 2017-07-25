package com.arcsoft.supervisor.web.settings;

import com.arcsoft.supervisor.model.domain.system.SmsWarningConfiguration;

/**
 * A bean class for retrieves parameters from request.
 *
 * @author zw.
 */
public class SmsCfg {

    private SmsWarningConfiguration smsCfg;

    private String phoneNumber;

    public SmsWarningConfiguration getSmsCfg() {
        return smsCfg;
    }

    public void setSmsCfg(SmsWarningConfiguration smsCfg) {
        this.smsCfg = smsCfg;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
