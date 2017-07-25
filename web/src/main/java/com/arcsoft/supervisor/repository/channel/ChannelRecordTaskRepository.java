package com.arcsoft.supervisor.repository.channel;

import com.arcsoft.supervisor.model.domain.channel.ChannelRecordTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChannelRecordTaskRepository extends JpaRepository<ChannelRecordTask, Integer> {
    public List<ChannelRecordTask> findBySupervisorChannelId(Integer channelId);
}
