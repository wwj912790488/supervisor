package com.arcsoft.supervisor.service.task.processor;

import com.arcsoft.supervisor.cluster.action.task.StartResponse;
import com.arcsoft.supervisor.model.domain.graphic.Screen;
import com.arcsoft.supervisor.model.domain.server.Server;
import com.arcsoft.supervisor.model.domain.task.Task;

import org.springframework.stereotype.Service;

/**
 * Processor for <tt>IP-Stream</tt> task.
 *
 * @author zw.
 */
@Service("ipComposeStreamTaskProcessor")
public class IPComposeStreamTaskProcessor extends AbstractComposeTaskProcessorSupport {

    @Override
    protected StartResponse start(Task task, Server server) {
        Screen screen = screenRepository.findOne(task.getReferenceId());
        ComposeTaskAndResponse composeTaskAndResponse = startComposeTask(task, server, screen);

        if(composeTaskAndResponse.getTaskParams().isScreenWithRTMP()) {
        	        	screen.setAddress(composeRTMPUrl(composeTaskAndResponse.getTaskParams().getRtspHostIp(),
        			composeTaskAndResponse.getTaskParams().getRtmpOPSFileName()));
        }
        else {
        	        	screen.setAddress(
                    composeUrl(composeTaskAndResponse.getTaskParams().getTargetIp(),
                    		composeTaskAndResponse.getResponse().getPort()));
        }
        
        screen.setRtspFileName(composeTaskAndResponse.getTaskParams().getRtspFileName());
        return composeTaskAndResponse.getResponse();
    }

    private String composeUrl(String targetIp, int port) {
        return String.format("udp://%s:%d", targetIp, port);
    }
    
    private static final String RTMP_STREAM_URL_PREFIX = "rtmp://";
    private static final String RTMP_STREAM_URL_SUFFIX = ".stream";
    private static final String RTMP_STREAM_URL_PORT = "1935";


    private String composeRTMPUrl(String serverIp, String fileName) {
        return RTMP_STREAM_URL_PREFIX + serverIp + ":" + RTMP_STREAM_URL_PORT + "/live/" + fileName + RTMP_STREAM_URL_SUFFIX;
    }

}
