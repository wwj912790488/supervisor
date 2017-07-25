package com.arcsoft.supervisor.service.settings;

import com.arcsoft.supervisor.exception.service.BusinessException;
import com.arcsoft.supervisor.exception.service.BusinessExceptionDescription;
import com.arcsoft.supervisor.model.domain.system.RtspConfiguration;

/**
 * @author zw.
 */
public interface RtspConfigurationService extends ConfigurationService<RtspConfiguration> {

    /**
     * Compose the completed url of rtsp with given <code>rtspFileName</code>.
     *
     * @param rtspFileName the name of rtsp file.The name should not contains any extend name
     * @param ip the access ip
     * @return the completed url
     * @throws BusinessException with below:
     * <ul>
     *     <li>{@link BusinessExceptionDescription#RTSP_SERVER_PUBLISH_URL_NOT_EXIST}</li>
     * </ul>
     */
    String composeUrl(String rtspFileName, String ip);


    /**
     * Returns the storage path of rtsp server
     *
     * @return the storage path of rtsp server
     * @throws BusinessException with below:
     * <ul>
     *     <li>{@link BusinessExceptionDescription#RTSP_SERVER_STORAGE_PATH_NOT_EXIST}</li>
     * </ul>
     */
    String getStoragePath();


    /**
     * Returns the ip of rtsp server.
     *
     * @return the ip
     * @throws BusinessException with below:
     * <ul>
     *     <li>{@link BusinessExceptionDescription#RTSP_SERVER_IP_IS_EMPTY}</li>
     * </ul>
     */
    String getIp();

}
