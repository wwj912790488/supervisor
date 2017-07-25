package com.arcsoft.supervisor.agent.service.task.resource;

import com.arcsoft.supervisor.agent.service.task.TranscodingTrackerResource;
import com.arcsoft.supervisor.model.vo.task.compose.TaskOutputResolutionAndIndexMapper;

import java.util.List;
import java.util.Map;

/**
 * A ip compose stream task resource implementation.
 *
 * @author zw.
 */
public class ComposeTaskTranscodingTrackerResource implements TranscodingTrackerResource<ComposeTaskTranscodingTrackerResource> {

    /**
     * The port number of compose stream output.
     */
    private int udpPort;
    
    private String rtmpOpsFileName;

    /**
     * The items of resolution and output index of task
     */
    private List<TaskOutputResolutionAndIndexMapper> taskOutputResolutionAndIndexMappers;

    private Map<String, Object> templateModel;

    public ComposeTaskTranscodingTrackerResource(int udpPort, Map<String, Object> templateModel) {
        this.udpPort = udpPort;
        this.templateModel = templateModel;
    }

    public ComposeTaskTranscodingTrackerResource(int udpPort,
                                                 List<TaskOutputResolutionAndIndexMapper> taskOutputResolutionAndIndexMappers,
                                                 Map<String, Object> templateModel) {
        this.udpPort = udpPort;
        this.taskOutputResolutionAndIndexMappers = taskOutputResolutionAndIndexMappers;
        this.templateModel = templateModel;
        Object rtmpOpsFileNameObject = templateModel.get("rtmpopsUrl");
        this.rtmpOpsFileName = rtmpOpsFileNameObject == null ? null : (String)rtmpOpsFileNameObject;

    }
   

	public String getRtmpOpsFileName() {
		return rtmpOpsFileName;
	}

	public void setRtmpOpsFileName(String rtmpOpsFileName) {
		this.rtmpOpsFileName = rtmpOpsFileName;
	}

	@Override
    public ComposeTaskTranscodingTrackerResource getResource() {
        return this;
    }

    @Override
    public void setResource(ComposeTaskTranscodingTrackerResource resource) {
        this.udpPort = resource.udpPort;
        this.taskOutputResolutionAndIndexMappers = resource.taskOutputResolutionAndIndexMappers;
    }

    public int getUdpPort() {
        return udpPort;
    }

    public List<TaskOutputResolutionAndIndexMapper> getTaskOutputResolutionAndIndexMappers() {
        return taskOutputResolutionAndIndexMappers;
    }

    public Map<String, Object> getTemplateModel() {
        return templateModel;
    }
}
