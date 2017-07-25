package com.arcsoft.supervisor.sartf.service.task.processor;

import com.arcsoft.supervisor.cluster.action.BaseResponse;
import com.arcsoft.supervisor.cluster.action.task.ReloadRequest;
import com.arcsoft.supervisor.cluster.action.task.StartRequest;
import com.arcsoft.supervisor.commons.profile.Sartf;
import com.arcsoft.supervisor.exception.service.BusinessExceptionDescription;
import com.arcsoft.supervisor.model.domain.channel.Channel;
import com.arcsoft.supervisor.model.domain.layouttemplate.LayoutTemplate;
import com.arcsoft.supervisor.model.domain.layouttemplate.LayoutTemplateCell;
import com.arcsoft.supervisor.model.domain.layouttemplate.LayoutTemplateInfo;
import com.arcsoft.supervisor.model.domain.server.Server;
import com.arcsoft.supervisor.model.domain.server.ServerFunction;
import com.arcsoft.supervisor.model.domain.task.Task;
import com.arcsoft.supervisor.model.domain.task.TaskPort;
import com.arcsoft.supervisor.model.domain.task.UserTaskInfo;
import com.arcsoft.supervisor.model.domain.user.SartfUser;
import com.arcsoft.supervisor.model.domain.userconfig.UserConfig;
import com.arcsoft.supervisor.model.domain.userconfig.UserConfigChannel;
import com.arcsoft.supervisor.model.vo.task.TaskStatus;
import com.arcsoft.supervisor.model.vo.task.TaskType;
import com.arcsoft.supervisor.model.vo.task.usercompose.UserComposeCellConfig;
import com.arcsoft.supervisor.model.vo.task.usercompose.UserComposeTaskParams;
import com.arcsoft.supervisor.sartf.repository.user.SartfUserRepository;
import com.arcsoft.supervisor.service.task.processor.AbstractTaskProcessorSupport;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service("userRelatedIpComposeStreamTaskProcessor")
@Sartf
public class UserRelatedIPComposeStreamTaskProcessor extends AbstractTaskProcessorSupport {

	@Autowired
	private SartfUserRepository userRepository;

	@Override
	protected ServerFunction getFunctionByTaskType(TaskType taskType) {
		return ServerFunction.IP_STREAM_COMPOSE;
	}

	@Override
	protected BaseResponse start(Task task, Server server) {
		 SartfUser user = userRepository.getOne(task.getReferenceId());
		 check(user);
	     List<UserComposeCellConfig> configs = convert(user);
	     UserComposeTaskParams composeTask = createUserComposeTaskParams(task, user, configs);

	     BaseResponse response;
	     if(task.isStatusEqual(TaskStatus.UPDATING)) {
	    	 ReloadRequest request = new ReloadRequest();
	    	 request.setTaskParams(composeTask);
	    	 response = execute(request, server);
	    	 task.setStatus(TaskStatus.RUNNING.toString());
	     } else {
	    	 StartRequest request = new StartRequest();
		     request.setTask(composeTask);
		     response = execute(request, server);
	     }
	     
	     if (!response.isSuccess()){
	         throw BusinessExceptionDescription.ERROR.exception();
	     }

	     UserTaskInfo info = user.getInfo();
	     if(info == null) {
	    	 info = new UserTaskInfo();
	    	 info.setTask(task);
	    	 info.setUser(user);
	    	 info.setRtspOpsFileName(composeTask.getRtspOpsFileName());
	    	 info.setRtspMobileFileName(composeTask.getRtspMobileFileName());
	    	 info.setLastUpdate(new Date());
	    	 user.setInfo(info);
	     } else {
	    	 info.setLastUpdate(new Date());
	     }
	     
	     return response;
	}

	@Override
	protected Server getServer(Task task) {
		if( task.getServerId() == null) {
			return super.getServer(task);
		} else {
			return serverRepository.getServer(task.getServerId());
		}
	}	
	
    protected List<UserComposeCellConfig> convert(SartfUser user) {
    	List<UserComposeCellConfig> configs = new ArrayList<>();
    	UserConfig userconfig = user.getCurrent();
    	if(userconfig == null) {
    		return configs;
    	}
    	LayoutTemplate template = userconfig.getTemplate();
    	List<UserConfigChannel> configchannels = userconfig.getChannels();
    	HashMap<Integer, Channel> channelMap = new HashMap<>();
    	for(UserConfigChannel configchannel : configchannels) {
    		channelMap.put(configchannel.getCellIndex(), configchannel.getChannel());
    	}
    	for(LayoutTemplateCell cell : template.getCells()) {
    		UserComposeCellConfig config = new UserComposeCellConfig();
    		Integer index = cell.getCell_index();
    		Channel channel = channelMap.get(index);
    		config.setIndex(index);
    		config.setxPos(cell.getxPos());
    		config.setyPos(cell.getyPos());
    		config.setWidth(cell.getWidth());
    		config.setHeight(cell.getHeight());
    		config.setUrl(channel == null ? "udp://172.0.0.2:9999" : channel.getAddress());
            config.setChannelName(channel == null ? "" : channel.getName());
            config.setProgramId(channel == null ? "0" : channel.getProgramId());
            config.setAudioId(channel == null ? "-1" : channel.getAudioId());
            configs.add(config);
    	}
    	return configs;
    }
    
    protected UserComposeTaskParams createUserComposeTaskParams(Task task, SartfUser user, List<UserComposeCellConfig> configs) {
    	UserComposeTaskParams params = new UserComposeTaskParams();
    	UserTaskInfo info = user.getInfo();
    	UserConfig userconfig = user.getCurrent();
    	int totalWidth, totalHeight;
    	if(userconfig == null) {
    		totalWidth = 1920;
    		totalHeight = 1080;
    	} else {
    		LayoutTemplate template = userconfig.getTemplate();
    		LayoutTemplateInfo templateInfo = template.getInfo();
    		totalWidth = templateInfo.getTotalWidth();
    		totalHeight = templateInfo.getTotalHeight();
    	}
    	
    	params.setId(task.getId());
    	params.setCellConfigs(configs);
    	params.setTaskType(TaskType.USER_RELATED_COMPOSE);
    	params.setTotalHeight(totalHeight);
    	params.setTotalWidth(totalWidth); 	
    	if(info == null) {
    		params.setRtspOpsFileName(DigestUtils.md5Hex("ops-" + task.getId()));
    		params.setRtspMobileFileName(DigestUtils.md5Hex("mobile-" + task.getId()));
    	} else {
    		params.setRtspOpsFileName(info.getRtspOpsFileName());
    		params.setRtspMobileFileName(info.getRtspMobileFileName());
    	}
		setCommonRtspParams(params);
		params.setScreenOutputPort(getTaskPortWithPortType(task, TaskPort.PortType.SCREEN).getPortNumber());
		params.setMobileOutputPort(getTaskPortWithPortType(task, TaskPort.PortType.MOBILE).getPortNumber());
//    	 TranscoderXmlBuilder.BuilderResourceAndXml builderResourceAndXml = transcoderXmlBuilder.build(
//                 new TranscoderXmlBuilder.BuilderParameters(params, convert(screen), serverId)
//         );
//    	 params.setTranscoderTemplate(builderResourceAndXml.getTranscoderXml());
    	return params;
    }
	
	protected void check(SartfUser user) {
		if( user.getCurrent() != null && user.getOps() == null) {
			 throw BusinessExceptionDescription.TASK_USER_OPS_NOT_BIND.exception();
		}
	}

}
