package com.arcsoft.supervisor.agent.service.task.processor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import com.arcsoft.supervisor.agent.config.AppConfig;
import com.arcsoft.supervisor.agent.service.task.StartTaskException;
import com.arcsoft.supervisor.agent.service.task.resource.ComposeTaskTranscodingTrackerResource;
import com.arcsoft.supervisor.agent.service.task.support.AbstractIpComposeStreamTaskProcessorSupport;
import com.arcsoft.supervisor.model.vo.task.AbstractTaskParams;
import com.arcsoft.supervisor.model.vo.task.compose.ComposeTaskParams;
import com.arcsoft.supervisor.transcoder.ITranscodingTracker;

public class SDIStreamComposeTaskTaskProcessor extends AbstractIpComposeStreamTaskProcessorSupport {

    @Override
    public int start(ComposeTaskParams task) throws StartTaskException {
    	ConfigSdpResource(task);
        ITranscodingTracker tracker = createTrackerAndSetUserData(task);
        int result = getTranscoder().startTask(tracker);
        if (result != 0) {
            throw new StartTaskException(String.format("Failed to start task [id=%s], return code=%d", task.getId(), result));
        }
        return -1; //The sdi task doesn't need a port.
    }
    
    private void ConfigSdpResource(ComposeTaskParams task)
			throws StartTaskException {
		Map<String, byte[]> sdpFiles = task.getSdpFiles();
    	for(Map.Entry<String, byte[]> entry : sdpFiles.entrySet()) {
            File sdpFile = new File(AppConfig.getString("transcoder_work_dir","/usr/local/arcsoft/arcvideo/transcoder-supervisor/") + entry.getKey());
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
    protected ComposeTaskTranscodingTrackerResource getTranscodingTrackerResourceOfComposeTask(
            TranscoderXmlAndTemplateModel model, AbstractTaskParams taskParams) {
        ComposeTaskParams composeTaskParams = (ComposeTaskParams) taskParams;
        return new ComposeTaskTranscodingTrackerResource(
                -1,
                composeTaskParams.getResolutionAndIndexMappers(),
                model.getModel()
        );
    }
}
