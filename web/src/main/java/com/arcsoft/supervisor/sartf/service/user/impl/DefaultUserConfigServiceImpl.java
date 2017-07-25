package com.arcsoft.supervisor.sartf.service.user.impl;

import com.arcsoft.supervisor.commons.profile.Sartf;
import com.arcsoft.supervisor.exception.service.BusinessExceptionDescription;
import com.arcsoft.supervisor.model.domain.channel.Channel;
import com.arcsoft.supervisor.model.domain.layouttemplate.LayoutTemplate;
import com.arcsoft.supervisor.model.domain.layouttemplate.LayoutTemplateCell;
import com.arcsoft.supervisor.model.domain.user.SartfUser;
import com.arcsoft.supervisor.model.domain.userconfig.UserConfig;
import com.arcsoft.supervisor.model.domain.userconfig.UserConfigChannel;
import com.arcsoft.supervisor.model.dto.rest.userconfig.UserConfigBean;
import com.arcsoft.supervisor.model.dto.rest.userconfig.UserConfigChannelBean;
import com.arcsoft.supervisor.repository.channel.ChannelRepository;
import com.arcsoft.supervisor.sartf.repository.layouttemplate.LayoutTemplateCellRepository;
import com.arcsoft.supervisor.sartf.repository.layouttemplate.LayoutTemplateRepository;
import com.arcsoft.supervisor.sartf.repository.user.UserConfigRepository;
import com.arcsoft.supervisor.sartf.service.user.UserConfigService;
import com.arcsoft.supervisor.service.TransactionSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
@Sartf
public class DefaultUserConfigServiceImpl implements UserConfigService, TransactionSupport {
	
	private final UserConfigRepository userconfigRepository;
	
	private final LayoutTemplateRepository layoutTemplateRepository;
	
	private final ChannelRepository channelRepository;
	
	private final LayoutTemplateCellRepository layoutTemplateCellRespository;

	@Autowired
	public DefaultUserConfigServiceImpl(
			UserConfigRepository userconfigRepository,
			LayoutTemplateRepository layoutTemplateRepository,
			ChannelRepository channelRepository,
			LayoutTemplateCellRepository layoutTemplateCellRespository) {
		this.userconfigRepository = userconfigRepository;
		this.layoutTemplateRepository = layoutTemplateRepository;
		this.channelRepository = channelRepository;
		this.layoutTemplateCellRespository = layoutTemplateCellRespository;
	}

	public UserConfigRepository getUserconfigRepository() {
		return userconfigRepository;
	}

	public LayoutTemplateRepository getLayoutTemplateRepository() {
		return layoutTemplateRepository;
	}

	public ChannelRepository getChannelRepository() {
		return channelRepository;
	}

	public LayoutTemplateCellRepository getLayoutTemplateCellRespository() {
		return layoutTemplateCellRespository;
	}

	@Override
	public UserConfig findById(Integer id) {
		return userconfigRepository.findOne(id);
	}

	@Override
	public void updateUserConfig(UserConfig userConfig, UserConfigBean bean) {
		
		Integer templateId = bean.getTemplate_id();
		LayoutTemplate template = layoutTemplateRepository.getOne(templateId);
		if(template == null) {
			throw BusinessExceptionDescription.INVALID_ARGUMENTS.exception();
		}
		List<UserConfigChannel> channels = new ArrayList<>();
		for(UserConfigChannelBean channelBean : bean.getChannels()) {
			Channel channel = channelRepository.getOne(channelBean.getId());
			LayoutTemplateCell cell = layoutTemplateCellRespository.findByTemplateAndCellIndex(template, channelBean.getCell_index());
			if(channel == null || cell == null) {
				throw BusinessExceptionDescription.INVALID_ARGUMENTS.exception();
			}
			UserConfigChannel userconfigchannel = new UserConfigChannel();
			userconfigchannel.setUserconfig(userConfig);
			userconfigchannel.setChannel(channel);
			userconfigchannel.setCell(cell);
			channels.add(userconfigchannel);
		}
		boolean modified = checkModified(userConfig, template, channels);
		if(modified) {
			userConfig.setTemplate(template);
			List<UserConfigChannel> userchannels = userConfig.getChannels();
			userchannels.clear();
			userchannels.addAll(channels);
			userConfig.setAudioChannel(null);
			userConfig.setCell(null);
			userConfig.setLastUpdate(new Date());
		}
	}
	
	private boolean checkModified(UserConfig userConfig, LayoutTemplate template, List<UserConfigChannel> channels) {
		if(userConfig.getTemplate() == null) {
			return true;
		}
		if(userConfig.getTemplate().getId() != template.getId()) {
			return true;
		}
		if(userConfig.getTemplate().getLastUpdate().before(template.getLastUpdate())) {
			return true;
		}
		if(userConfig.getChannels().size() != channels.size()) {
			return true;
		}
		HashMap<Integer, Integer> oldChannelCellMap = new HashMap<>();
		HashMap<Integer, Integer> newChannelCellMap = new HashMap<>();
		for(UserConfigChannel oldchannel : userConfig.getChannels()) {
			oldChannelCellMap.put(oldchannel.getCellIndex(), oldchannel.getChannelId());
		}
		for(UserConfigChannel newchannel : channels) {
			newChannelCellMap.put(newchannel.getCellIndex(), newchannel.getChannelId());
		}
		for(Integer cellindex : newChannelCellMap.keySet()) {
			Integer oldchannelId = oldChannelCellMap.get(cellindex);
			Integer newchannelId = newChannelCellMap.get(cellindex);
			if(oldchannelId == null || newchannelId == null || oldchannelId != newchannelId) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void saveUserConfig(SartfUser user, UserConfigBean bean) {
		UserConfig userConfig = new UserConfig();
		Integer templateId = bean.getTemplate_id();
		LayoutTemplate template = layoutTemplateRepository.getOne(templateId);
		if(template == null) {
			throw BusinessExceptionDescription.INVALID_ARGUMENTS.exception();
		}
		List<UserConfigChannel> channels = new ArrayList<>();
		for(UserConfigChannelBean channelBean : bean.getChannels()) {
			Channel channel = channelRepository.getOne(channelBean.getId());
			LayoutTemplateCell cell = layoutTemplateCellRespository.findByTemplateAndCellIndex(template, channelBean.getCell_index());
			if(channel == null || cell == null) {
				throw BusinessExceptionDescription.INVALID_ARGUMENTS.exception();
			}
			UserConfigChannel userconfigchannel = new UserConfigChannel();
			userconfigchannel.setUserconfig(userConfig);
			userconfigchannel.setChannel(channel);
			userconfigchannel.setCell(cell);
			channels.add(userconfigchannel);
		}
		userConfig.setUser(user);
		userConfig.setTemplate(template);
		userConfig.setChannels(channels);
		userConfig.setLastUpdate(new Date());
		
		userconfigRepository.save(userConfig);
	}
	
	@Override
	public void updateUserConfigAudioChannel(Integer config_id, Integer channel_id) {
		if(config_id == null || channel_id == null) {
			throw BusinessExceptionDescription.INVALID_ARGUMENTS.exception();
		}
		UserConfig userconfig = findById(config_id);
		if(userconfig == null) {
			throw BusinessExceptionDescription.INVALID_ARGUMENTS.exception();
		}		
		if(channel_id == 0) {
			userconfig.setAudioChannel(null);
		} else {
			Channel channel = channelRepository.getOne(channel_id);
			if(channel == null) {
				throw BusinessExceptionDescription.INVALID_ARGUMENTS.exception();
			}
			
			List<UserConfigChannel> channels = userconfig.getChannels();
			ArrayList<Integer> userchannelIds = new ArrayList<>();
			for(UserConfigChannel userchannel : channels) {
				userchannelIds.add(userchannel.getChannelId());
			}
			if(userchannelIds.contains(channel.getId())) {
				userconfig.setAudioChannel(channel);
			} else {
				throw BusinessExceptionDescription.INVALID_ARGUMENTS.exception();
			}
		}		
	}

	@Override
	public void updateUserConfigAudioCellIndex(Integer config_id, Integer cell_index) {
		if(config_id == null || cell_index == null) {
			throw BusinessExceptionDescription.INVALID_ARGUMENTS.exception();
		}
		UserConfig userconfig = findById(config_id);
		if(userconfig == null) {
			throw BusinessExceptionDescription.INVALID_ARGUMENTS.exception();
		}		
		if(cell_index == -1) {
			userconfig.setCell(null);
		} else {
			LayoutTemplate template = userconfig.getTemplate();
			LayoutTemplateCell cell = layoutTemplateCellRespository.findByTemplateAndCellIndex(template, cell_index);
			if(cell == null) {
				throw BusinessExceptionDescription.INVALID_ARGUMENTS.exception();
			}
			
			List<UserConfigChannel> channels = userconfig.getChannels();
			ArrayList<Integer> cellindexIds = new ArrayList<>();
			for(UserConfigChannel userchannel : channels) {
				cellindexIds.add(userchannel.getCell().getId());
			}
			if(cellindexIds.contains(cell.getId())) {
				userconfig.setCell(cell);
			} else {
				throw BusinessExceptionDescription.INVALID_ARGUMENTS.exception();
			}
		}		
		
	}

}
