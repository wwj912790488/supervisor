package com.arcsoft.supervisor.service.channel.impl;

import com.arcsoft.supervisor.exception.ObjectAlreadyExistsException;
import com.arcsoft.supervisor.exception.OriginalChannelIdlareadyExistException;
import com.arcsoft.supervisor.exception.ResolveMediainfoException;
import com.arcsoft.supervisor.exception.server.NameExistsException;
import com.arcsoft.supervisor.model.domain.channel.*;
import com.arcsoft.supervisor.repository.channel.ChannelGroupRepository;
import com.arcsoft.supervisor.repository.channel.ChannelRepository;
import com.arcsoft.supervisor.repository.channel.ChannelTagRepository;
import com.arcsoft.supervisor.service.ServiceSupport;
import com.arcsoft.supervisor.service.TransactionSupport;
import com.arcsoft.supervisor.service.channel.ChannelQueryParams;
import com.arcsoft.supervisor.service.channel.ChannelService;
import com.arcsoft.supervisor.service.commons.mediainfo.MediainfoService;
import com.arcsoft.supervisor.service.commons.mediainfo.impl.ProgramAndAudioMediainfo;
import com.arcsoft.supervisor.utils.app.Environment;
import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.mysema.query.BooleanBuilder;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zw.
 */
@Service
public class DefaultChannelServiceImpl extends ServiceSupport implements ChannelService, TransactionSupport {


    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private ChannelGroupRepository groupReposity;

    @Autowired
    private ChannelTagRepository channelTagRepository;

    @Autowired
    @Qualifier("defaultMediainfoService")
    private MediainfoService mediainfoService;

    @Override
    public void save(Channel channel) {
        boolean enableduplicate = isEnableDuplicate();
        if (isChannelNameExists(channel.getId(), channel.getName())) {
            throw new NameExistsException(channel.getName());
        } else if (isChannelExists(channel.getId(), channel.getIp(), channel.getPort(), channel.getProgramId(), channel.getAudioId()) && !enableduplicate) {
            throw new ObjectAlreadyExistsException(channel);
        } else if (isChannelOriginalidExists(channel.getId(), channel.getOrigchannelid()) && !enableduplicate) {
            throw new OriginalChannelIdlareadyExistException();
        } else {
            List<ChannelTag> tags = channel.getTags();
            if (tags != null) {
                List<ChannelTag> removeTags = new ArrayList<ChannelTag>();
                List<ChannelTag> addTags = new ArrayList<>();
                for (ChannelTag tag : tags) {
                    if (tag.getId() == null) {
                        try {
                            ChannelTag existTag = channelTagRepository.findFirstByName(tag.getName());
                            if (existTag != null) {
                                removeTags.add(tag);
                                List<Channel>  channels = existTag.getChannels();
                                if(channels!=null){
                                    channels.add(channel);
                                    existTag.setChannels(channels);
                                    addTags.add(existTag);
                                }else {
                                    channels=new ArrayList<>();
                                    channels.add(channel);
                                    existTag.setChannels(channels);
                                    addTags.add(existTag);
                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                }
                tags.removeAll(removeTags);
                tags.addAll(addTags);
            }

            channelRepository.save(channel);

        }
    }

    @Override
    public void save(Channel channel, ChannelInfo info) {
        if (channel.getId() != null) {
            Channel persistChannel = getById(channel.getId());
            if (persistChannel != null) {
                ChannelInfo persistChannelInfo = persistChannel.getChannelInfo();
                if (persistChannelInfo != null) {
                    BeanUtils.copyProperties(info, persistChannelInfo, "id");
                    channel.setChannelInfo(persistChannelInfo);
                } else {
                    channel.setChannelInfo(info);
                }
            } else {
                channel.setChannelInfo(info);
            }
        } else {
            channel.setChannelInfo(info);
        }
        save(channel);

    }

    @Override
    public void saveAddress(Channel channel, String ip) {
        channel.setIp(ip);
        save(channel);
    }

    @Override
    public Page<Channel> paginate(ChannelQueryParams q, PageRequest pageRequest) {
        return channelRepository.findAll(queryChannel(q), pageRequest);
    }

    public Specification<Channel> queryChannel(final ChannelQueryParams params) {
        return new Specification<Channel>() {
            @Override
            public Predicate toPredicate(Root<Channel> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<javax.persistence.criteria.Predicate> predicates = new ArrayList<Predicate>();
//                if(params.getTags() != null && !params.getTags().isEmpty()) {
//                    List<Predicate> tagPredicates = new ArrayList<>();
//                    for(Integer tag : params.getTags()) {
//                        ChannelTag ct = new ChannelTag();
//                        ct.setId(tag);
//                        tagPredicates.add(cb.isMember(ct, root.<List<ChannelTag>>get("tags")));
//                    }
//                    predicates.add(cb.or(tagPredicates.toArray(new Predicate[]{})));
//                }
                if (params.getGroups() != null && !params.getGroups().isEmpty()) {
                    List<ChannelGroup> groups = FluentIterable.from(params.getGroups()).transform(new Function<Integer, ChannelGroup>() {

                        @Nullable
                        @Override
                        public ChannelGroup apply(Integer input) {
                            if (input != -1) {
                                ChannelGroup group = new ChannelGroup();
                                group.setId(input);
                                return group;
                            }
                            return null;
                        }
                    }).filter(Predicates.notNull()).toList();
                    List<Predicate> groupPredicates = new ArrayList<>();
                    if (groups.size() > 0) {
                        groupPredicates.add(root.<ChannelGroup>get("group").in(groups));
                    }
                    if (params.getGroups().contains(-1)) {
                        groupPredicates.add(root.<ChannelGroup>get("group").isNull());
                    }
                    predicates.add(cb.or(groupPredicates.toArray(new Predicate[]{})));
                }
                if (StringUtils.isNotBlank(params.getChannelName())) {
                    predicates.add(cb.like(root.<String>get("name"), "%" + params.getChannelName() + "%"));
                }
                return cb.and(predicates.toArray(new Predicate[]{}));
            }
        };
    }

    public boolean isChannelNameExists(Integer id, String name) {
        Long c = channelRepository.countByName(name);
        if (id == null) {
            return c != null && c > 0l;
        } else {
            Channel channel = channelRepository.findOne(id);
            if (channel != null && channel.getName().equals(name)) {
                return c != null && c > 1l;
            } else {
                return c != null && c > 0l;
            }
        }
    }

    public boolean isChannelExists(Integer id, String ip, Integer port, String programId, String audioId) {
        Long c = channelRepository.countByIpAndPortAndProgramIdAndAudioId(ip, port, programId, audioId);
        if (id == null) {
            return c != null && c > 0l;
        }
        Channel channel = channelRepository.findOne(id);
        if (channel != null && ip.equals(channel.getIp()) && port == channel.getPort()
                && programId.equals(channel.getProgramId()) && audioId.equals(channel.getAudioId())) {
            return c != null && c > 1l;
        } else {
            return c != null && c > 0l;
        }
    }

    public boolean isChannelOriginalidExists(Integer id, String originalchanid) {
        if (originalchanid == null)
            return false;

        Long c = channelRepository.countByOrigchannelid(originalchanid);
        if (c < 1L)
            return false;

        if (id == null) {
            return c != null && c > 0l;
        }
        Channel channel = channelRepository.findByOrigchannelid(originalchanid);
        if (channel != null && id.equals(channel.getId())) {
            return c != null && c > 1l;
        } else {
            return c != null && c > 0l;
        }
    }

    @Override
    public Channel findByIpAndPortAndProgramIdAndAudioId(String ip, Integer port, String programId, String audioId) {
        return channelRepository.findByIpAndPortAndProgramIdAndAudioId(ip, port, programId, audioId);
    }

    @Override
    public void deleteById(int id) {
        try {
            channelRepository.delete(id);
        } catch (EmptyResultDataAccessException e) {

        }
    }

    @Override
    public void deleteByIds(List<Channel> channels) {
        channelRepository.deleteInBatch(channels);
    }

    @Override
    public void updateGroup(List<Channel> channels, int groupId) {
        ChannelGroup group = groupReposity.findOne(groupId);
        for (Channel channel : channels) {
            Channel channelToUpdate = channelRepository.findOne(channel.getId());
            channelToUpdate.setGroup(group);
            save(channelToUpdate);
        }
    }

    @Override
    public Channel getById(int id) {
        return channelRepository.findOne(id);
    }

    @Override
    public void update() {
        //todo:
    }

    @Override
    public List<Channel> listAll() {
        return channelRepository.findAll();
    }

    @Override
    public List<Channel> getUngrouped() {
        return channelRepository.findByGroupNull();
    }

    @Override
    public List<Channel> getByGroupId(int groupId) {
        ChannelGroup group = new ChannelGroup();
        group.setId(groupId);
        return channelRepository.findByGroup(group);
    }

    @Override
    public Page<Channel> pagenate(int pageNo, int pageSize) {
        return channelRepository.findAll(new PageRequest(pageNo, pageSize));
    }

    @Override
    public Page<Channel> pagenateBySupportMobile(int pageNo, int pageSize) {
        return channelRepository.findByIsSupportMobileTrue(new PageRequest(pageNo, pageSize));
    }

    @Override
    public Page<Channel> pagenateByGroup(int groupId, int pageNo, int pageSize) {
        ChannelGroup group = new ChannelGroup();
        group.setId(groupId);
        return channelRepository.findByGroup(group, new PageRequest(pageNo, pageSize));
    }

    @Override
    public Channel getByIdWithOutLazy(int id) {
        return channelRepository.getChannelWithoutLazy(id);
    }

    @Override
    public Channel getByOrigchannelid(String origchannelid)

    {
        return channelRepository.findByOrigchannelid(origchannelid);
    }


    @Override
    public Channel fromChannelDesc(final NewChannelDesc newChannelDesc) {
        Channel channel = new Channel();
        channel.setName(newChannelDesc.getName());
        channel.setIp(newChannelDesc.getAddress());
        channel.setProgramId(newChannelDesc.getPid());
        channel.setAudioId(newChannelDesc.getAudioId());
        channel.setEnableSignalDetect(true);
        channel.setProtocol(channel.getIp().split(":")[0]);
        if (newChannelDesc.getCd() != null) {
            channel.setContentDetectConfig(newChannelDesc.getCd());
            channel.setEnableContentDetect(true);
        }
        if (newChannelDesc.getSd() != null) {
            channel.setSignalDetectByTypeConfig(newChannelDesc.getSd());
            channel.setEnableSignalDetect(true);
            channel.setEnableSignalDetectByType(true);
        }
        ChannelInfo channelInfo = null;
        try {
            if ("-1".equals(channel.getProgramId()) && "-1".equals(channel.getAudioId())) {
                ProgramAndAudioMediainfo programAndAudioMediainfo = mediainfoService.getProgramAndAudio(channel.getAddress()).get(0);
                channelInfo = mediainfoService.getChannelInfo(
                        channel.getAddress(),
                        String.valueOf(programAndAudioMediainfo.getProgramId()),
                        String.valueOf(programAndAudioMediainfo.getAudios().get(0).getId())
                );
                channel.setProgramId(String.valueOf(programAndAudioMediainfo.getProgramId()));
                channel.setAudioId(String.valueOf(programAndAudioMediainfo.getAudios().get(0).getId()));
            } else {
                channelInfo = mediainfoService.getChannelInfo(
                        channel.getAddress(),
                        channel.getProgramId(),
                        channel.getAudioId()
                );
            }

            channel.setChannelInfo(channelInfo);
        } catch (ResolveMediainfoException e) {
            e.printStackTrace();
        }
        if (isChannelExists(null, channel.getAddress(), null, channel.getProgramId(), channel.getAudioId())) {
            Channel newChannel = findByIpAndPortAndProgramIdAndAudioId(channel.getAddress(), null, channel.getProgramId(), channel.getAudioId());
            if (newChannelDesc.getCd() != null) {
                newChannel.setContentDetectConfig(newChannelDesc.getCd());
                newChannel.setEnableContentDetect(true);
            }
            if (newChannelDesc.getSd() != null) {
                newChannel.setSignalDetectByTypeConfig(newChannelDesc.getSd());
                newChannel.setEnableSignalDetect(true);
                newChannel.setEnableSignalDetectByType(true);
            }
            return newChannel;
        } else {
            try {
                save(channel, channelInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return channel;
    }

    private boolean isEnableDuplicate() {
        try {
            String duplicate = Environment.getProperty("channel.duplicate", "0");
            return duplicate != null ? duplicate.equals("1") : false;

        } catch (Exception e) {
        }
        return false;
    }
}
