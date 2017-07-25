package com.arcsoft.supervisor.service.task.processor;

import com.arcsoft.supervisor.cluster.action.BaseResponse;
import com.arcsoft.supervisor.cluster.action.task.StartRequest;
import com.arcsoft.supervisor.cluster.action.task.StartResponse;
import com.arcsoft.supervisor.exception.service.BusinessException;
import com.arcsoft.supervisor.exception.service.BusinessExceptionDescription;
import com.arcsoft.supervisor.model.domain.channel.Channel;
import com.arcsoft.supervisor.model.domain.channel.ChannelMobileConfig;
import com.arcsoft.supervisor.model.domain.channel.ChannelRecordHistory;
import com.arcsoft.supervisor.model.domain.server.Server;
import com.arcsoft.supervisor.model.domain.settings.Storage;
import com.arcsoft.supervisor.model.domain.task.Task;
import com.arcsoft.supervisor.model.domain.task.TaskPort;
import com.arcsoft.supervisor.model.vo.task.TaskType;
import com.arcsoft.supervisor.model.vo.task.rtsp.MobileConfig;
import com.arcsoft.supervisor.model.vo.task.rtsp.RTSPTaskParams;
import com.arcsoft.supervisor.service.settings.StorageSelector;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ServletContextAware;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletContext;
/**
 * @author zw.
 */
@Service("rtspTaskProcessor")
public class RtspTaskProcessor extends AbstractChannelTaskProcessorSupport implements ServletContextAware {

    private static final String RECORD_BASE_PATH_PREFIX = "/mnt/data/remote/";

    @Autowired
    @Qualifier(value = "randomStorageSelector")
    private StorageSelector storageSelector;
    
    private ServletContext servletContext;

    @Override
    protected BaseResponse start(Channel channel, Server server, int taskId) {
        RTSPTaskParams rtspTaskParams = constructBaseParams(channel, taskId);
        List<MobileConfig> mobileConfigs = new ArrayList<>();
        boolean hasMobile = false;
        if (channel.getIsSupportMobile() && channel.getMobileConfigs() != null && !channel.getMobileConfigs().isEmpty()) {
            mobileConfigs = convertFrom(channel.getMobileConfigs(), channel.getId(), taskRepository.findOne(taskId));
            rtspTaskParams.setConfigs(mobileConfigs);
            setCommonRtspParams(rtspTaskParams);
            hasMobile = true;
            rtspTaskParams.setProtocol(channel.getProtocol());
            rtspTaskParams.setPort(channel.getPort());
        }

        StartRequest request = new StartRequest();
        request.setTask(rtspTaskParams);
        StartResponse response = (StartResponse) execute(request, server);
        if (response.isSuccess()) {
            if (hasMobile) {
                setAddressOfMobileConfig(channel, mobileConfigs);
            }
            /*if (channel.getEnableRecord()) {
                channel.setRecordBasePath(rtspTaskParams.getRecordPath());
                channel.addRecordHistory(new ChannelRecordHistory(channel, rtspTaskParams.getRecordPath()));
            }*/
        }
        return response;
    }

    /**
     * Sets the address of each <code>mobileConfigs</code>.
     * <p>The address value is decision by <code>type of mobileConfigs</code>'s item.</p>
     *
     * @param channel       the instance of channel
     * @param mobileConfigs the mobile config items
     */
    private void setAddressOfMobileConfig(Channel channel, List<MobileConfig> mobileConfigs) {
        for (MobileConfig config : mobileConfigs) {
            ChannelMobileConfig mobileConfig = channel.getChannelMobileConfigByType(config.getType());
            if (mobileConfig == null) {
                continue;
            }
            mobileConfig.setAddress(config.getFileName());
        }
    }

    /**
     * Returns the <tt>record base path</tt> of file by given <code>channel</code>.
     *
     * @param channel the instance of channel
     * @return the record base path after replace all blank
     * @throws BusinessException thrown with {@link BusinessExceptionDescription#TASK_STORAGE_NOT_EXIST} if can't found
     *                           any available storage
     */
    private String getRandomRecordBasePath(Channel channel) {
        Storage recordStorage = storageSelector.select();
        if (recordStorage == null) {
            throw BusinessExceptionDescription.TASK_STORAGE_NOT_EXIST.exception();
        }
        return RECORD_BASE_PATH_PREFIX
                + (recordStorage.getName().endsWith("/") ? recordStorage.getName() : (recordStorage.getName() + "/"))
                + channel.getEncodeName() + "/"
                + channel.getProgramId() + "-" + channel.getAudioId() + "/"
                + (UUID.randomUUID().toString().replaceAll("-", ""));
    }

    /**
     * Construct a new <code>RTSPTaskParams</code> with givens <code>channel, taskId and recordBasePath</code>
     *
     * @param channel the instance of channel will start
     * @param taskId  the identify value of channel task
     * @return a <code>RTSPTaskParams</code> construct from <code>channel, taskId and recordBasePath</code>
     */
    private RTSPTaskParams constructBaseParams(Channel channel, int taskId) {
        RTSPTaskParams rtspTaskParams = new RTSPTaskParams();
        rtspTaskParams.setTaskType(TaskType.RTSP);
        rtspTaskParams.setId(taskId);
        rtspTaskParams.setAudioId(channel.getAudioId());
        rtspTaskParams.setProgramId(channel.getProgramId());
        rtspTaskParams.setVideocodec(channel.getChannelInfo().getVcodec());
        rtspTaskParams.setAudiocodec(channel.getChannelInfo().getAcodec());
        rtspTaskParams.setPort(channel.getPort());
        rtspTaskParams.setProtocol(channel.getProtocol());
        String address = channel.getAddress();
        rtspTaskParams.setUdpUrl(address);
        if(address.startsWith("sdp")) {
        	String localPath = servletContext.getRealPath("/WEB-INF/" + address);
        	File file = new File(localPath);
        	if(file.exists()) {
        		try {
					rtspTaskParams.setSdpFile(Files.readAllBytes(file.toPath()));
				} catch (IOException e) {
				}
        	}
        }
        rtspTaskParams.setEnableRecord(false);
        //if (channel.getEnableRecord()) {
        //    rtspTaskParams.setRecordFileName(channel.getTranscoderRecordFileName());
        //    rtspTaskParams.setRecordPath(getRandomRecordBasePath(channel));
        //    rtspTaskParams.setRecordFormat(channel.getRecordFormat());
        //}
        return rtspTaskParams;
    }

    /**
     * Converts <code>List<ChannelMobileConfig></code> to <code>List<MobileConfig></code>.
     * <p>It will doForward <code>ChannelMobileConfig</code> to <code>MobileConfig</code> and
     * set the filename as a md5 encode string.</p>
     *
     * @param mobileConfigs the mobile config items
     * @param channelId     the identify value of channel
     * @return the <code>MobileConfig</code> items
     */
    private List<MobileConfig> convertFrom(List<ChannelMobileConfig> mobileConfigs, int channelId, Task task) {
        List<MobileConfig> configs = new ArrayList<>();
        for (ChannelMobileConfig mobileConfig : mobileConfigs) {
            MobileConfig c = new MobileConfig();
            BeanUtils.copyProperties(mobileConfig, c, "id", "channel", "address");
            c.setFileName(DigestUtils.md5Hex(String.valueOf(channelId) + "-" + (mobileConfig.getType() == 0 ? "sd" : "hd")));
            c.setPortNumber(
                    getTaskPortWithPortType(
                            task,
                            mobileConfig.getType() == 0 ? TaskPort.PortType.SD : TaskPort.PortType.HD
                    ).getPortNumber()
            );
            configs.add(c);
        }
        return configs;
    }

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

}
