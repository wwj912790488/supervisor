package com.arcsoft.supervisor.service.converter.impl;

import com.arcsoft.supervisor.commons.spring.SpringUtils;
import com.arcsoft.supervisor.exception.service.BusinessException;
import com.arcsoft.supervisor.exception.service.BusinessExceptionDescription;
import com.arcsoft.supervisor.model.domain.channel.Channel;
import com.arcsoft.supervisor.model.domain.channel.ChannelMobileConfig;
import com.arcsoft.supervisor.model.domain.graphic.Screen;
import com.arcsoft.supervisor.model.domain.graphic.ScreenPosition;
import com.arcsoft.supervisor.model.domain.graphic.ScreenSchema;
import com.arcsoft.supervisor.model.domain.task.Task;
import com.arcsoft.supervisor.model.dto.rest.screen.PositionUrlBean;
import com.arcsoft.supervisor.model.dto.rest.screen.ScreenPreviewBean;
import com.arcsoft.supervisor.service.converter.ConverterAdapter;
import com.arcsoft.supervisor.service.graphic.ScreenService;
import com.arcsoft.supervisor.service.settings.RtspConfigurationService;
import com.arcsoft.supervisor.service.task.TaskService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zw.
 */
@Service("screenToScreenPreviewBeanConverter")
public class ScreenAndScreenPreviewBeanConverter extends ConverterAdapter<Integer, ScreenPreviewBean> {

    @Autowired
    private ScreenService screenService;

    @Autowired
    private RtspConfigurationService rtspConfigurationService;

    @Autowired
    private TaskService taskService;

    @Override
    public ScreenPreviewBean doForward(Integer source) {

        Screen screen = screenService.getById(source);
        ScreenPreviewBean screenPreviewBean = new ScreenPreviewBean();
        screenPreviewBean.setCode(BusinessExceptionDescription.OK.getCode());
        String clientIp = SpringUtils.getThreadBoundedHttpServletRequest().getRemoteAddr();
        if (screen != null) {
            ScreenSchema screenSchema = screen.getActiveSchema();
            if (screenSchema != null) {

                Task screenTask = null;
                try {
                    screenTask = taskService.getScreenTask(source);
                }
                catch (Exception e){
                    screenTask = null;
                }

                if(screenTask== null || screenTask.isStopped()) {
                    screenPreviewBean.setCode(BusinessExceptionDescription.TASK_NOT_RUNNING.getCode());
                }
                else{
                    String url = getScreenRtspUrl(screen,clientIp);
                    if(url!=null){
                        screenPreviewBean.setUrl(url);
                    }
                    else{
                        screenPreviewBean.setCode(BusinessExceptionDescription.SCREEN_NOTCONFIG_MOBILE.getCode());
                    }
                }

                screenPreviewBean.setColCount(screenSchema.getColumnCount());
                screenPreviewBean.setRowCount(screenSchema.getRowCount());
                List<ScreenPosition> positions = screenSchema.getScreenPositions();
                if (positions != null) {
                    List<PositionUrlBean> positionUrlBeans = new ArrayList<>();
                    for (ScreenPosition position : positions) {
                        Channel channel = null;
                        try{
                            channel = position.getChannel();
                        }catch (Exception e){
                            channel = null;
                        }
                        if(channel!=null){
                            positionUrlBeans.add(new PositionUrlBean(channel.getId(),position.getRow(),
                                    position.getColumn(),
                                    channel == null ? "" : getRtspUrl(channel, clientIp))
                            );
                        }
                        else {
                            positionUrlBeans.add(new PositionUrlBean(-1,position.getRow(),
                                    position.getColumn(),
                                    ""));
                        }
                    }
                    screenPreviewBean.setPositionUrlBeans(positionUrlBeans);
                }

            }else {
                screenPreviewBean.setCode(BusinessExceptionDescription.SCREEN_NOT_EXISTS.getCode());
            }
        }
        else{
            screenPreviewBean.setCode(BusinessExceptionDescription.SCREEN_NOT_EXISTS.getCode());
        }

        return screenPreviewBean;
    }

    private String getRtspUrl(Channel channel, String clientIp) {
        if (channel.getIsSupportMobile() && channel.getMobileConfigs() != null && !channel.getMobileConfigs().isEmpty()) {
            for (ChannelMobileConfig config : channel.getMobileConfigs()) {
                if (StringUtils.isNotBlank(config.getAddress())) {
                    if (config.getType() == 1) { //just get hd url
                        channel.setHdUrl(rtspConfigurationService.composeUrl(config.getAddress(), clientIp));
                    }
                }
            }
        }
        return StringUtils.isNotBlank(channel.getHdUrl()) ? channel.getHdUrl() : "";
    }

    private String getScreenRtspUrl(Screen screen, String clientIp){

        String output = screen.getWallPosition().getOutput();
        if(!StringUtils.isEmpty(output) && output.startsWith("rtmp")){
            return output;
        }

        String url = null;
        boolean bMobileSupport = false;
        String rtsp = screen.getRtspFileName();
        if(rtsp!=null){
            url = rtspConfigurationService.composeUrl(rtsp, SpringUtils.getThreadBoundedHttpServletRequest().getRemoteAddr());
            bMobileSupport = true;
        }
        else {
            bMobileSupport = false;
        }
        if(!bMobileSupport){
            String opsurl = screen.getAddress();
            if(opsurl!=null && (opsurl.startsWith("rtmp://")||opsurl.startsWith("rtsp://"))){
                url = opsurl;
            }
        }
        return url;
    }
}

