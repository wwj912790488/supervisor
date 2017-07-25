package com.arcsoft.supervisor.agent.service.task.processor;

import com.arcsoft.supervisor.agent.service.task.StartTaskException;
import com.arcsoft.supervisor.agent.service.task.TranscoderXmlUtils;
import com.arcsoft.supervisor.agent.service.task.resource.ComposeTaskTranscodingTrackerResource;
import com.arcsoft.supervisor.agent.service.task.support.AbstractComposeStreamTaskProcessor;
import com.arcsoft.supervisor.model.vo.task.AbstractTaskParams;
import com.arcsoft.supervisor.model.vo.task.usercompose.UserComposeCellConfig;
import com.arcsoft.supervisor.model.vo.task.usercompose.UserComposeTaskParams;
import com.arcsoft.supervisor.transcoder.ITranscodingTracker;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserRelatedIPStreamComposeTaskTaskProcessor extends AbstractComposeStreamTaskProcessor {

	public static final int opsWidth = 1920, opsHeight = 1080, rtspWidth = 1280, rtspHeight = 720;

    private static final String TRANSCODER_TEMPLATE_NAME = "usertasktranscoder.tpl";



    @Override
	public void start(AbstractTaskParams task) throws StartTaskException {
		UserComposeTaskParams userTask = (UserComposeTaskParams) task;
		if(userTask.getCellConfigs() == null) {			
			userTask.setCellConfigs(new ArrayList<UserComposeCellConfig>());
		}
		if(userTask.getCellConfigs().size() < 13) {
        	for(int i = userTask.getCellConfigs().size(); i < 13; i++) {
        		UserComposeCellConfig faked = new UserComposeCellConfig();
        		faked.setxPos(0);
        		faked.setyPos(0);
        		faked.setWidth(0);
        		faked.setHeight(0);
        		faked.setIndex(i);
        		faked.setUrl("udp://172.0.0.2:9999");
        		faked.setProgramId("0");
        		faked.setAudioId("-1");
        		userTask.getCellConfigs().add(faked);
        	}
        }
        ITranscodingTracker tracker = createTrackerAndSetUserData(task);
        int result = getTranscoder().startTask(tracker);
        if (result != 0) {
            throw new StartTaskException(String.format("Failed to start task [id=%s], return code=%d", task.getId(), result));
        } else {           
                try {
                    String rtspStreamFilePath = getRtspStreamFileResourceManager().composeAndWriteUrl(
                            userTask.getRtspStoragePath(),
                            userTask.getRtspMobileFileName(),
                            composeUdpUrl(userTask.getRtspHostIp(), userTask.getMobileOutputPort())
                    );
                    String opsStreamFilePath = getRtspStreamFileResourceManager().composeAndWriteUrl(
                            userTask.getRtspStoragePath(),
                            userTask.getRtspOpsFileName(),
                            composeUdpUrl(userTask.getRtspHostIp(), userTask.getScreenOutputPort())
                    );
                    getRtspStreamFileResourceManager().setStreamFilePathToItranscodingTracker(tracker, rtspStreamFilePath, opsStreamFilePath);
                } catch (IOException e) {
                    LOG.error("Failed to write stream file of task [id={}]", task.getId());
                }
        }

	}

    @Override
    protected ComposeTaskTranscodingTrackerResource getTranscodingTrackerResourceOfComposeTask(TranscoderXmlAndTemplateModel model, AbstractTaskParams taskParams) {
        UserComposeTaskParams userTask = (UserComposeTaskParams) taskParams;
        //Create the ComposeTaskTranscodingTrackerResource for holds the udp port during task lifecycle
        return new ComposeTaskTranscodingTrackerResource(
                userTask.getScreenOutputPort(),
                model.getModel()
        );
    }

    @Override
    public TranscoderXmlAndTemplateModel createTranscoderModel(AbstractTaskParams taskParams) throws IOException, TemplateException {
        UserComposeTaskParams task = (UserComposeTaskParams) taskParams;
        Map<String, Object> model = assemblyModel(task);
        String xmlContent = TranscoderXmlUtils.generateTranscoderXml(TRANSCODER_TEMPLATE_NAME, model);
        return new TranscoderXmlAndTemplateModel(xmlContent, model);
    }

    /**
     * Reload transcoder task base on a running task.
     *
     * @param taskParams the parameters of task
     * @throws IOException
     * @throws TemplateException
     */
    @Override
    public void reload(AbstractTaskParams taskParams) throws IOException, TemplateException {
    	UserComposeTaskParams userTaskParams = (UserComposeTaskParams) taskParams;
    	if(userTaskParams.getCellConfigs() == null) {
    		userTaskParams.setCellConfigs(new ArrayList<UserComposeCellConfig>());
    	}
    	if(userTaskParams.getCellConfigs().size() < 13) {
        	for(int i = userTaskParams.getCellConfigs().size(); i < 13; i++) {
        		UserComposeCellConfig faked = new UserComposeCellConfig();
        		faked.setxPos(0);
        		faked.setyPos(0);
        		faked.setWidth(0);
        		faked.setHeight(0);
        		faked.setIndex(i);
        		faked.setUrl("udp://172.0.0.2:9999");
        		faked.setProgramId("0");
        		faked.setAudioId("-1");
        		userTaskParams.getCellConfigs().add(faked);
        	}
        }
    	int index = -1;
    	for(UserComposeCellConfig cellConfig: userTaskParams.getCellConfigs()) {
    		if(!StringUtils.equals(cellConfig.getUrl(), "udp://172.0.0.2:9999")) {
    			index = cellConfig.getIndex();
    		}
    	}
        ITranscodingTracker tracker = getITranscodingTracker(taskParams.getId());
        if (tracker != null) {
        	if(index != -1) {
        		tracker.switchAudio(0, index);
        		tracker.switchAudio(1, index);
        	}
            tracker.reload(getTranscoderXmlBaseOnTracker(tracker, TRANSCODER_TEMPLATE_NAME, userTaskParams));
            if(index != -1) {
            	tracker.switchAudio(0, 99);
            }
        }
    }

    @Override
    public void displayMessage(int taskId, int taskType, String message) {
        //Do nothing because this task hasn't functionality
    }



    @Override
    public void warnBorder(int taskId, int index, boolean isShow) {
        //Do nothing because this task hasn't functionality
    }

    @Override
    public void displayStyledMessage(int taskId, String font, int size, int color, int alpha, int bgcolor, int bgalpha, int x, int y, int width, int height, String message) {

    }

    /**
     * Retrieves the xml of transcoder using {@link ComposeTaskTranscodingTrackerResource#templateModel}.
     *
     * @param tracker tracker contains template model
     * @param templateName the name of transcoder template
     * @return completed xml from {@link ComposeTaskTranscodingTrackerResource#templateModel}.
     * @throws IOException
     * @throws TemplateException
     */
    private String getTranscoderXmlBaseOnTracker(ITranscodingTracker tracker, String templateName,
                                                 UserComposeTaskParams taskParams) throws IOException, TemplateException {
        Map<String, Object> model = getTranscodingTrackerResourceOfComposeTask(tracker).getResource().getTemplateModel();
        model.put("task", taskParams);
        return TranscoderXmlUtils.generateTranscoderXml(
                templateName,
                model
        );
    }

	protected Map<String, Object> assemblyModel(UserComposeTaskParams task) {
        Map<String, Object> model = new HashMap<>();
        model.put("task", task);
        model.put("opsWidth", opsWidth);
        model.put("opsHeight", opsHeight);
        model.put("rtspWidth", rtspWidth);
        model.put("rtspHeight", rtspHeight);
        model.put("rtspServerIp", task.getRtspHostIp());
        model.put("opsUdpPort", task.getScreenOutputPort());
        model.put("rtspUdpPort", task.getMobileOutputPort());
        return model;
    }

}
