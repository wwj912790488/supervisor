package com.arcsoft.supervisor.model.domain.system;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Entity class for content detect warning configuration.
 *
 * @author zw.
 */
@Entity
@Table(name = "configuration_warning_sms")
@DiscriminatorValue("4")
public class SmsWarningConfiguration extends Configuration {

    /**
     * The url of mobile send message service.
     */
    private String url;

    /**
     * The account of mobile send message service.
     */
    private String account;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
