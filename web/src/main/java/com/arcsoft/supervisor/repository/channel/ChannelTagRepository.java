package com.arcsoft.supervisor.repository.channel;

import com.arcsoft.supervisor.model.domain.channel.ChannelTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChannelTagRepository extends JpaRepository<ChannelTag, Integer> {
    @Query("SELECT ct FROM ChannelTag ct WHERE ct.channels IS NOT EMPTY")
    public List<ChannelTag> findChannelsNotEmpty();
    public ChannelTag findFirstByName(String name);
}
