package com.arcsoft.supervisor.sartf.service.task.impl;

import com.arcsoft.supervisor.cluster.action.ActionErrorCode;
import com.arcsoft.supervisor.cluster.action.ActionHandler;
import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.task.StateChangeRequest;
import com.arcsoft.supervisor.cluster.action.task.StateChangeResponse;
import com.arcsoft.supervisor.cluster.app.ActionException;
import com.arcsoft.supervisor.cluster.app.Request;
import com.arcsoft.supervisor.cluster.app.Response;
import com.arcsoft.supervisor.commons.profile.Sartf;
import com.arcsoft.supervisor.model.domain.channel.Channel;
import com.arcsoft.supervisor.model.domain.task.Task;
import com.arcsoft.supervisor.model.domain.user.SartfUser;
import com.arcsoft.supervisor.model.vo.task.TaskStateChange;
import com.arcsoft.supervisor.model.vo.task.TaskStatus;
import com.arcsoft.supervisor.repository.channel.ChannelRepository;
import com.arcsoft.supervisor.repository.task.TaskRepository;
import com.arcsoft.supervisor.sartf.repository.user.SartfUserRepository;
import com.arcsoft.supervisor.service.ServiceSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Sartf
public class SartfTaskStateChangeHandler extends ServiceSupport implements ActionHandler {

    private final SartfUserRepository userRepository;
    private final TaskRepository taskRepository;
    private final ChannelRepository channelRepository;

    @Autowired
    public SartfTaskStateChangeHandler(
            SartfUserRepository userRepository,
            TaskRepository taskRepository,
            ChannelRepository channelRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.channelRepository = channelRepository;
    }

    @Override
    public int[] getActions() {
        return new int[]{
                Actions.TASK_STATE_CHANGE
        };
    }

    @Transactional
    @Override
    public Response execute(Request request) throws ActionException {
        return handleStateChange((StateChangeRequest) request);
    }

    private StateChangeResponse handleStateChange(StateChangeRequest request) {
        StateChangeResponse response = new StateChangeResponse();
        response.setErrorCode(ActionErrorCode.SUCCESS);
        List<TaskStateChange> taskStateChanges = request.getStates();
        if (taskStateChanges != null && !taskStateChanges.isEmpty()) {
            try {
                for (TaskStateChange stateChange : taskStateChanges) {
                    Task task = taskRepository.findOne(stateChange.getId());
                    if (task == null) {
                        continue;
                    }
                    TaskStatus status = TaskStatus.valueOf(stateChange.getState());

                    // Ignore running status on USER_RELATED_COMPOSE task and ready status on RTSP task
                    if ( (task.getType() == 5 && status == TaskStatus.RUNNING)
                            || (task.getType() == 3 && status == TaskStatus.READY) ) {
                        continue;
                    }

                    if (task.getType() == 5 && status == TaskStatus.READY) {
                        status = TaskStatus.RUNNING;
                    }
                    boolean stopped = (status == TaskStatus.STOP || status == TaskStatus.ERROR);
                    task.setStatus(stopped ? TaskStatus.STOP.toString() : status.toString());
                    if (stopped) {
                        doAfterTaskStop(task);
                    }
                }
            } catch (Exception e) {
                logger.error("Failed to process StateChangeRequest", e);
            }

        }
        return response;
    }

    private void doAfterTaskStop(Task task) {
        switch (task.getTypeAsEnum()) {
            case USER_RELATED_COMPOSE:
                clearUserCurrentConfig(task.getReferenceId());
            case RTSP:
                clearRtsp(task.getReferenceId());
            default:
                break;
        }
    }

    private void clearUserCurrentConfig(int userId) {
        SartfUser user = userRepository.findOne(userId);
        user.setCurrent(null);
    }

    private void clearRtsp(int channelId) {
        Channel channel = channelRepository.findOne(channelId);
        if (channel != null) {
            channel.clearMobileAddress();
            channel.setEndTimeOfLastRecordHistory();
        }
    }
}
