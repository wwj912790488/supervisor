package com.arcsoft.supervisor.service.converter.impl;

import java.util.ArrayList;
import java.util.List;

import com.arcsoft.supervisor.model.domain.alarm.AlarmConfig;
import com.arcsoft.supervisor.model.domain.alarm.AlarmConfigChannel;
import com.arcsoft.supervisor.model.dto.rest.alarmconfig.AlarmConfigChannelBean;
import com.arcsoft.supervisor.model.dto.rest.alarmconfig.CommonAlarmConfigBean;
import org.springframework.stereotype.Service;


import com.arcsoft.supervisor.service.converter.ConverterAdapter;

@Service("AlarmConfigAndCommonAlarmConfigBeanConverter")
public class AlarmConfigAndCommonAlarmConfigBeanConverter extends
		ConverterAdapter<AlarmConfig, CommonAlarmConfigBean> {

	@Override
	public CommonAlarmConfigBean doForward(AlarmConfig source) throws Exception {
		CommonAlarmConfigBean bean = new CommonAlarmConfigBean();
		bean.setEnablecontentdetect(source.getEnablecontentdetect());
		bean.setEnableBlack(source.getEnableBlack());
		bean.setEnableSilence(source.getEnableSilence());
		bean.setEnableNoFrame(source.getEnableNoFrame());
		bean.setEnableBoomSonic(source.getEnableBoomSonic());
		bean.setEnableGreen(source.getEnableGreen());
		bean.setEnableLowVolume(source.getEnableLowVolume());
		bean.setEnableLoudVolume(source.getEnableLoudVolume());
		bean.setEnablesignaldetect(source.getEnablesignaldetect());
		bean.setEnableBroken(source.getEnableBroken());
		bean.setEnableProgidLoss(source.getEnableProgidLoss());
		bean.setEnableVideoLoss(source.getEnableVideoLoss());
		bean.setEnableAudioLoss(source.getEnableAudioLoss());
		bean.setEnableCcError(source.getEnableCcError());
		List<AlarmConfigChannelBean> channels = new ArrayList<AlarmConfigChannelBean>();
		for(AlarmConfigChannel channel : source.getChannels()) {
			AlarmConfigChannelBean channelbean = new AlarmConfigChannelBean();
			channelbean.setId(channel.getChannel().getId());
			channels.add(channelbean);
		}
		bean.setChannels(channels);
		return bean;
	}

}
