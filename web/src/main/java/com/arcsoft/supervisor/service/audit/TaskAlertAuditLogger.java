package com.arcsoft.supervisor.service.audit;


import com.arcsoft.supervisor.commons.SupervisorDefs;
import com.arcsoft.supervisor.model.domain.channel.Channel;
import com.arcsoft.supervisor.model.domain.log.ServiceLog;
import com.arcsoft.supervisor.model.domain.task.Task;
import com.arcsoft.supervisor.model.vo.task.TaskType;
import com.arcsoft.supervisor.repository.channel.ChannelRepository;
import com.arcsoft.supervisor.repository.log.ServiceLogRepository;
import com.arcsoft.supervisor.repository.task.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * A implementation to audit the alert task.
 *
 * @author zw.
 */
@Service
public class TaskAlertAuditLogger extends AbstractAuditLogger<TaskAlertAuditContent> {

    @Autowired
    private ServiceLogRepository serviceLogRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private ChannelRepository channelRepository;

    @Transactional
    @Override
    public void log(TaskAlertAuditContent content) {
        Task task = taskRepository.findOne(content.getTaskId());
        if (task == null) {
            return;
        }
        if (task.getTypeAsEnum() == TaskType.RTSP) {
            //Add channel name if the task is channel
            Channel channel = channelRepository.findOne(task.getReferenceId());
            if (channel != null) {
                content.setChannelName(channel.getName());
            }
            content.setModule(SupervisorDefs.Modules.CHANNEL);
        } else {
            content.setModule(SupervisorDefs.Modules.GRAPHICS);
        }

        ServiceLog serviceLog = content.toServiceLog(
                format(content, getExprKey(task, content.getLevel())) + ", " + content.getDescription()
        );
        serviceLogRepository.save(serviceLog);
    }

    private String getExprKey(Task task, AuditLevel level) {
        return TaskType.RTSP == task.getTypeAsEnum() ? getChannelTaskExprKey(level) : getComposeTaskExprKey(level);
    }


    private String getChannelTaskExprKey(AuditLevel level) {
        return level == AuditLevel.ERROR ? ExpressionKeys.TASK_CHANNEL_ERROR.getKey()
                : ExpressionKeys.TASK_CHANNEL_WARN.getKey();
    }

    private String getComposeTaskExprKey(AuditLevel level) {
        return level == AuditLevel.ERROR ? ExpressionKeys.TASK_COMPOSE_ERROR.getKey()
                : ExpressionKeys.TASK_COMPOSE_WARN.getKey();
    }

    enum ExpressionKeys {
        TASK_CHANNEL_ERROR("task.channel.error"),
        TASK_CHANNEL_WARN("task.channel.warn"),
        TASK_COMPOSE_ERROR("task.compose.error"),
        TASK_COMPOSE_WARN("task.compose.warn");

        private final String key;

        ExpressionKeys(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }

}
