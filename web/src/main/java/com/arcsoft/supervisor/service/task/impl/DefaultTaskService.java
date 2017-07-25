package com.arcsoft.supervisor.service.task.impl;

import com.arcsoft.supervisor.commons.profile.Production;
import com.arcsoft.supervisor.model.domain.channel.Channel;
import com.arcsoft.supervisor.model.domain.graphic.Screen;
import com.arcsoft.supervisor.model.domain.graphic.ScreenPosition;
import com.arcsoft.supervisor.model.domain.graphic.ScreenSchema;
import com.arcsoft.supervisor.model.domain.task.Task;
import com.arcsoft.supervisor.model.vo.task.TaskStatus;
import com.arcsoft.supervisor.model.vo.task.TaskType;
import com.arcsoft.supervisor.repository.channel.ChannelRepository;
import com.arcsoft.supervisor.repository.graphic.ScreenRepository;
import com.arcsoft.supervisor.repository.profile.TaskProfileRepository;
import com.arcsoft.supervisor.repository.server.ServerJpaRepository;
import com.arcsoft.supervisor.repository.task.TaskRepository;
import com.arcsoft.supervisor.service.task.TaskExecutor;
import com.arcsoft.supervisor.service.task.TaskPortAssigner;
import com.arcsoft.supervisor.service.task.gpu.GpuLoadBalanceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author zw.
 */
@Service
@Production
public class DefaultTaskService extends AbstractTaskServiceSupport {

    private final ChannelRepository channelRepository;

    @Autowired
    public DefaultTaskService(
            TaskRepository repository,
            ServerJpaRepository serverRepository,
            ScreenRepository screenRepository,
            TaskPortAssigner portAssigner,
            TaskProfileRepository taskProfileRepository,
            ChannelRepository channelRepository) {
        super(repository, serverRepository, screenRepository, portAssigner, taskProfileRepository);
        this.channelRepository = channelRepository;
    }

    @Override
    public List<String> getRunningTasksChannelNameOnServer(String serverId) {
        List<Task> runningTasks = getRepository().findByTypeInAndServerIdAndStatus(
                Arrays.asList(
                        TaskType.IP_STREAM_COMPOSE.getType(),
                        TaskType.SDI_STREAM_COMPOSE.getType(),
                        TaskType.RTSP.getType()
                ),
                serverId,
                TaskStatus.RUNNING.name()
        );
        List<String> channelNames = new ArrayList<>();
        for (Task task : runningTasks) {
            if (task.getTypeAsEnum() == TaskType.IP_STREAM_COMPOSE
                    || task.getTypeAsEnum() == TaskType.SDI_STREAM_COMPOSE) {
                Screen screen = getScreenRepository().findOne(task.getReferenceId());
                if (screen != null && screen.getActiveSchema() != null) {
                    ScreenSchema screenSchema = screen.getActiveSchema();
                    List<ScreenPosition> screenPositions = screenSchema.getScreenPositions();
                    for (ScreenPosition screenPosition : screenPositions) {
                        Channel channel = screenPosition.getChannel();
                        if (channel != null) {
                            channelNames.add(channel.getName());
                        }
                    }
                }

            } else {
                Channel channel = channelRepository.findOne(task.getReferenceId());
                if (channel != null) {
                    channelNames.add(channel.getName());
                }
            }
        }
        return channelNames;
    }

    @Override
    public void resetTaskStatusByRefid(Integer screenId)
    {
        List<Task> tasks = getRepository().findByReferenceId(screenId);
        if(tasks != null && tasks.size()>0){
            Task screenTask = tasks.get(0);
            if(screenTask.isStatusEqual(TaskStatus.ERROR)){
                screenTask.setStatusWithEnum(TaskStatus.STOP);
                getRepository().save(screenTask);
                return;
            }
        }
    }
}
