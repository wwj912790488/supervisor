package com.arcsoft.supervisor.agent.service.task.processor;

import com.arcsoft.supervisor.agent.service.task.StartTaskException;
import com.arcsoft.supervisor.agent.service.task.TranscoderXmlUtils;
import com.arcsoft.supervisor.agent.service.task.support.AbstractTranscoderTaskProcessorSupport;
import com.arcsoft.supervisor.model.vo.task.AbstractTaskParams;
import com.arcsoft.supervisor.model.vo.task.rtsp.MobileConfig;
import com.arcsoft.supervisor.model.vo.task.rtsp.RTSPTaskParams;
import com.arcsoft.supervisor.transcoder.ITranscodingTracker;
import com.arcsoft.supervisor.transcoder.TranscodingKey;
import com.arcsoft.supervisor.utils.app.Environment;
import freemarker.template.TemplateException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A rtsp processor implementation to handling all of rtsp tasks.
 *
 * @author zw.
 */
public class RtspTaskProcessor extends AbstractTranscoderTaskProcessorSupport {

    private static final int DEFAULT_THUMBNAIL_WIDTH = 110;

    @Override
    public void start(AbstractTaskParams task) throws StartTaskException {
        LOG.info("Receive start task: " + task);
        RTSPTaskParams rtspTask = (RTSPTaskParams) task;
        createSdpIfEnable(rtspTask);
        if (rtspTask.getEnableRecord()){
            createRecordPathIfNotExist(rtspTask.getRecordPath());
        }
        ITranscodingTracker tracker = createITranscodingTracker(task.getId(), getTranscoderXmlString(rtspTask));

        int result = getTranscoder().startTask(tracker);
        if (result != 0) {
            throw new StartTaskException(String.format("Failed to start task [id=%s], return code=%d", task.getId(), result));
        } else if (isSupportMobile(rtspTask) && Environment.getProfiler().isEnableWowza()) {
            // Only execute when wowza is enabled
            doAfterMobileTaskRunning(rtspTask, tracker);
        }

    }

    private String getTranscoderXmlString(RTSPTaskParams rtspTask) throws StartTaskException {
        String transcoderXmlContent;
        try {
            transcoderXmlContent = TranscoderXmlUtils.generateTranscoderXml(getTemplateName(),
                    assemblyModel(rtspTask));
        } catch (TemplateException | IOException e) {
            throw new StartTaskException(e);
        }
        return transcoderXmlContent;
    }

    private void createSdpIfEnable(RTSPTaskParams rtspTask) throws StartTaskException {
        String address = rtspTask.getUdpUrl();
        byte[] sdp = rtspTask.getSdpFile();
        if(address.startsWith("sdp") && sdp != null) {
        	File sdpFile = new File(getTranscoderWorkDir() + address);
        	File parentFile = sdpFile.getParentFile();
        	if(!parentFile.exists() && !parentFile.mkdirs()) {
        		throw new StartTaskException();
        	}
        	try {
				Files.write(sdpFile.toPath(), sdp);
			} catch (IOException e) {
				throw new StartTaskException(e);
			}
        }
    }

    /**
     *
     * Retrieves template name by currently profile.
     *
     * @return {@code transcoder-rtsp-sartf.tpl} if profile is {@code sartf} otherwise is {@code transcoder-rtsp.tpl}
     */
    private String getTemplateName() {
        return Environment.getProfiler().isSartf() ? "transcoder-rtsp-sartf.tpl" : "transcoder-rtsp.tpl";
    }


    private void doAfterMobileTaskRunning(RTSPTaskParams rtspTask, ITranscodingTracker tracker) {
        Map<Byte, String> streamFiles = new HashMap<>(2);
        for (MobileConfig config : filterMobileConfigs(rtspTask.getConfigs())) {

            streamFiles.put(
                    config.getType(),
                    getRtspStreamFileResourceManager()
                            .composeRtspStreamFilePath(
                                    rtspTask.getRtspStoragePath(),
                                    config.getFileName()
                            )
            );

            try {
                getRtspStreamFileResourceManager().writeUrlToStreamFile(
                        composeUdpUrl(rtspTask.getRtspHostIp(), config.getPortNumber()),
                        streamFiles.get(config.getType())
                );

            } catch (IOException e) {
                LOG.error("Failed to write stream file of task [id={}]", rtspTask.getId());
            }

        }
        String[] streamFilePaths = Environment.getProfiler().isSartf()
                ? new String[] { streamFiles.get((byte) 0) }
                : new String[] { streamFiles.get((byte) 0), streamFiles.get((byte) 1) };

        getRtspStreamFileResourceManager().setStreamFilePathToItranscodingTracker(
                tracker,
                streamFilePaths
        );
    }

    /**
     * Filter configs by profile.
     *
     * <p>Only retrieves hd config if currently profile is {@code sartf} otherwise will retrieves
     * all of {@code configs}.</p>
     *
     * @param configs the mobile configs of task
     * @return hd config or all of configs
     */
    private List<MobileConfig> filterMobileConfigs(List<MobileConfig> configs) {
        List<MobileConfig> mobileConfigs = new ArrayList<>();
        for (MobileConfig config : configs) {
            if (Environment.getProfiler().isSartf()) {
                if (config.isHd()) {
                    mobileConfigs.add(config);
                }
            } else {
                mobileConfigs.add(config);
            }
        }
        return mobileConfigs;
    }

    @Override
    public byte[] getThumbnail(int taskId) throws UnsupportedOperationException {
        final byte[] defaultData = new byte[0];
        ITranscodingTracker transcodingTracker = getTranscoder().getTranscodingTracker(new TranscodingKey(taskId));
        if (transcodingTracker != null) {
            InputStream in = transcodingTracker.getThumbnail(DEFAULT_THUMBNAIL_WIDTH);
            if (in != null) {
                ByteArrayInputStream bis = (ByteArrayInputStream) in;
                byte[] data = new byte[bis.available()];
                try {
                    bis.read(data);
                } catch (IOException e) {
                    data = defaultData;
                }
                return data;
            }
        }
        return defaultData;
    }

    private boolean isSupportMobile(RTSPTaskParams rtspTask) {
        return rtspTask.getConfigs() != null && rtspTask.getConfigs().size() > 0;
    }

    private Map<String, Object> assemblyModel(RTSPTaskParams task) {
        Map<String, Object> model = new HashMap<>();
        boolean videopassthrough = false;
        boolean audiopassthrough = false;
        if (isSupportMobile(task)) {
            MobileConfigGroup configGroup = groupMobileConfig(task.getConfigs());
            model.put("hdConfig", configGroup.getHdConfig());
            model.put("sdConfig", configGroup.getSdConfig());
            if (Environment.getProfiler().isEnableWowza()) {
                model.put("sdUdpPort", configGroup.getSdConfig().getPortNumber());
                model.put("hdUdpPort", configGroup.getHdConfig().getPortNumber());
            } else {
                // Use rtmp
                model.put("sdUrl", composeRTMPUrl(task.getRtspHostIp(), configGroup.getSdConfig().getFileName()));
                model.put("hdUrl", composeRTMPUrl(task.getRtspHostIp(), configGroup.getHdConfig().getFileName()));
            }
            model.put("rtspServerIp", task.getRtspHostIp());
            model.put("isWowzaRtsp", Environment.getProfiler().isEnableWowza());

            String vcodec = task.getVideocodec();
            if( vcodec!=null &&
                    vcodec.compareToIgnoreCase("H.264")==0 && configGroup.getHdConfig().getWidth()==0 )
            {
                videopassthrough = true;

/*                if(task.getAudiocodec().compareToIgnoreCase("AAC")==0)
                {
                    audiopassthrough = true;
                }*/
            }
        }
        model.put("isSupportMobile", isSupportMobile(task));
        model.put("udpUrl", task.getUdpUrl());
        model.put("audioId", task.getAudioId());
        model.put("programId", task.getProgramId());
        model.put("isVideoPassthrough",videopassthrough);
        model.put("isAudioPassthrough",audiopassthrough);
        model.put("id", task.getId());
        model.put("enableRecord", task.getEnableRecord());
        if (task.getEnableRecord()){
            model.put("recordTimeSegment", getRecordTimeSegmentByFormat(task.getRecordFormat()));
            model.put("recordBasePath", task.getRecordPath());
            model.put("recordFileName", task.getRecordFileName());
            model.put("recordFormat", task.getRecordFormat());
        }
        model.put("protocol", task.getProtocol());
        model.put("port", task.getPort());
        return model;
    }

    private MobileConfigGroup groupMobileConfig(List<MobileConfig> configs) {
        MobileConfigGroup group = new MobileConfigGroup();
        for (MobileConfig config : configs) {
            if (config.isSd()) {
                group.setSdConfig(config);
            } else {
                group.setHdConfig(config);
            }
        }
        return group;
    }

    private void createRecordPathIfNotExist(String basePath) {
        File file = new File(basePath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    private short getRecordTimeSegmentByFormat(Byte format) {
        return (short) (format == 0 ? 300 : 60);
    }

    /**
     * Holds the sd and hd mobile config.
     */
    private static class MobileConfigGroup {

        private MobileConfig hdConfig;
        private MobileConfig sdConfig;

        public void setHdConfig(MobileConfig hdConfig) {
            this.hdConfig = hdConfig;
        }

        public void setSdConfig(MobileConfig sdConfig) {
            this.sdConfig = sdConfig;
        }

        public MobileConfig getHdConfig() {
            return hdConfig;
        }

        public MobileConfig getSdConfig() {
            return sdConfig;
        }

    }

}
