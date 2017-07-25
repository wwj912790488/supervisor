package com.arcsoft.supervisor.agent.service.task.support;

import com.arcsoft.supervisor.agent.service.agent.AgentService;
import com.arcsoft.supervisor.agent.service.task.RtspStreamFileResourceManager;
import com.arcsoft.supervisor.cd.data.ContentServerInfo;
import com.arcsoft.supervisor.transcoder.*;
import com.arcsoft.supervisor.transcoder.spi.single.NativeTranscodingParams;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * A base <code>transcoder</code> processor support class to provides some
 * convenient methods for child class to do extends.
 *
 * @author zw.
 */
public abstract class AbstractTranscoderTaskProcessorSupport extends AbstractTaskProcessorSupport {

    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    private static final String UDP_PREFIX = "udp://";

    private static final String UDP_PORT_SEPARATOR = ":";

    /**
     * The path of store xml of transcoder.
     */
    private static final String TRANSCODER_TMP_DIR = StringUtils.appendIfMissing(AppConfig.getProperty(AppConfig.KEY_TRANSCODER_WORK_DIR), "/");

    private static final String TRANSCODER_WORK_DIR = StringUtils.appendIfMissing(AppConfig.getProperty(AppConfig.KEY_TRANSCODER_PATH), "/");

    private final String transcoderWorkDir;

    //todo: Checks and create directory use spring initialize method instead of static initialize.
    /**
     * Checks and create directory of transcoder if not existed.
     *
     */
    static {
        File file = new File(TRANSCODER_TMP_DIR);
        if (!file.exists() && !file.mkdirs()) {
            throw new TranscoderTmpFolderNotExistException(TRANSCODER_TMP_DIR);
        }
    }

    protected AbstractTranscoderTaskProcessorSupport() {
        this.transcoderWorkDir = new File(TRANSCODER_WORK_DIR).getParentFile().getAbsolutePath() + "/";
    }

    public static class TranscoderTmpFolderNotExistException extends RuntimeException {
        public TranscoderTmpFolderNotExistException(String message) {
            super(message);
        }
    }

    /**
     * Returns the path of transcoder tmp directory.
     * <p>The returned path is end with {@code /}
     *
     * @return the path of transcoder tmp directory
     */
    public String getTranscoderTmpDir() {
        return TRANSCODER_TMP_DIR;
    }

    /**
     * Returns the path of transcoder folder.
     * <p>The returned path is end with {@code /}
     *
     * @return the file path of transcoder folder
     */
    public String getTranscoderWorkDir() {
        return transcoderWorkDir;
    }

    private Transcoder transcoder;

    private AgentService agentService;

    private RtspStreamFileResourceManager rtspStreamFileResourceManager;

    private ContentServerInfo contentServerInfo;

    public ContentServerInfo getContentServerInfo() {
		return contentServerInfo;
	}

	public void setContentServerInfo(ContentServerInfo contentServerInfo) {
		this.contentServerInfo = contentServerInfo;
	}

	public void setRtspStreamFileResourceManager(RtspStreamFileResourceManager rtspStreamFileResourceManager) {
        this.rtspStreamFileResourceManager = rtspStreamFileResourceManager;
    }

    public RtspStreamFileResourceManager getRtspStreamFileResourceManager() {
        return rtspStreamFileResourceManager;
    }

    public Transcoder getTranscoder() {
        return transcoder;
    }

    public void setTranscoder(Transcoder transcoder) {
        this.transcoder = transcoder;
    }

    public AgentService getAgentService() {
        return agentService;
    }

    public void setAgentService(AgentService agentService) {
        this.agentService = agentService;
    }


    /**
     * Composes the udp url with specific ip and port.
     *
     * @param ip the ip address
     * @param udpPort the port of udp stream
     * @return the completed udp url
     */
    protected String composeUdpUrl(String ip, int udpPort) {
        return UDP_PREFIX + ip + UDP_PORT_SEPARATOR + udpPort;
    }

    private static final String RTMP_STREAM_URL_PREFIX = "rtmp://";
    private static final String RTMP_STREAM_URL_SUFFIX = ".stream";
    private static final String RTMP_STREAM_URL_PORT = "1935";


    protected String composeRTMPUrl(String serverIp, String fileName) {
        return RTMP_STREAM_URL_PREFIX + serverIp + ":" + RTMP_STREAM_URL_PORT + "/live/" + fileName + RTMP_STREAM_URL_SUFFIX;
    }

    /**
     * Creates a new {@code ITranscodingTracker} use given {@code taskId} and {@code transcoderXml}.
     *
     * @param taskId        the identify value of task
     * @param transcoderXml the xml of transcoder
     * @return the instance of {@code ITranscodingTracker} with {@code normal} priority
     */
    protected ITranscodingTracker createITranscodingTracker(int taskId, String transcoderXml) {
        return transcoder.createTranscodingTracker(new TranscodingKey(taskId),
                TaskRuntimePrioity.PRIORITY_NORMAL,
                new NativeTranscodingParams(transcoderXml)
        );
    }

    @Override
    public String getTranscoderXml(int taskId) {
        ITranscodingTracker tracker = getITranscodingTracker(taskId);
        if (tracker == null) {
            throw new NullPointerException("The tracker of " + taskId + " is not exist.");
        }
        return tracker.getTranscodingParams().getParamAsXml();
    }

    @Override
    public boolean isRunning(int taskId) {
        ITranscodingTracker transcodingTracker = getITranscodingTracker(taskId);
        return transcodingTracker != null
                && transcodingTracker.getTranscoderRunningStatus() != ITranscodingTracker.TRANSCODING_EXIT;
    }

    @Override
    public String getProgressXml(int taskId) {
        ITranscodingTracker transcodingTracker = getITranscodingTracker(taskId);
        if (transcodingTracker != null) {
            TranscodingInfo info = transcodingTracker.getProgressInfo(null);
            return info == null ? null : info.toString();
        }
        return null;
    }

    /**
     * Retrieves the {@code ITranscodingTracker} with given <code>taskId</code>.
     *
     * @param taskId the identify value of task id
     * @return {@link ITranscodingTracker} according to given task id.
     */
    protected ITranscodingTracker getITranscodingTracker(int taskId) {
        return transcoder.getTranscodingTracker(new TranscodingKey(taskId));
    }

    @Override
    public void stop(int taskId) {
        TranscodingKey transcodingKey = new TranscodingKey(taskId);
        ITranscodingTracker transcodingTracker = transcoder.getTranscodingTracker(transcodingKey);
        if (transcodingTracker != null) {
            getRtspStreamFileResourceManager().deleteStreamFile(transcodingTracker);
            transcoder.cancelTask(new TranscodingKey(taskId), Transcoder.STOP_FLAG_MANUAL);
        }
    }
}
