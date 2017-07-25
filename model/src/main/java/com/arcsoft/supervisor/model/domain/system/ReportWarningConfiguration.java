package com.arcsoft.supervisor.model.domain.system;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by wwj on 2017/2/7.
 */
@Entity
@Table(name = "configuration_warning_report")
@DiscriminatorValue("5")
public class ReportWarningConfiguration  extends Configuration{

    private String ip;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

}
