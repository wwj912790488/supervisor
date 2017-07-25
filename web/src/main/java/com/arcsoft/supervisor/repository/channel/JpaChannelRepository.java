package com.arcsoft.supervisor.repository.channel;

import com.arcsoft.supervisor.model.domain.channel.Channel;

/**
 *
 * A jpa interface for channel.
 *
 * @author zw.
 */
public interface JpaChannelRepository {

    public Channel getChannelWithoutLazy(int id);

}
