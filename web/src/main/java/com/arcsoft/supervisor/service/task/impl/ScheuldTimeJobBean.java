package com.arcsoft.supervisor.service.task.impl;

import com.arcsoft.supervisor.cluster.action.task.ScreenWarningBorderRequest;
import com.arcsoft.supervisor.model.domain.channel.Channel;
import com.arcsoft.supervisor.model.domain.log.ContentDetectLog;
import com.arcsoft.supervisor.repository.graphic.ScreenPositionJPARepo;
import com.arcsoft.supervisor.repository.log.ContentDetectLogRepository;
import com.arcsoft.supervisor.service.remote.RemoteExecutorService;
import com.arcsoft.supervisor.service.server.ServerService;
import com.arcsoft.supervisor.service.task.TaskService;

import java.util.List;

/**
 * Created by wwj on 2017/5/10.
 */
public class ScheuldTimeJobBean   {
    private String endTime;
    private ContentDetectLog detectLog;
    private String channelName;
    private ContentDetectLogRepository contentDetectLogRepository;
    private RemoteExecutorService remoteExecutorService;
    private TaskService taskService;
    private ServerService serverService;
    private ScreenPositionJPARepo screenPositionJPARepo;
    private Channel channel;
    private List<ScreenWarningBorderRequest> requestList;
    private boolean alarmFlag;  //true 不告警时间段内   flase   不告警时间之外

    public ScheuldTimeJobBean(boolean alarmFlag,String endTime, ContentDetectLog detectLog, String channelName, ContentDetectLogRepository contentDetectLogRepository, RemoteExecutorService remoteExecutorService, TaskService taskService, ServerService serverService, ScreenPositionJPARepo screenPositionJPARepo, Channel channel,List<ScreenWarningBorderRequest> requestList) {
        this.alarmFlag=alarmFlag;
        this.endTime = endTime;
        this.detectLog = detectLog;
        this.channelName = channelName;
        this.contentDetectLogRepository = contentDetectLogRepository;
        this.remoteExecutorService = remoteExecutorService;
        this.taskService = taskService;
        this.serverService = serverService;
        this.screenPositionJPARepo = screenPositionJPARepo;
        this.channel = channel;
        this.requestList=requestList;
    }

    public boolean isAlarmFlag() {
        return alarmFlag;
    }

    public void setAlarmFlag(boolean alarmFlag) {
        this.alarmFlag = alarmFlag;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public ContentDetectLog getDetectLog() {
        return detectLog;
    }

    public void setDetectLog(ContentDetectLog detectLog) {
        this.detectLog = detectLog;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public ContentDetectLogRepository getContentDetectLogRepository() {
        return contentDetectLogRepository;
    }

    public void setContentDetectLogRepository(ContentDetectLogRepository contentDetectLogRepository) {
        this.contentDetectLogRepository = contentDetectLogRepository;
    }

    public RemoteExecutorService getRemoteExecutorService() {
        return remoteExecutorService;
    }

    public void setRemoteExecutorService(RemoteExecutorService remoteExecutorService) {
        this.remoteExecutorService = remoteExecutorService;
    }

    public TaskService getTaskService() {
        return taskService;
    }

    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    public ServerService getServerService() {
        return serverService;
    }

    public void setServerService(ServerService serverService) {
        this.serverService = serverService;
    }

    public ScreenPositionJPARepo getScreenPositionJPARepo() {
        return screenPositionJPARepo;
    }

    public void setScreenPositionJPARepo(ScreenPositionJPARepo screenPositionJPARepo) {
        this.screenPositionJPARepo = screenPositionJPARepo;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public List<ScreenWarningBorderRequest> getRequestList() {
        return requestList;
    }

    public void setRequestList(List<ScreenWarningBorderRequest> requestList) {
        this.requestList = requestList;
    }
}
