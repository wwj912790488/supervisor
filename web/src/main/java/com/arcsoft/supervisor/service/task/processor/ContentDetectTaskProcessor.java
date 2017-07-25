package com.arcsoft.supervisor.service.task.processor;

import com.arcsoft.supervisor.cluster.action.BaseResponse;
import com.arcsoft.supervisor.cluster.action.task.StartRequest;
import com.arcsoft.supervisor.cluster.action.task.StartResponse;
import com.arcsoft.supervisor.model.domain.channel.Channel;
import com.arcsoft.supervisor.model.domain.channel.ChannelContentDetectConfig;
import com.arcsoft.supervisor.model.domain.server.Server;
import com.arcsoft.supervisor.model.vo.task.MediaCheckType;
import com.arcsoft.supervisor.model.vo.task.TaskType;
import com.arcsoft.supervisor.model.vo.task.cd.ContentDetectTaskParams;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * @author zw.
 */
@Service("contentDetectTaskProcessor")
@Deprecated
public class ContentDetectTaskProcessor extends AbstractChannelTaskProcessorSupport {

    private static final byte DEFAULT_BOOM_SONIC_SECONDS = 10;

    @Override
    protected BaseResponse start(Channel channel, Server server, int taskId) {
        BaseResponse response = new StartResponse();
        if (channel.getEnableContentDetect() && channel.getContentDetectConfig() != null) {
            ContentDetectTaskParams taskParams = convertFrom(channel.getContentDetectConfig(), taskId);
            if (!taskParams.getCheckType().isEmpty()){
                taskParams.setProgram_id(Integer.valueOf(channel.getProgramId()));
                taskParams.setAudio_pid(Integer.valueOf(channel.getAudioId()));
                taskParams.setUrl(channel.getAddress());
                StartRequest request = new StartRequest();
                request.setTask(taskParams);
                response = execute(request, server);
            }
        }
        return response;
    }

    private ContentDetectTaskParams convertFrom(ChannelContentDetectConfig contentDetectConfig, int taskId) {
        ContentDetectTaskParams detectTaskParams = new ContentDetectTaskParams();
        detectTaskParams.setTaskType(TaskType.CONTENT_DETECT);
        detectTaskParams.setId(taskId);
        ArrayList<Integer> detectType = new ArrayList<>();
        ArrayList<Integer> detectTypeParam = new ArrayList<>();
        if (contentDetectConfig.getEnableBoomSonic()) {
            detectType.add(MediaCheckType.CHECK_TYPE_BREAK_INDEX);
            detectTypeParam.add((int) DEFAULT_BOOM_SONIC_SECONDS);
        }
        if (contentDetectConfig.getBlackSeconds() != null && contentDetectConfig.getBlackSeconds() > 0) {
            detectType.add(MediaCheckType.CHECK_TYPE_BLACK_FIELD_INDEX);
            detectTypeParam.add((int) (contentDetectConfig.getBlackSeconds() * 1000));
        }
        if (contentDetectConfig.getGreenSeconds() != null && contentDetectConfig.getGreenSeconds() > 0) {
            detectType.add(MediaCheckType.CHECK_TYPE_GREEN_FIELD_INDEX);
            detectTypeParam.add((int) (contentDetectConfig.getGreenSeconds() * 1000));
        }
        if (contentDetectConfig.getNoFrameSeconds() != null && contentDetectConfig.getNoFrameSeconds() > 0) {
            detectType.add(MediaCheckType.CHECK_TYPE_STATIC_FRAME_INDEX);
            detectTypeParam.add((int) (contentDetectConfig.getNoFrameSeconds() * 1000));
        }
        if (contentDetectConfig.getSilenceSeconds() != null && contentDetectConfig.getSilenceSeconds() > 0) {
            detectType.add(MediaCheckType.CHECK_TYPE_MUTE_THRESHOLD_INDEX);
            detectTypeParam.add((int) (contentDetectConfig.getSilenceSeconds() * 1000));
        }
//        //TODO: just for test, remove in package
//        detectType.add(MediaCheckType.CHECK_TYPE_VOLUME_LOUD_INDEX);
//        detectTypeParam.add(1000);
//
//        detectType.add(MediaCheckType.CHECK_TYPE_WHITE_INDEX);
//        detectTypeParam.add(10);

        detectTaskParams.setCheckType(detectType);
        detectTaskParams.setCheckTypeParam(detectTypeParam);
        return detectTaskParams;
    }
}
