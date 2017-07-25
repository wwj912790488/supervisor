package com.arcsoft.supervisor.service.alarm.impl;


import com.arcsoft.supervisor.exception.service.BusinessExceptionDescription;
import com.arcsoft.supervisor.model.domain.alarm.AlarmConfigChannel;
import com.arcsoft.supervisor.model.domain.alarm.AlarmConfig;
import com.arcsoft.supervisor.model.domain.channel.Channel;
import com.arcsoft.supervisor.model.domain.user.User;
import com.arcsoft.supervisor.model.dto.rest.alarmconfig.AlarmConfigChannelBean;
import com.arcsoft.supervisor.model.dto.rest.alarmconfig.AlarmConfigBean;
import com.arcsoft.supervisor.repository.alarm.AlarmConfigRepository;
import com.arcsoft.supervisor.repository.channel.ChannelRepository;
import com.arcsoft.supervisor.service.ServiceSupport;
import com.arcsoft.supervisor.service.TransactionSupport;
import com.arcsoft.supervisor.service.alarm.AlarmConfigService;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author jt.
 */

@Service
public class DefaultAlarmConfigService extends ServiceSupport implements AlarmConfigService, TransactionSupport {

    @Autowired
    private AlarmConfigRepository alarmConfigRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Override
    public AlarmConfig findById(Integer id) {
        return alarmConfigRepository.findOne(id);
    }

    @Override
    public void updateAlarmConfig(AlarmConfig alarmConfig, AlarmConfigBean bean) {
        //  channel list
        List<AlarmConfigChannel> channels = new ArrayList<>();
        for(AlarmConfigChannelBean channelBean : bean.getChannels()) {
            Channel channel = channelRepository.getOne(channelBean.getId());
            if(channel == null) {
                throw BusinessExceptionDescription.INVALID_ARGUMENTS.exception();
            }
            AlarmConfigChannel alarmConfigChannel = new AlarmConfigChannel();
            alarmConfigChannel.setAlarmConfig(alarmConfig);
            alarmConfigChannel.setChannel(channel);
            channels.add(alarmConfigChannel);
        }
        boolean modified = checkModified(alarmConfig, bean, channels);
        if(modified) {
            List<AlarmConfigChannel> alarmChannels = alarmConfig.getChannels();
            alarmChannels.clear();
            alarmChannels.addAll(channels);
            //alarmChannels.setLastUpdate(new Date());

            //  Sync content detect config
            alarmConfig.setEnablecontentdetect(bean.getEnablecontentdetect());
            alarmConfig.setEnableBlack(bean.getEnableBlack());
            alarmConfig.setEnableSilence(bean.getEnableSilence());
            alarmConfig.setEnableNoFrame(bean.getEnableNoFrame());
            alarmConfig.setEnableBoomSonic(bean.getEnableBoomSonic());
            alarmConfig.setEnableGreen(bean.getEnableGreen());
            alarmConfig.setEnableLowVolume(bean.getEnableLowVolume());
            alarmConfig.setEnableLoudVolume(bean.getEnableLoudVolume());
            //  Sync Signal detect config
            alarmConfig.setEnablesignaldetect(bean.getEnablesignaldetect());
            alarmConfig.setEnableBroken(bean.getEnableBroken());
            alarmConfig.setEnableProgidLoss(bean.getEnableProgidLoss());
            alarmConfig.setEnableVideoLoss(bean.getEnableVideoLoss());
            alarmConfig.setEnableAudioLoss(bean.getEnableAudioLoss());
            alarmConfig.setEnableCcError(bean.getEnableCcError());
        }
    }

    private boolean checkModified(AlarmConfig alarmConfig, AlarmConfigBean bean, List<AlarmConfigChannel> channels) {

        return true;

//        if(alarmConfig.getChannels().size() != channels.size()) {
//            return true;
//        }
//        HashMap<Integer, Integer> oldChannelCellMap = new HashMap<Integer, Integer>();
//        HashMap<Integer, Integer> newChannelCellMap = new HashMap<Integer, Integer>();
//        for(AlarmConfigChannel oldchannel : alarmConfig.getChannels()) {
//            oldChannelCellMap.put(oldchannel.getCellIndex(), oldchannel.getChannelId());
//        }
//        for(AlarmConfigChannel newchannel : channels) {
//            newChannelCellMap.put(newchannel.getCellIndex(), newchannel.getChannelId());
//        }
//        for(Integer cellindex : newChannelCellMap.keySet()) {
//            Integer oldchannelId = oldChannelCellMap.get(cellindex);
//            Integer newchannelId = newChannelCellMap.get(cellindex);
//            if(oldchannelId == null || newchannelId == null || oldchannelId != newchannelId) {
//                return true;
//            }
//        }
//        return false;
    }

    @Override
    public void saveAlarmConfig(User user, AlarmConfigBean bean) {
        AlarmConfig alarmConfig = new AlarmConfig();
        //  channel list
        List<AlarmConfigChannel> channels = new ArrayList<>();
        for(AlarmConfigChannelBean channelBean : bean.getChannels()) {
            Channel channel = channelRepository.getOne(channelBean.getId());
            if(channel == null) {
                throw BusinessExceptionDescription.INVALID_ARGUMENTS.exception();
            }
            AlarmConfigChannel alarmConfigChannel = new AlarmConfigChannel();
            alarmConfigChannel.setAlarmConfig(alarmConfig);
            alarmConfigChannel.setChannel(channel);
            channels.add(alarmConfigChannel);
        }
        alarmConfig.setUser(user);
        alarmConfig.setChannels(channels);
        alarmConfig.setLastUpdate(new Date());

        //  Sync content detect config
        alarmConfig.setEnablecontentdetect(bean.getEnablecontentdetect());
        alarmConfig.setEnableBlack(bean.getEnableBlack());
        alarmConfig.setEnableSilence(bean.getEnableSilence());
        alarmConfig.setEnableNoFrame(bean.getEnableNoFrame());
        alarmConfig.setEnableBoomSonic(bean.getEnableBoomSonic());
        alarmConfig.setEnableGreen(bean.getEnableGreen());
        alarmConfig.setEnableLowVolume(bean.getEnableLowVolume());
        alarmConfig.setEnableLoudVolume(bean.getEnableLoudVolume());
        //  Sync Signal detect config
        alarmConfig.setEnablesignaldetect(bean.getEnablesignaldetect());
        alarmConfig.setEnableBroken(bean.getEnableBroken());
        alarmConfig.setEnableProgidLoss(bean.getEnableProgidLoss());
        alarmConfig.setEnableVideoLoss(bean.getEnableVideoLoss());
        alarmConfig.setEnableAudioLoss(bean.getEnableAudioLoss());
        alarmConfig.setEnableCcError(bean.getEnableCcError());

        alarmConfigRepository.save(alarmConfig);
    }
}
