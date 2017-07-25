package com.arcsoft.supervisor.model.vo.task;

import com.arcsoft.supervisor.model.vo.task.compose.ComposeTaskParams;
import com.arcsoft.supervisor.model.vo.task.rtsp.RTSPTaskParams;
import com.arcsoft.supervisor.model.vo.task.usercompose.UserComposeTaskParams;

import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * @author zw.
 */
@XmlSeeAlso({ComposeTaskParams.class, RTSPTaskParams.class, UserComposeTaskParams.class})
public abstract class AbstractRtspParams extends AbstractTaskParams {

    /**
     * The rtsp server publish path.
     */
    private String rtspStoragePath;

    /**
     * The ip address of rtsp server.
     */
    private String rtspHostIp;

    public String getRtspHostIp() {
        return rtspHostIp;
    }

    public void setRtspHostIp(String rtspHostIp) {
        this.rtspHostIp = rtspHostIp;
    }

    public String getRtspStoragePath() {
        return rtspStoragePath;
    }

    public void setRtspStoragePath(String rtspStoragePath) {
        this.rtspStoragePath = rtspStoragePath;
    }
}
