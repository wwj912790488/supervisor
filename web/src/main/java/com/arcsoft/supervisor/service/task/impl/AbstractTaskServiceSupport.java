package com.arcsoft.supervisor.service.task.impl;

import com.arcsoft.supervisor.cluster.action.server.RecognizeSDIRequest;
import com.arcsoft.supervisor.cluster.action.task.*;
import com.arcsoft.supervisor.commons.spring.event.EventReceiver;
import com.arcsoft.supervisor.model.domain.graphic.MessageStyle;
import com.arcsoft.supervisor.model.domain.graphic.Screen;
import com.arcsoft.supervisor.model.domain.graphic.Wall;
import com.arcsoft.supervisor.model.domain.server.Server;
import com.arcsoft.supervisor.model.domain.server.ServerComponent;
import com.arcsoft.supervisor.model.domain.task.Task;
import com.arcsoft.supervisor.model.vo.task.TaskStatus;
import com.arcsoft.supervisor.model.vo.task.TaskType;
import com.arcsoft.supervisor.repository.graphic.ScreenRepository;
import com.arcsoft.supervisor.repository.profile.TaskProfileRepository;
import com.arcsoft.supervisor.repository.server.ServerJpaRepository;
import com.arcsoft.supervisor.repository.task.TaskRepository;
import com.arcsoft.supervisor.service.TransactionSupport;
import com.arcsoft.supervisor.service.remote.RemoteExecutorServiceSupport;
import com.arcsoft.supervisor.service.server.event.ServerAliveChangedEvent;
import com.arcsoft.supervisor.service.server.event.ServerRemovedEvent;
import com.arcsoft.supervisor.service.task.TaskExecutor;
import com.arcsoft.supervisor.service.task.TaskPortAssigner;
import com.arcsoft.supervisor.service.task.TaskService;
import com.arcsoft.supervisor.service.task.gpu.GpuLoadBalanceManager;
import com.arcsoft.supervisor.utils.NamedThreadFactory;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PreDestroy;
import java.io.StringReader;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zw.
 */
public abstract class AbstractTaskServiceSupport extends RemoteExecutorServiceSupport implements TaskService, TransactionSupport {

    private final TaskRepository repository;
    private final ServerJpaRepository serverRepository;

    @Autowired
    private TaskExecutor taskExecutor;
    private final ScreenRepository screenRepository;

    private final TaskPortAssigner portAssigner;

    private final TaskProfileRepository taskProfileRepository;

    @Autowired
     private GpuLoadBalanceManager gpuLoadBalanceManager;

    private ExecutorService executor = Executors.newCachedThreadPool(NamedThreadFactory.create("TaskService"));

    public AbstractTaskServiceSupport(
            TaskRepository repository,
            ServerJpaRepository serverRepository,
            ScreenRepository screenRepository,
            TaskPortAssigner portAssigner,
            TaskProfileRepository taskProfileRepository) {
        this.repository = repository;
        this.serverRepository = serverRepository;
        this.screenRepository = screenRepository;
        this.portAssigner = portAssigner;
        this.taskProfileRepository = taskProfileRepository;
    }


    public TaskRepository getRepository() {
        return repository;
    }

    public ServerJpaRepository getServerRepository() {
        return serverRepository;
    }

    public TaskExecutor getTaskExecutor() {
        return taskExecutor;
    }

    public ScreenRepository getScreenRepository() {
        return screenRepository;
    }

    public TaskPortAssigner getPortAssigner() {
        return portAssigner;
    }

    public TaskProfileRepository getTaskProfileRepository() {
        return taskProfileRepository;
    }

    @PreDestroy
    public void destroy() {
        this.executor.shutdown();
    }

    @Override
    public void saveOrUpdate(Task task) {
        repository.save(task);
    }

    @Override
    public Task getById(int id) {
        return repository.findOne(id);
    }

    @Override
    public void delete(int id) {
        repository.delete(id);
    }

    @Override
    public Task getByTypeAndReferenceId(int referenceId, TaskType type) {
        return repository.findByTypeAndReferenceId(type.getType(), referenceId);
    }


    @Override
    public List<Task> getRunningComposeTasks() {
        return repository.findRunningComposeTasksByTypes(TaskType.IP_STREAM_COMPOSE.getType(), TaskType.SDI_STREAM_COMPOSE.getType());
    }


    @Override
    public Task createOrGetTask(int referenceId, TaskType type) {
        return createOrGetTask(referenceId, type, null, -1,-1);
    }

    @Override
    public Task createOrGetTask(int referenceId, TaskType type, int taskProfileId) {
        return createOrGetTask(referenceId, type, null, taskProfileId,-1);
    }

    @Override
    public Task createOrGetTask(int referenceId, TaskType type, String serverId, int taskProfileId, int gpuStartIndex) {
        Task task = repository.findByTypeAndReferenceId(type.getType(), referenceId);
        if (task != null) {
            if (taskProfileId != -1 && (task.getProfile() == null || task.getProfile().getId() != taskProfileId)) {
                task.setProfile(taskProfileRepository.findOne(taskProfileId));
            }
            if ((type == TaskType.SDI_STREAM_COMPOSE || type == TaskType.IP_STREAM_COMPOSE)
                    && StringUtils.isNotBlank(serverId)) {
                task.setServerId(serverId);
            }
            if (task.getTaskPorts() == null || task.getTaskPorts().isEmpty()) {
                task.addTaskPort(portAssigner.getTaskPorts(type));
            }

            if(gpuStartIndex>=0&&gpuStartIndex<8)
                task.setGpudIndex(gpuStartIndex);
            else
                task.setGpudIndex(-1);
            return task;
        }
        Task newTask = new Task(type.getType(), referenceId);
        if (StringUtils.isNotBlank(serverId)) {
            newTask.setServerId(serverId);
        }
        newTask.setProfile(taskProfileRepository.findOne(taskProfileId));
        newTask.addTaskPort(portAssigner.getTaskPorts(type));

        if(gpuStartIndex>=0&&gpuStartIndex<8)
            newTask.setGpudIndex(gpuStartIndex);
        else
            newTask.setGpudIndex(-1);
        repository.save(newTask);
        return newTask;
    }

    @Override
    public byte[] getRtspTaskThumbnail(int channelId) {
        Task task = getByTypeAndReferenceId(channelId, TaskType.RTSP);
        if (task != null && task.isStatusEqual(TaskStatus.RUNNING)) {
            Server server = serverRepository.getServer(task.getServerId());
            if (server != null) {
                GetTaskThumbnailRequest request = new GetTaskThumbnailRequest();
                request.setId(task.getId());
                GetTaskThumbnailResponse response = (GetTaskThumbnailResponse) remoteExecutorService.remoteExecute(request, server);
                return response.getData();
            }
        }
        return new byte[0];
    }

    @Override
    public void displayMessageOnComposeTask(int composeTaskId, String message) {
        Task task = getById(composeTaskId);
        if (task != null && task.isStatusEqual(TaskStatus.RUNNING)) {
            Server server = serverRepository.getServer(task.getServerId());
            if (server != null) {
                DisplayMessageRequest request = new DisplayMessageRequest();
                request.setComposeTaskId(composeTaskId);
                request.setMessage(message == null ? "" : message);
                request.setTaskType(task.getType());
                remoteExecutorService.remoteExecute(request, server);
            }
        }
    }

    @EventReceiver(TaskRunningEvent.class)
    @Transactional
    public void onTaskRunning(TaskRunningEvent event) {
        displayStyledMessageOnScreen(event.getScreenId(), event.getStyle(), event.getMessage());
    }

    @Override
    public void displayStyledMessageOnScreen(int screenId, MessageStyle style, String message) {
        Task composeTask = getByTypeAndReferenceId(screenId, TaskType.IP_STREAM_COMPOSE);
        if (composeTask == null || !composeTask.isStatusEqual(TaskStatus.RUNNING)) {
            return ;
        }
        Server server = serverRepository.getServer(composeTask.getServerId());
        if(server != null) {
            DisplayStyledMessageRequest request = new DisplayStyledMessageRequest();
            request.setTaskId(composeTask.getId());
            BeanUtils.copyProperties(style, request);
            request.setMessage(message);
            try {
                DisplayStyledMessageResponse response = (DisplayStyledMessageResponse) remoteExecutorService.remoteExecute(request, server);
            } catch(Exception e) {
                logger.debug("Failed to display styled message of task [id=" + composeTask.getId() + "]");
            }
        }

    }


    @Override
    public String getProgress(int composeTaskId) {
        Task task = getById(composeTaskId);
        if (task != null && task.isStatusEqual(TaskStatus.RUNNING)) {
            Server server = serverRepository.getServer(task.getServerId());
            if (server != null) {
                GetTaskProgressRequest request = new GetTaskProgressRequest();
                request.setId(composeTaskId);
                try {
                    GetTaskProgressResponse response = (GetTaskProgressResponse) remoteExecutorService.remoteExecute(request, server);
                    return response.getXml();
                } catch (Exception e) {
                    logger.error("Failed to get progress of task [id=" + composeTaskId + "]");
                }
            }
        }
        return null;
    }

    @Override
    public boolean isIPStreamComposeTaskHasOutput(int screenId) {
        Task composeTask = getByTypeAndReferenceId(screenId, TaskType.IP_STREAM_COMPOSE);
        if (composeTask == null) {
            return false;
        }
        String progressXml = getProgress(composeTask.getId());
        if (StringUtils.isBlank(progressXml)) {
            return false;
        }
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(new StringReader(progressXml));
            Element rootElement = document.getRootElement();
            Node node = rootElement.selectSingleNode("//input");
            return node != null
                    && StringUtils.isNotBlank(node.getStringValue())
                    && Long.parseLong(node.getStringValue()) > 101; //The udp input start time is 101
        } catch (DocumentException e) {
            logger.error("Failed to checks task [id=" + composeTask.getId() + "] output");
        }
        return false;
    }

    @Override
    public void recognize(final Server server, final ServerComponent sdi, final int number) {
        executor.submit(new Runnable() {

            @Override
            public void run() {
                RecognizeSDIRequest request = new RecognizeSDIRequest();
                request.setSdiName(sdi.getName());
                request.setNumber(number);
                try {
                    remoteExecutorService.remoteExecute(request, server);
                } catch (Exception e) {
                    logger.error("Failed to recognize sdi = " + sdi.getName() + " at server = " + server.getName());
                }

            }
        });

    }

    @Override
    public List<Task> getChannelTasksByChannelIds(List<Integer> channelIds) {
        return repository.findByTypeAndReferenceIdIn(TaskType.RTSP.getType(), channelIds);
    }

    @Override
    public Task getScreenTask(int screenId) {
        Wall wall = screenRepository.findOne(screenId).getWallPosition().getWall();
        TaskType taskType = wall.getType() == 1 ? TaskType.IP_STREAM_COMPOSE : TaskType.SDI_STREAM_COMPOSE;
        return getByTypeAndReferenceId(screenId, taskType);
    }


    @Override
    public Integer getUsedTaskProfileIdByScreenId(int screenId) {
        Task task = getScreenTask(screenId);
        return task != null && task.getProfile() != null && task.getProfile().getId() != null
                ? task.getProfile().getId()
                : null;
    }

    @Override
    public String getTranscoderXml(int taskId) {
        Task task = getById(taskId);
        if (task != null && task.isStatusEqual(TaskStatus.RUNNING)) {
            Server server = serverRepository.getServer(task.getServerId());
            if (server != null) {
                GetTranscoderXmlRequest request = new GetTranscoderXmlRequest();
                request.setTaskId(taskId);
                request.setTaskType(task.getTypeAsEnum());
                try {
                    GetTranscoderXmlResponse response = (GetTranscoderXmlResponse) remoteExecutorService.remoteExecute(request, server);
                    if (response.isSuccess()) {
                        return response.getTranscoderXml();
                    }
                } catch (Exception e) {
                    logger.error("Failed to get transcoder xml of task [id=" + taskId + "]");
                }
            }
        }
        return null;
    }

    @EventReceiver(value = ServerRemovedEvent.class)
    @Transactional
    public void onServerRemoved(ServerRemovedEvent event) {
        for (Server server : event.getRemovedServers()) {
            List<Task> tasks = repository.findByServerIdIn(Collections.singletonList(server.getId()));
            logger.info("Remove all of tasks on server [id={}] cause by server remove event", server.getId());
            for (Task task : tasks) {
                try {
                    taskExecutor.stop(task.getId(), server);
                } catch (Exception e) {
                    logger.error("Failed to do remove for task [id={}] with " +
                            "server remove event on server [id={}]", task.getId(), server.getId());
                }
                gpuLoadBalanceManager.releaseGpuItems(task.getId(), server.getId());
                repository.delete(task.getId());
            }

            //TODO: clear screen output url of compose task
            //TODO: clear mobile output url of channel task
        }
    }


    /**
     * Handles the event of {@code ServerAliveChangedEvent}.
     *
     * @param event the event to be handle
     */
    @Transactional
    @EventReceiver(value = ServerAliveChangedEvent.class)
    @SuppressWarnings("all")
    public void onServerAlivedChanged(ServerAliveChangedEvent event) {
        logger.info("Ready to process " + event);
        syncTaskStatus(event.getServer());
        //syncRunningTaskStatus(event.getServer());
        logger.info("Finish process " + event);
    }

    private void syncTaskStatus(Server server) {
        final List<Task> tasks = getTasks(server.getId());
        if(!tasks.isEmpty()) {
            if(server.isAlive()) {
                for(final Task task : tasks) {
                    syncTaskWhenServerAlive(task);
                }
            } else {
                for(final Task task : tasks) {
                    syncTaskWhenServerDown(task);
                }
            }
        }
    }

    protected void syncTaskWhenServerDown(Task task) {
        if(task.getStatus() != null) {
            TaskStatus status = TaskStatus.valueOf(task.getStatus());
            switch (status) {
                case RUNNING:
                    disconnectRunningTaskWhenServerDown(task);
                    break;
                default:
                    break;
            }
        }
    }

    private void disconnectRunningTaskWhenServerDown(Task task) {
        task.setStatus(TaskStatus.DISCONNECTED.toString());
    }

    protected void syncTaskWhenServerAlive(Task task) {
        if(task.getStatus() != null) {
            TaskStatus status = TaskStatus.valueOf(task.getStatus());
            switch (status) {
                case RUNNING:
                    syncRunningTaskWhenServerAlive(task);
                    break;
                case DISCONNECTED:
                    syncDisconnectedTaskWhenServerAlive(task);
                    break;
                default:
                    break;
            }
        }
    }

    private void syncRunningTaskWhenServerAlive(Task task) {
        if(!taskExecutor.isTaskRunning(task.getId())) {
            getEventManager().submit(new TaskStopEvent(task.getId(), new Date()));
            taskExecutor.start(task.getId());
        }
    }

    private void syncDisconnectedTaskWhenServerAlive(Task task) {
        if(!taskExecutor.isTaskRunning(task.getId())) {
            getEventManager().submit(new TaskStopEvent(task.getId(), new Date()));
           taskExecutor.start(task.getId());
        } else {
            task.setStatus(TaskStatus.RUNNING.toString());
        }
    }

    private List<Task> getTasks(String serverId) {
        return repository.findByTypeInAndServerId(Lists.newArrayList(TaskType.IP_STREAM_COMPOSE.getType(), TaskType.SDI_STREAM_COMPOSE.getType()),
                serverId);
    }

    /**
     * Synchronizes status of {@code server}'s tasks.
     *
     * @param server which server's task need to be synchronize
     */
    private void syncRunningTaskStatus(Server server) {
        final List<Task> runningTasks = getRunningTasks(server.getId());
        if (!runningTasks.isEmpty()) {
            if (server.isAlive()) {
                for (final Task task : runningTasks) {
                    if (!taskExecutor.isTaskRunning(task.getId())) {
                        doClearAfterTaskIsStop(task);
                    }
                }
            } else {
                for (final Task task : runningTasks) {
                    doClearAfterTaskIsStop(task);
                }
            }
        }
    }

    protected List<Task> getRunningTasks(String serverId) {
        return repository.findByTypeInAndServerIdAndStatus(
                Lists.newArrayList(TaskType.IP_STREAM_COMPOSE.getType(), TaskType.SDI_STREAM_COMPOSE.getType()),
                serverId,
                TaskStatus.RUNNING.toString()
        );
    }

    private void doClearAfterTaskIsStop(Task task) {
        task.setStatus(TaskStatus.STOP.toString());
//        if (StringUtils.isNotBlank(task.getServerId())) {
//            task.setServerId(null);
//        }
        if (task.getReferenceId() != null && task.getTypeAsEnum() == TaskType.IP_STREAM_COMPOSE) {
            Screen screen = screenRepository.findOne(task.getReferenceId());
            if (screen != null) {
                screen.setAddress(null);
                screen.setRtspFileName(null);
            }
        }

        doAfterOfDoClearAfterTaskIsStop(task);

    }

    protected void doAfterOfDoClearAfterTaskIsStop(Task task) {

    }
}
