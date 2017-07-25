package com.arcsoft.supervisor.sartf.service.task.impl;

import com.arcsoft.supervisor.cluster.action.task.SwitchAudioByChannelRequest;
import com.arcsoft.supervisor.commons.profile.Sartf;
import com.arcsoft.supervisor.model.domain.server.Server;
import com.arcsoft.supervisor.model.domain.task.Task;
import com.arcsoft.supervisor.model.domain.user.SartfUser;
import com.arcsoft.supervisor.model.vo.task.TaskStatus;
import com.arcsoft.supervisor.model.vo.task.TaskType;
import com.arcsoft.supervisor.repository.graphic.ScreenRepository;
import com.arcsoft.supervisor.repository.profile.TaskProfileRepository;
import com.arcsoft.supervisor.repository.server.ServerJpaRepository;
import com.arcsoft.supervisor.repository.task.TaskRepository;
import com.arcsoft.supervisor.sartf.repository.user.SartfUserRepository;
import com.arcsoft.supervisor.sartf.service.task.SartfTaskService;
import com.arcsoft.supervisor.service.task.TaskExecutor;
import com.arcsoft.supervisor.service.task.TaskPortAssigner;
import com.arcsoft.supervisor.service.task.gpu.GpuLoadBalanceManager;
import com.arcsoft.supervisor.service.task.impl.AbstractTaskServiceSupport;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.StringReader;
import java.util.List;


@Service
@Sartf
public class SartfTaskServiceImpl extends AbstractTaskServiceSupport implements SartfTaskService {

    private final SartfUserRepository sartfUserRepository;

    @Autowired
    public SartfTaskServiceImpl(
            TaskRepository repository,
            ServerJpaRepository serverRepository,
            ScreenRepository screenRepository,
            TaskPortAssigner portAssigner,
            TaskProfileRepository taskProfileRepository,
            SartfUserRepository sartfUserRepository,
            GpuLoadBalanceManager gpuLoadBalanceManager) {
        super(repository, serverRepository, screenRepository, portAssigner, taskProfileRepository);
        this.sartfUserRepository = sartfUserRepository;
    }

    @Override
    public void updateTaskStatus(int taskId, TaskStatus fromStatus, TaskStatus toStatus) {
        Task task = getRepository().findOne(taskId);
        if(task != null && task.isStatusEqual(fromStatus)) {
            task.setStatus(toStatus.toString());
        }
    }

    @Override
    public boolean isUserRelatedTaskHasOutput(int userId) {
        logger.info("test isUserRelatedTaskHasOutput userId" + userId);
        Task task = getByTypeAndReferenceId(userId, TaskType.USER_RELATED_COMPOSE);
        return isTaskHasOutput(task);
    }

    private boolean isTaskHasOutput(Task task) {
        if (task == null) {
            logger.info("task is null");
            return false;
        }
        String progressXml = getProgress(task.getId());
        logger.info("getProcessXml String " + progressXml);
        if (StringUtils.isBlank(progressXml)) {
            return false;
        }
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(new StringReader(progressXml));
            Element rootElement = document.getRootElement();
            Node node = rootElement.selectSingleNode("//input");
            logger.info("task [id=" + task.getId() + "] output " + node.getStringValue());
            return node != null
                    && StringUtils.isNotBlank(node.getStringValue())
                    && Long.parseLong(node.getStringValue()) > 101; //The udp input start time is 101
        } catch (DocumentException e) {
            logger.error("Failed to checks task [id=" + task.getId() + "] output");
        }
        return false;
    }

    @Override
    public void switchAudioByChannel(int composeTaskId, int videoSettingId, int channelId) {
        Task task = getById(composeTaskId);
        if (task != null && task.isStatusEqual(TaskStatus.RUNNING)) {
            Server server = getServerRepository().getServer(task.getServerId());
            if (server != null) {
                SwitchAudioByChannelRequest request = new SwitchAudioByChannelRequest();
                request.setTaskId(composeTaskId);
                request.setVideoSettingId(videoSettingId);
                request.setChannelId(channelId);

                try {
                    remoteExecutorService.remoteExecute(request, server);
                } catch (Exception e) {
                    logger.error("Failed to switch audio to channel [id=" +channelId +"] on task [id=" + composeTaskId + "]");
                }
            }
        }
    }


    @Override
    protected void doAfterOfDoClearAfterTaskIsStop(Task task) {
        if(task.getReferenceId() != null && task.getTypeAsEnum() == TaskType.USER_RELATED_COMPOSE){
            SartfUser user = sartfUserRepository.findOne(task.getReferenceId());
            if(user != null) {
                user.setCurrent(null);
            }
        }
    }

    @Override
    protected List<Task> getRunningTasks(String serverId) {
        return getRepository().findByTypeInAndServerIdAndStatus(
                Lists.newArrayList(
                        TaskType.IP_STREAM_COMPOSE.getType(),
                        TaskType.SDI_STREAM_COMPOSE.getType(),
                        TaskType.USER_RELATED_COMPOSE.getType()
                ),
                serverId,
                TaskStatus.RUNNING.toString()
        );
    }

    @Override
    public List<String> getRunningTasksChannelNameOnServer(String serverId) {
        return null;
    }

    @Override
    public void resetTaskStatusByRefid(Integer screenId){}
}
