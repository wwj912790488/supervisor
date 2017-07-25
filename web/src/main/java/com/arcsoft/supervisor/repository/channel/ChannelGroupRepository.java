package com.arcsoft.supervisor.repository.channel;

import com.arcsoft.supervisor.model.domain.channel.ChannelGroup;
import org.springframework.data.repository.CrudRepository;

/**
 * The Repository interface for <tt>ChannelGroup</tt>.
 *
 * @author zw.
 */
public interface ChannelGroupRepository extends CrudRepository<ChannelGroup, Integer> {

    public ChannelGroup findByName(String name);
    public long countByName(String name);
}
