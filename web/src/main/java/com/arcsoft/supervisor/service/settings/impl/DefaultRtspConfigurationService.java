package com.arcsoft.supervisor.service.settings.impl;

import com.arcsoft.supervisor.exception.service.BusinessExceptionDescription;
import com.arcsoft.supervisor.model.domain.system.RtspConfiguration;
import com.arcsoft.supervisor.repository.settings.RtspConfigurationRepository;
import com.arcsoft.supervisor.service.TransactionSupport;
import com.arcsoft.supervisor.service.settings.RtspConfigurationService;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Service implementation for <tt>RtspConfiguration</tt>.
 *
 * @author zw.
 */
@Service("rtspConfigurationService")
public class DefaultRtspConfigurationService extends AbstractConfigurationService<RtspConfiguration>
        implements TransactionSupport, RtspConfigurationService {

    private static final String RTSP_EXTEND_NAME = ".stream";

    private static final String PRIVATE_NETWORK_10 = "10\\.(([0-1][0-9]{1,2}|2[0-4][0-9]|25[0-5]|[0-9]{1,2})\\.){2}([0-1][0-9]{1,2}|2[0-4][0-9]|25[0-5]|[0-9]{1,2})";

    private static final String PRIVATE_NETWORK_172 = "172\\.((1[6-9])|(2[0-9])|(3[0-6]))\\.(([0-1][0-9]{1,2}|2[0-4][0-9]|25[0-5]|[0-9]{1,2})\\.?+){2}";

    private static final String PRIVATE_NETWORK_192 = "192\\.168\\.(([0-1][0-9]{1,2}|2[0-4][0-9]|25[0-5]|[0-9]{1,2})\\.?+){2}";

    private static final String PUBLIC_NETWORK = "([01][0-9][0-9]\\.|2[0-4][0-9]\\.|[0-9][0-9]\\.|25[0-5]\\.|[0-9]\\.)([01][0-9][0-9]\\.|2[0-4][0-9]\\.|[0-9][0-9]\\.|25[0-5]\\.|[0-9]\\.)([01][0-9][0-9]\\.|2[0-4][0-9]\\.|[0-9][0-9]\\.|25[0-5]\\.|[0-9]\\.)([01][0-9][0-9]|2[0-4][0-9]|25[0-5]|[0-9][0-9]|[0-9])";

    private static final Pattern PATTERN_PRIVATE_NETWORK_10 = Pattern.compile(PRIVATE_NETWORK_10);

    private static final Pattern PATTERN_PRIVATE_NETWORK_172 = Pattern.compile(PRIVATE_NETWORK_172);

    private static final Pattern PATTERN_PRIVATE_NETWORK_192 = Pattern.compile(PRIVATE_NETWORK_192);

    private static final Pattern PATTERN_PUBLIC_NETWORK = Pattern.compile(PUBLIC_NETWORK);


    @Autowired
    protected DefaultRtspConfigurationService(RtspConfigurationRepository repository) {
        super(repository);
    }

    @Override
    public RtspConfiguration saveOrUpdate(RtspConfiguration rtspConfiguration) {
        rtspConfiguration.appendSuffixToMixedPublishUrlIfMissing();
        return super.saveOrUpdate(rtspConfiguration);
    }


    @Override
    public String composeUrl(String rtspFileName, final String ip) {
        logger.info("composeUrl with ip : " + ip);
        RtspConfiguration cfg = getFromCache();
        if (cfg == null) {
            throw BusinessExceptionDescription.RTSP_SERVER_PUBLISH_URL_NOT_EXIST.exception();
        }

        Optional<String> matchedUrl = FluentIterable.from(cfg.getPublishUrls())
                .filter(new Predicate<String>() {
                    @Override
                    public boolean apply(String url) {
                        if (isPrivateNetwork10(ip)) {
                            return isPrivateNetwork10(url);
                        } else if(isPrivateNetwork172(ip)) {
                            return isPrivateNetwork172(url);
                        } else if (isPrivateNetwork192(ip)) {
                            return isPrivateNetwork192(url);
                        } else if (isPublicNetwork(ip)){
                            return !isPrivateNetwork10(url)&&!isPrivateNetwork172(url)&&!isPrivateNetwork192(url)&&isPublicNetwork(url);
                        }
                        return false;
                    }
                }).first();

        if (matchedUrl.isPresent()) {
            logger.info("matchedUrl : " + matchedUrl.get());
            return matchedUrl.get() + rtspFileName + RTSP_EXTEND_NAME;
        } else {
            List<String> urls = cfg.getPublishUrls();
            if(urls.size() > 0) {
                logger.info("matchedUrl : " + urls.get(0));
                return urls.get(0) + rtspFileName + RTSP_EXTEND_NAME;
            } else {
                throw BusinessExceptionDescription.RTSP_PUBLISHER_IP_NOT_MATCH.exception();
            }
        }
    }

    @Override
    public String getStoragePath() {
        RtspConfiguration cfg = getFromCache();
        if (cfg != null && StringUtils.isNotBlank(cfg.getPublishFolderPath())) {
            return cfg.getPublishFolderPath();
        }
        throw BusinessExceptionDescription.RTSP_SERVER_STORAGE_PATH_NOT_EXIST.exception();
    }

    @Override
    public String getIp() {
        RtspConfiguration cfg = getFromCache();
        if (cfg == null || StringUtils.isBlank(cfg.getIp())) {
            throw BusinessExceptionDescription.RTSP_SERVER_IP_IS_EMPTY.exception();
        }
        return cfg.getIp();
    }

    private boolean isPrivateNetwork10(String ip) {
        return PATTERN_PRIVATE_NETWORK_10.matcher(ip).find();
    }

    private boolean isPrivateNetwork172(String ip) {
        return PATTERN_PRIVATE_NETWORK_172.matcher(ip).find();
    }

    private boolean isPrivateNetwork192(String ip) {
        return PATTERN_PRIVATE_NETWORK_192.matcher(ip).find();
    }

    private boolean isPublicNetwork(String ip) {
        return PATTERN_PUBLIC_NETWORK.matcher(ip).find();
    }

}
