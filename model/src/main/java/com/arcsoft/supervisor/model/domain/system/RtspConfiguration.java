package com.arcsoft.supervisor.model.domain.system;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Entity class for <code>Rtsp</code> of Configuration.
 *
 * @author zw.
 */
@Entity
@Table(name = "configuration_rtsp")
@DiscriminatorValue("2")
@DynamicUpdate
public class RtspConfiguration extends Configuration {


    private static final String PATH_SEPARATOR_SUFFIX = "/";
    private static final String MIXED_PUBLISH_URL_SEPARATOR = ",";

    /**
     * The multiple rtsp publish url of rtsp server separator by comma.
     */
    private String mixedPublishUrl;

    /**
     * The folder path of rtsp publish path.
     */
    private String publishFolderPath;

    /**
     * The LAN ip of rtsp server.The ip will used as the output target of transcoder.
     */
    private String ip;

    public RtspConfiguration() {
    }

    public String getMixedPublishUrl() {
        return mixedPublishUrl;
    }

    public void setMixedPublishUrl(String mixedPublishUrl) {
        this.mixedPublishUrl = mixedPublishUrl;
    }

    public String getPublishFolderPath() {
        return publishFolderPath;
    }

    public void setPublishFolderPath(String publishFolderPath) {
        this.publishFolderPath = StringUtils.appendIfMissing(publishFolderPath, PATH_SEPARATOR_SUFFIX);
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * Returns the list of publish url converted from {@link #mixedPublishUrl}.
     *
     * @return the url list converted from {@link #mixedPublishUrl}.
     */
    @JsonIgnore
    public List<String> getPublishUrls() {
        if (StringUtils.isBlank(mixedPublishUrl)) {
            return Collections.emptyList();
        }
        return Arrays.asList(this.mixedPublishUrl.split(MIXED_PUBLISH_URL_SEPARATOR));
    }

    /**
     * Append a suffix with <code>/</code> to each url of {@link #mixedPublishUrl}.
     * <p><b>Notes: if {@link #mixedPublishUrl} is not empty then call this method
     * will reset the value of {@link #mixedPublishUrl}.</b></p>
     */
    public void appendSuffixToMixedPublishUrlIfMissing() {
        if (StringUtils.isNotBlank(mixedPublishUrl)) {
            List<String> urls = new ArrayList<>();
            for (String url : mixedPublishUrl.split(MIXED_PUBLISH_URL_SEPARATOR)) {
                urls.add(StringUtils.appendIfMissing(url, PATH_SEPARATOR_SUFFIX));
            }
            this.mixedPublishUrl = StringUtils.join(urls, MIXED_PUBLISH_URL_SEPARATOR);
        }
    }
}
