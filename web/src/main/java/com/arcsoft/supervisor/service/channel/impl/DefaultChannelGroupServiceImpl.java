package com.arcsoft.supervisor.service.channel.impl;

import com.arcsoft.supervisor.exception.ObjectAlreadyExistsException;
import com.arcsoft.supervisor.model.domain.channel.ChannelGroup;
import com.arcsoft.supervisor.repository.channel.ChannelGroupRepository;
import com.arcsoft.supervisor.service.TransactionSupport;
import com.arcsoft.supervisor.service.channel.ChannelGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * A {@link ChannelGroupService} implementation.
 *
 * @author zw.
 */
@Service
public class DefaultChannelGroupServiceImpl implements ChannelGroupService, TransactionSupport {

    @Autowired
    private ChannelGroupRepository channelGroupRepository;

    @Override
    public void save(ChannelGroup group) {
    	if(isGroupNameExists(group.getName())) {
    		throw new ObjectAlreadyExistsException(group.getName());
    	}
        channelGroupRepository.save(group);
    }
    
    private boolean isGroupNameExists(String name) {
    	Long c = channelGroupRepository.countByName(name);
    	return c != null && c.longValue() > 0;
    }

    @Override
    public void delete(int groupId) {
        try{
            channelGroupRepository.delete(groupId);
        }catch (EmptyResultDataAccessException e){

        }
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<ChannelGroup> listAll() {
        Iterable<ChannelGroup> ite = channelGroupRepository.findAll();
        return ite == null ? Collections.emptyList() : (List)ite;
    }

    @Override
    public void updateGroupName(int groupId, String name) {
        ChannelGroup group = channelGroupRepository.findOne(groupId);
        if (group != null){
            group.setName(name);
        }
    }

    @Override
    public ChannelGroup getById(int groupId) {
        return channelGroupRepository.findOne(groupId);
    }

    @Override
    public ChannelGroup getByName(String groupName) {
        return channelGroupRepository.findByName(groupName);
    }
}
