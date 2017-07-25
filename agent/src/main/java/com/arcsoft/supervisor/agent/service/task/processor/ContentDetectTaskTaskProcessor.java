package com.arcsoft.supervisor.agent.service.task.processor;

import com.arcsoft.supervisor.agent.service.task.support.AbstractTaskProcessorSupport;
import com.arcsoft.supervisor.cd.MediaCheckerApp;
import com.arcsoft.supervisor.cd.data.StartTaskInfo;
import com.arcsoft.supervisor.model.vo.task.AbstractTaskParams;
import com.arcsoft.supervisor.model.vo.task.cd.ContentDetectTaskParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A processor implementation for content detect task.
 *
 * @author zw.
 */
public class ContentDetectTaskTaskProcessor extends AbstractTaskProcessorSupport {

    private static final Logger LOG = LoggerFactory.getLogger(ContentDetectTaskTaskProcessor.class);

	private MediaCheckerApp mediaCheckerApp = null;

	public void setMediaCheckerApp(MediaCheckerApp mediaCheckerApp) {
		this.mediaCheckerApp = mediaCheckerApp;
	}

	@Override
	public void start(AbstractTaskParams task) {
        LOG.info("Receive start task: " + task);
		ContentDetectTaskParams contentDetectTask = (ContentDetectTaskParams) task;
		StartTaskInfo info = new StartTaskInfo();
		info.setAudio_pid(contentDetectTask.getAudio_pid());
		info.setCheckType(contentDetectTask.getCheckType());
        info.setCheckTypeParam(contentDetectTask.getCheckTypeParam());
		info.setIndex(contentDetectTask.getIndex());
		info.setProgram_id(contentDetectTask.getProgram_id());
		info.setSubtitle_pid(contentDetectTask.getSubtitle_pid());
		info.setTaskid(contentDetectTask.getId());
		info.setUrl(contentDetectTask.getUrl());
		info.setVideo_pid(contentDetectTask.getVideo_pid());
        this.mediaCheckerApp.startTask(info);
	}

	@Override
	public void stop(int taskId) {
		this.mediaCheckerApp.stopTask(taskId);
	}

    @Override
    public boolean isRunning(int taskId) {
        return mediaCheckerApp.isTaskRun(taskId);
    }
}
