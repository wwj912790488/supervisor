package com.arcsoft.supervisor.service.channel;

public interface ChannelRecordService {
    public boolean exist();
    public void startRecord(Integer channelId);
    public void stopRecord(Integer channelId);
    public void startRegularFileDeleter();
}
