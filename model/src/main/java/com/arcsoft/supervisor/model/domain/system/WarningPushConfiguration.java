package com.arcsoft.supervisor.model.domain.system;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Entity class for push content detect log to client.
 *
 * @author zw.
 */
@Entity
@Table(name = "configuration_warning_push")
@DiscriminatorValue("5")
public class WarningPushConfiguration extends Configuration {

    private static final String REMOTE_URL_PATTERN = "http://%s:54322/WarningInfo_app";
    private static final String REMOTE_URL_PATTERN2 = "http://%s/WarningInfo_app";

    /**
     * The ip address of client.
     */
    private String ip;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        if(StringUtils.isNotBlank(ip) && ip.contains(":"))
        {
            String[] ar = ip.split(":");
            this.ip = String.format( "%s:%d",ar[0],Integer.parseInt(ar[1]));
            return;
        }
        this.ip = ip;
    }

    /**
     * Returns the remote publish http url.
     *
     * @return the remote publish http url or empty string with {@code ""} if {@link #ip} is null or empty
     */
    public String getRemoteUrl() {
        if(StringUtils.isNotBlank(ip))
        {
            return ip.contains(":")?String.format(REMOTE_URL_PATTERN2, ip):String.format(REMOTE_URL_PATTERN, ip);
        }
        return StringUtils.EMPTY;
    }
}
