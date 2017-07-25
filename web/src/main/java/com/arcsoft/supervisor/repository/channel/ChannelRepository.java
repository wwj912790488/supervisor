package com.arcsoft.supervisor.repository.channel;

import com.arcsoft.supervisor.model.domain.channel.Channel;
import com.arcsoft.supervisor.model.domain.channel.ChannelGroup;
import com.arcsoft.supervisor.model.domain.channel.ChannelTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

/**
 * The repository interface for <tt>Channel</tt>
 *
 * @author zw.
 */
public interface ChannelRepository extends JpaRepository<Channel, Integer>, JpaChannelRepository, JpaSpecificationExecutor {

    @Query("select c from Channel c where c.group is null")
    public List<Channel> findByGroupNull();

    public List<Channel> findByGroup(ChannelGroup group);

    public List<Channel> findByGroupIn(List<ChannelGroup> groups);


    public Long countByIpAndPortAndProgramIdAndAudioId(String ip, Integer port, String programId, String audioId);

    public Channel findByIpAndPortAndProgramIdAndAudioId(String ip, Integer port, String programId, String audioId);

    public Long countByName(String name);

    public Long countByOrigchannelid(String origchannelid);

    public Channel findByOrigchannelid(String origchannelid);

    /**
     * Finds all of support mobile terminal of channel with {@code pageable}.
     *
     * @param pageable the pageable object to
     * @return all of support mobile terminal channels
     */
    public Page<Channel> findByIsSupportMobileTrue(Pageable pageable);

    public Page<Channel> findByGroup(ChannelGroup group, Pageable pageable);

    public List<Channel> findByIdIn(Collection<Integer> channelIds);

    @Query("select c from Channel c where :tag member of c.tags")
    public Page<Channel> findHasChannelTag(@Param("tag") ChannelTag tag, Pageable pageable);

}
