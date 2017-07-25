package com.arcsoft.supervisor.agent.service.task.support;

import com.arcsoft.supervisor.agent.service.task.StartTaskException;
import com.arcsoft.supervisor.agent.service.task.TranscoderXmlUtils;
import com.arcsoft.supervisor.agent.service.task.TranscodingTrackerResource;
import com.arcsoft.supervisor.agent.service.task.resource.ComposeTaskTranscodingTrackerResource;
import com.arcsoft.supervisor.agent.service.task.resource.TaskResourceHolder;
import com.arcsoft.supervisor.model.vo.task.AbstractTaskParams;
import com.arcsoft.supervisor.model.vo.task.compose.ComposeTaskParams;
import com.arcsoft.supervisor.model.vo.task.compose.TaskOutputResolutionAndIndexMapper;
import com.arcsoft.supervisor.transcoder.ITranscodingTracker;
import com.arcsoft.supervisor.transcoder.TranscodingKey;
import com.arcsoft.supervisor.utils.app.Environment;
import freemarker.template.TemplateException;
import org.apache.commons.codec.binary.StringUtils;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zw.
 */
public abstract class AbstractIpComposeStreamTaskProcessorSupport extends AbstractComposeStreamTaskProcessor {

    public void start(AbstractTaskParams task) throws StartTaskException {
        start((ComposeTaskParams) task);
    }

    /**
     * Starts task using <code>ComposeTaskParams</code>.
     *
     * @param task the {@link com.arcsoft.supervisor.model.vo.task.compose.ComposeTaskParams}
     * @return the ports result object of IP-Stream compose udp and rtsp bind to
     * @throws StartTaskException
     */
    public abstract int start(ComposeTaskParams task) throws StartTaskException;

    @Override
    public TranscoderXmlAndTemplateModel createTranscoderModel(AbstractTaskParams taskParams) throws IOException, TemplateException {
        ComposeTaskParams task = (ComposeTaskParams) taskParams;
        List<String> seifiles = createSEIMessageXmlFile(task);
        String path = null;
        if (IsNeedContentDetect(task.getTranscoderTemplate())) {
            path = createContentDetectFile(task);
        }

        Map<String, Object> model = assemblyModel(task, path, seifiles);
        String xmlContent = TranscoderXmlUtils.generateTranscoderXmlFromTemplateString(
                task.getTranscoderTemplate(),
                model
        );
        return new TranscoderXmlAndTemplateModel(xmlContent, model);
    }

    private boolean IsNeedContentDetect(String xmlstr) {
        String path = null;
        String xpathExp = "/TranscoderTask/MultiScreenInputs/ContentDetectXml";
        XPathFactory f = XPathFactory.newInstance();
        XPath p = f.newXPath();
        try {
            path = p.evaluate(xpathExp, new InputSource(new StringReader(xmlstr)));
        } catch (Exception e) {

        }
        return path != null && !path.isEmpty();
    }

    protected String createContentDetectFile(ComposeTaskParams task) throws IOException, TemplateException {
        String contentDetectXmlContent = TranscoderXmlUtils.generateTranscoderXml("content-detect.tpl",
                assemblyContentDetectModel(task));
        String fileName = String.format("%s%d_%d_content_detect.xml", getTranscoderTmpDir(), task.getId(),
                System.currentTimeMillis());

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(fileName), "utf-8"))) {
            writer.write(contentDetectXmlContent);
        }
        return fileName;
    }

    protected List<String> createSEIMessageXmlFile(ComposeTaskParams task) throws IOException, TemplateException {
        List<String> filenames = null;
        List<String> seimessages = task.getSeiMessages();
        if (seimessages != null && seimessages.size() > 0) {
            filenames = new ArrayList<String>();
            for (int index = 0; index < seimessages.size(); index++) {
                String seiContent = seimessages.get(index);
                String fileName = String.format("%s%d_%d_%d_sei_message.xml", getTranscoderTmpDir(), task.getId(), index, System.currentTimeMillis());
                File file = new File(fileName);
                try {
                    file.delete();
                } catch (Exception e) {
                }

                try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(fileName), "utf-8"))) {
                    writer.write(seiContent);

                    filenames.add(fileName);
                }
            }
        }

        return filenames;
    }

    private Map<String, Object> assemblyContentDetectModel(ComposeTaskParams task) {
        Map<String, Object> model = new HashMap<>();
        model.put("taskId", task.getId());
        model.put("ip", getContentServerInfo().getIp());
        model.put("port", getContentServerInfo().getListenPort());
        model.put("contentDetectConfigs", task.getContentDetectConfigs());
        return model;
    }

    /**
     * Assemble the given {@code task port rtspUdpPort} to {@code Map} will used in transcoder xml
     * template.
     *
     * @param task the instance of {@code ComposeTaskParams}
     * @return a key-value pairs
     */
    protected Map<String, Object> assemblyModel(ComposeTaskParams task, String path, List<String> seifiles) {
        Map<String, Object> model = new HashMap<>();
        model.put("outputIp", task.getTargetIp()==null?"127.0.0.1":task.getTargetIp());
        if (path == null) {
            if (model.containsKey("contentDetectConfigPath")) {
                model.remove("contentDetectConfigPath");
            }
        } else {
            model.put("contentDetectConfigPath", path);
        }
        if (task.getScreenOutputPort() != null) {
            model.put("outputPort", task.getScreenOutputPort()<=0?9999:task.getScreenOutputPort());
        }
        if (Environment.getProfiler().isEnableWowza()) {
            model.put("rtspOutputIp", task.getRtspHostIp());
            if (task.getMobileOutputPort() != null) {
                model.put("rtspOutputPort", task.getMobileOutputPort());
            }
        } else {
            if (task.getTargetRtmpUrl() != null) {
                model.put("rtmpUrl", task.getTargetRtmpUrl());
            } else {
                model.put("rtmpUrl", composeRTMPUrl(task.getRtspHostIp(), task.getRtspFileName()));
            }

        }
        model.put("rtmpopsUrl", composeRTMPUrl(task.getRtspHostIp(), task.getRtmpOPSFileName()));

        if (seifiles != null && seifiles.size() > 0) {
            for (int i = 0; i < seifiles.size(); i++) {
                model.put("seimessage_path_" + i, seifiles.get(i));
            }
        }

        return model;
    }

    protected Map<String, Object> assemblyModelUpdate(Map<String, Object> oldmodel, ComposeTaskParams task, String path, List<String> seifiles) {
        Map<String, Object> model = oldmodel;
        if (path == null) {
            if (model.containsKey("contentDetectConfigPath")) {
                model.remove("contentDetectConfigPath");
            }
        } else {
            model.put("contentDetectConfigPath", path);
        }
        if (seifiles != null && seifiles.size() > 0) {
            for (int i = 0; i < seifiles.size(); i++) {
                model.put("seimessage_path_" + i, seifiles.get(i));
            }
        }

        return model;
    }

    public void warnBorder(int taskId, int index, boolean isShow) {
        ITranscodingTracker transcodingTracker = getTranscoder().getTranscodingTracker(new TranscodingKey(taskId));
        if (transcodingTracker != null) {
            transcodingTracker.warnBorder(index, isShow);
        }
    }


    @SuppressWarnings("all")
    public void displayMessage(int taskId, int taskType, String message) {
        ITranscodingTracker transcodingTracker = getTranscoder().getTranscodingTracker(new TranscodingKey(taskId));
        if (transcodingTracker != null
                && transcodingTracker.getUserData() != null
                && transcodingTracker.getUserData() instanceof TaskResourceHolder) {
            TaskResourceHolder resourceHolder = (TaskResourceHolder) transcodingTracker.getUserData();
            TranscodingTrackerResource<ComposeTaskTranscodingTrackerResource> composeTaskTrackerResource =
                    resourceHolder.getByType(ComposeTaskTranscodingTrackerResource.class);
            for (TaskOutputResolutionAndIndexMapper traim :
                    composeTaskTrackerResource.getResource().getTaskOutputResolutionAndIndexMappers()) {
                transcodingTracker.displayMessage(traim.getIndex(), message, traim.getWidth(), traim.getHeight());
            }
        }
    }

    public void displayStyledMessage(int taskId, String font, int size, int color, int alpha, int bgcolor, int bgalpha,
                                     int x, int y, int width, int height, String message) {
        ITranscodingTracker transcodingTracker = getTranscoder().getTranscodingTracker(new TranscodingKey(taskId));
        if (transcodingTracker != null
                && transcodingTracker.getUserData() != null
                && transcodingTracker.getUserData() instanceof TaskResourceHolder) {
            TaskResourceHolder resourceHolder = (TaskResourceHolder) transcodingTracker.getUserData();
            TranscodingTrackerResource<ComposeTaskTranscodingTrackerResource> composeTaskTrackerResource =
                    resourceHolder.getByType(ComposeTaskTranscodingTrackerResource.class);
            for (TaskOutputResolutionAndIndexMapper traim :
                    composeTaskTrackerResource.getResource().getTaskOutputResolutionAndIndexMappers()) {
                transcodingTracker.displayStyledMessage(traim.getIndex(), font, size, color, alpha, x, y, width, height, message);
            }
        }
    }

    public void reload(AbstractTaskParams taskParams) throws IOException, TemplateException {
        ComposeTaskParams composeTaskParams = (ComposeTaskParams) taskParams;
        List<String> seifiles = createSEIMessageXmlFile(composeTaskParams);
        String path = null;
        if (IsNeedContentDetect(composeTaskParams.getTranscoderTemplate())) {
            path = createContentDetectFile(composeTaskParams);
        }

        //     Map<String, Object> model = assemblyModel(composeTaskParams, path,seifiles);

        ITranscodingTracker tracker = getITranscodingTracker(composeTaskParams.getId());
        if (tracker != null) {
            Map<String, Object> model = assemblyModelUpdate(getTranscodingTrackerResourceOfComposeTask(tracker).getResource().getTemplateModel(), composeTaskParams, path, seifiles);
            tracker.reload(getTranscoderXmlBaseOnTracker(tracker, composeTaskParams.getTranscoderTemplate()));
        }
    }

    public Integer getPid(int taskId) {
        ITranscodingTracker transcodingTracker = getTranscoder().getTranscodingTracker(new TranscodingKey(taskId));
        if (transcodingTracker != null) {
            return transcodingTracker.getPid();
        } else {
            return -1;
        }
    }
}
