package com.arcsoft.supervisor.service.task.impl;

import com.arcsoft.supervisor.cluster.action.ActionErrorCode;
import com.arcsoft.supervisor.cluster.action.ActionHandler;
import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.task.StateChangeRequest;
import com.arcsoft.supervisor.cluster.action.task.StateChangeResponse;
import com.arcsoft.supervisor.cluster.app.ActionException;
import com.arcsoft.supervisor.cluster.app.Request;
import com.arcsoft.supervisor.cluster.app.Response;
import com.arcsoft.supervisor.commons.profile.Production;
import com.arcsoft.supervisor.commons.spring.event.EventManager;
import com.arcsoft.supervisor.model.domain.channel.Channel;
import com.arcsoft.supervisor.model.domain.graphic.Screen;
import com.arcsoft.supervisor.model.domain.server.OpsServer;
import com.arcsoft.supervisor.model.domain.server.Server;
import com.arcsoft.supervisor.model.domain.task.Task;
import com.arcsoft.supervisor.model.vo.task.TaskStateChange;
import com.arcsoft.supervisor.model.vo.task.TaskStatus;
import com.arcsoft.supervisor.model.vo.task.TaskType;
import com.arcsoft.supervisor.repository.channel.ChannelRepository;
import com.arcsoft.supervisor.repository.graphic.ScreenRepository;
import com.arcsoft.supervisor.repository.server.ServerJpaRepository;
import com.arcsoft.supervisor.repository.server.ServerRepository;
import com.arcsoft.supervisor.repository.task.TaskRepository;
import com.arcsoft.supervisor.service.ServiceSupport;
import com.arcsoft.supervisor.service.server.OpsServerOperator;
import com.arcsoft.supervisor.service.server.ServerService;
import com.arcsoft.supervisor.service.task.TaskExecutor;
import com.arcsoft.supervisor.service.task.TaskKeeper;
import com.arcsoft.supervisor.service.task.TaskService;
import com.arcsoft.supervisor.service.task.gpu.GpuLoadBalanceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.arcsoft.supervisor.model.vo.task.TaskStatus.RUNNING;
import static com.arcsoft.supervisor.model.vo.task.TaskStatus.STOP;
import static com.arcsoft.supervisor.model.vo.task.TaskStatus.ERROR;

/**
 * Handling the status of all of tasks.
 *
 * @author zw.
 */
@Service
@Production
public class TaskStateChangeHandler extends ServiceSupport implements ActionHandler {

    private final TaskRepository taskRepository;

    private final ChannelRepository channelRepository;

    private final OpsServerOperator<OpsServer> opsServerOperator;

    private final ScreenRepository screenRepository;

    private final GpuLoadBalanceManager gpuLoadBalance;

    private Map<Integer, TaskKeeper> taskKeepers = new HashMap<>();

    @Autowired
    private ServerJpaRepository serverRepository;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private EventManager eventManager;

    @Autowired
    public TaskStateChangeHandler(
            TaskRepository taskRepository,
            ChannelRepository channelRepository,
            OpsServerOperator<OpsServer> opsServerOperator,
            ScreenRepository screenRepository,
            GpuLoadBalanceManager gpuLoadBalance) {
        this.taskRepository = taskRepository;
        this.channelRepository = channelRepository;
        this.opsServerOperator = opsServerOperator;
        this.screenRepository = screenRepository;
        this.gpuLoadBalance = gpuLoadBalance;
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

    public StateChangeResponse handleStateChange(StateChangeRequest request) {
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
                    boolean stopped = (status == STOP || status == TaskStatus.ERROR);
                    task.setStatus(stopped ? STOP.toString() : status.toString());
                    switch (status) {
                        case STOP:
                            task.setStatus(STOP.toString());
                            doAfterTaskStop(task);
                            break;
                        case ERROR:
                            task.setStatus(ERROR.toString());
                            doAfterTaskError(task);
                            break;
                        case RUNNING:
                            task.setStatus(RUNNING.toString());
                            doAfterTaskRunning(task, stateChange);
                            break;
                    }

                    TaskKeeper keeper = taskKeepers.get(task.getId());
                    if(keeper == null) {
                        keeper = new TaskKeeper();
                        taskKeepers.put(task.getId(), keeper);
                    }
                    boolean restart = keeper.updateStatus(status);
                    if(restart) {
                        taskExecutor.start(task.getId());
                    }
                }
            } catch (Exception e) {
                logger.error("Failed to process StateChangeRequest", e);
            }

        }
        return response;
    }

    private void doAfterTaskRunning(Task task, TaskStateChange stateChange) {
        if (task.getTypeAsEnum() == TaskType.IP_STREAM_COMPOSE) {
            task.setPid(stateChange.getPid());
            Server taskServer = serverRepository.getServer(task.getServerId());
            OpsServer server = getOpsServer(task.getReferenceId());
            Screen screen = screenRepository.findOne(task.getReferenceId());
            if(screen != null) {
                eventManager.submit(new TaskRunningEvent(screen.getId(), screen.getStyle(), screen.getMessage()));
                if (server != null) {
                    server.getId(); //trigger lazy loading to fill all properties value
                    if (!stateChange.getRtmpOpsFileName().contains("null"))
                        opsServerOperator.start(server, stateChange.getRtmpOpsFileName(), taskServer == null ? null : taskServer.getIp());
                    else if (stateChange.getComposeTaskUdpPort() != null)
                        opsServerOperator.start(server, "udp://" + server.getIp() + ":" + stateChange.getComposeTaskUdpPort(), taskServer == null ? null : taskServer.getIp());//udp
                }
            }
        }
    }

    private void doAfterTaskStop(Task task) {
        switch (task.getTypeAsEnum()) {
            case IP_STREAM_COMPOSE:
                eventManager.submit(new TaskStopEvent(task.getId(), new Date()));
                gpuLoadBalance.releaseGpuItems(task.getId(), task.getServerId());
                clearIPStreamCompose(task.getReferenceId());
                break;
            case RTSP:
                logger.info("doAfterTaskStop, taskid="+task.getReferenceId());
                clearRtsp(task.getReferenceId());
                break;
            default:
                break;

        }
//        task.setServerId(null);
    }

    private void doAfterTaskError(Task task) {
        switch (task.getTypeAsEnum()) {
            case IP_STREAM_COMPOSE:
                eventManager.submit(new TaskStopEvent(task.getId(), new Date()));
                gpuLoadBalance.releaseGpuItems(task.getId(), task.getServerId());
                clearIPStreamCompose(task.getReferenceId());
                break;
            case RTSP:
                logger.info("doAfterTaskError, taskid="+task.getReferenceId());
                clearRtsp(task.getReferenceId());
                break;
            default:
                break;

        }
//        task.setServerId(null);
    }

    private void clearRtsp(int channelId) {
        Channel channel = channelRepository.findOne(channelId);
        if (channel != null) {
//            channel.clearMobileAddress();
            channel.setEndTimeOfLastRecordHistory();
        }
    }

    private void clearIPStreamCompose(int screenId) {
        Screen screen = screenRepository.findOne(screenId);
        if (screen != null) {
            screen.clearRtspFileName();
            screen.setAddress(null);
            stopOpsServer(screen);
        }
    }

    private OpsServer getOpsServer(int screenId) {
        return getOpsServer(screenRepository.findOne(screenId));
    }

    private OpsServer getOpsServer(Screen screen){
        if (screen != null && screen.getWallPosition() != null && screen.getWallPosition().getOpsServer() != null){
            OpsServer server = screen.getWallPosition().getOpsServer();
            server.getId(); //trigger lazying loading
            return server;
        }
        return null;
    }

    private void stopOpsServer(Screen screen) {
        OpsServer opsServer = getOpsServer(screen);
        if (opsServer != null){
            opsServerOperator.stop(opsServer);
        }
    }


}
