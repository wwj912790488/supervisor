package com.arcsoft.supervisor.agent.service.task.processor;

import com.arcsoft.supervisor.agent.service.task.StartTaskException;
import com.arcsoft.supervisor.agent.service.task.resource.ComposeTaskTranscodingTrackerResource;
import com.arcsoft.supervisor.agent.service.task.support.AbstractIpComposeStreamTaskProcessorSupport;
import com.arcsoft.supervisor.model.vo.task.AbstractTaskParams;
import com.arcsoft.supervisor.model.vo.task.compose.ComposeTaskParams;
import com.arcsoft.supervisor.transcoder.ITranscodingTracker;
import com.arcsoft.supervisor.utils.app.Environment;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * A processor to process the compose ip stream task.
 *
 * @author zw.
 */
public class IPStreamComposeTaskTaskProcessorTaskProcessorSupport extends AbstractIpComposeStreamTaskProcessorSupport {

    @Override
    public int start(ComposeTaskParams task) throws StartTaskException {
    	configSdpResource(task);
        ITranscodingTracker tracker = createTrackerAndSetUserData(task);
        int result = getTranscoder().startTask(tracker);
        if (result != 0) {
            throw new StartTaskException(String.format("Failed to start task [id=%s], return code=%d", task.getId(), result));
        } else if (task.isEnableRtsp() && Environment.getProfiler().isEnableWowza()){
            try {
                String streamFilePath = getRtspStreamFileResourceManager().composeAndWriteUrl(
                        task.getRtspStoragePath(),
                        task.getRtspFileName(),
                        composeUdpUrl(task.getRtspHostIp(), task.getMobileOutputPort())
                );
                getRtspStreamFileResourceManager().setStreamFilePathToItranscodingTracker(tracker, streamFilePath);
            } catch (IOException e) {
                LOG.error("Failed to write stream file of task [id={}]", task.getId());
            }
        }
        return task.getScreenOutputPort();
    }

	private void configSdpResource(ComposeTaskParams task)
			throws StartTaskException {
		Map<String, byte[]> sdpFiles = task.getSdpFiles();
    	for(Map.Entry<String, byte[]> entry : sdpFiles.entrySet()) {
    		File sdpFile = new File(getTranscoderWorkDir() + entry.getKey());
        	File parentFile = sdpFile.getParentFile();
        	if(!parentFile.exists() && !parentFile.mkdirs()) {
        		throw new StartTaskException();
        	}
        	try {
				Files.write(sdpFile.toPath(), entry.getValue());
			} catch (IOException e) {
				throw new StartTaskException(e);
			}
    	}
	}

    @Override
    protected ComposeTaskTranscodingTrackerResource getTranscodingTrackerResourceOfComposeTask(TranscoderXmlAndTemplateModel model, AbstractTaskParams taskParams) {
        ComposeTaskParams task = (ComposeTaskParams) taskParams;
        // Create the ComposeTaskTranscodingTrackerResource for holds the udp port, screen position
        // configuration of task and model for generate transcoder xml.
        // The model and screen position configuration of task will used for reload
        // task in the future.
        return new ComposeTaskTranscodingTrackerResource(
                task.getScreenOutputPort(),
                task.getResolutionAndIndexMappers(),
                model.getModel()
        );
    }
}
