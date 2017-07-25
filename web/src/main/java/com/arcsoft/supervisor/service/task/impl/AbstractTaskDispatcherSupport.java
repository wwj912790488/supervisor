package com.arcsoft.supervisor.service.task.impl;

import com.arcsoft.supervisor.commons.spring.SessionCallBack;
import com.arcsoft.supervisor.commons.spring.SessionTemplate;
import com.arcsoft.supervisor.exception.ObjectNotExistsException;
import com.arcsoft.supervisor.exception.service.BusinessException;
import com.arcsoft.supervisor.exception.service.BusinessExceptionDescription;
import com.arcsoft.supervisor.exception.service.Description;
import com.arcsoft.supervisor.model.domain.channel.Channel;
import com.arcsoft.supervisor.model.domain.graphic.Screen;
import com.arcsoft.supervisor.model.domain.task.Task;
import com.arcsoft.supervisor.model.vo.task.TaskStatus;
import com.arcsoft.supervisor.model.vo.task.TaskType;
import com.arcsoft.supervisor.service.ServiceSupport;
import com.arcsoft.supervisor.service.channel.ChannelService;
import com.arcsoft.supervisor.service.commons.lock.TaskRecordLock;
import com.arcsoft.supervisor.service.graphic.ScreenService;
import com.arcsoft.supervisor.service.task.TaskDispatcherFacade;
import com.arcsoft.supervisor.service.task.TaskExecutor;
import com.arcsoft.supervisor.service.task.TaskService;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.arcsoft.supervisor.exception.service.BusinessExceptionDescription.*;

/**
 * @author zw.
 */
public abstract class AbstractTaskDispatcherSupport extends ServiceSupport implements TaskDispatcherFacade {

    private final TaskExecutor taskExecutor;
    private final ScreenService screenService;
    private final TaskService taskService;
    private final ChannelService channelService;
    private final TaskRecordLock taskRecordLock;
    private final SessionTemplate sessionTemplate;

    /**
     * The max timeout seconds for start/stop complete.
     */
    private static final int DEFAULT_TIME_OUT_SECONDS = 60;

    public AbstractTaskDispatcherSupport(
            TaskExecutor taskExecutor,
            ScreenService screenService,
            TaskService taskService,
            ChannelService channelService,
            TaskRecordLock taskRecordLock,
            SessionTemplate sessionTemplate) {
        this.taskExecutor = taskExecutor;
        this.screenService = screenService;
        this.taskService = taskService;
        this.channelService = channelService;
        this.taskRecordLock = taskRecordLock;
        this.sessionTemplate = sessionTemplate;
    }

    public TaskExecutor getTaskExecutor() {
        return taskExecutor;
    }

    public ScreenService getScreenService() {
        return screenService;
    }

    public TaskService getTaskService() {
        return taskService;
    }

    public ChannelService getChannelService() {
        return channelService;
    }

    public TaskRecordLock getTaskRecordLock() {
        return taskRecordLock;
    }

    public SessionTemplate getSessionTemplate() {
        return sessionTemplate;
    }




    @Override
    public void restartScreenTask(int screenId, int profileId) {
        taskRecordLock.acquireScreenLock(screenId);
        try {
            stopScreen(screenId);
            startScreen(screenId, profileId);
        } finally {
            taskRecordLock.releaseScreenLock(screenId);
        }
    }

    @Override
    public void startScreenTask(int screenId, int taskProfileId) {
        taskRecordLock.acquireScreenLock(screenId);
        try {
            startScreen(screenId, taskProfileId);
        } finally {
            taskRecordLock.releaseScreenLock(screenId);
        }
    }

    @Override
    public void startScreenTask(int screenId, int taskProfileId, String serverId, int gpuStartIndex) {
        taskRecordLock.acquireScreenLock(screenId);
        try {
            startScreen(screenId, taskProfileId, serverId,gpuStartIndex);
        }catch (Exception e){
         logger.info(e.getMessage(),e);
            throw e;
        }
        finally {
            taskRecordLock.releaseScreenLock(screenId);
        }
    }

    @Override
    public void reloadOrStartScreenTask(int screenId, int taskProfileId, String serverId, int gpuStartIndex){
        taskRecordLock.acquireScreenLock(screenId);
        try {
            reloadOrStartScreen(screenId, taskProfileId, serverId,gpuStartIndex);
        }catch (Exception e){
            logger.info(e.getMessage(),e);
            throw e;
        } finally {
            taskRecordLock.releaseScreenLock(screenId);
        }
    }

    @Override
    public void stopScreenTask(int screenId) {
        taskRecordLock.acquireScreenLock(screenId);
        try {
            stopScreen(screenId);
        } finally {
            taskRecordLock.releaseScreenLock(screenId);
        }
    }



    private void startScreen(int screenId, int taskProfileId, String serverId, int gpuStartIndex) {
        final Screen screen = getScreen(screenId);
        checkNotNull(screen, SCREEN_NOT_EXISTS);
        int type = screen.getWallPosition().getWall().getType();

        Task screenTask = null;
        if (type == 1) {
            screenTask = taskService.createOrGetTask(screenId, TaskType.IP_STREAM_COMPOSE, serverId, taskProfileId,gpuStartIndex);
        } else if (type == 2) {
            screenTask = taskService.createOrGetTask(screenId, TaskType.SDI_STREAM_COMPOSE,
                    screen.getWallPosition().getSdiOutput().getServer().getId(), taskProfileId,gpuStartIndex);
        }
        checkNotNull(screenTask, TASK_NOT_EXIST);
        try {
            if (screenTask.isStopped()) {
                taskExecutor.start(screenTask.getId());
            }
            waitForStatus(screenTask.getId(), TaskStatus.RUNNING);
        }catch (Exception e){
            try{
                screenTask.setStatus(TaskStatus.STOP.toString());
                taskService.saveOrUpdate(screenTask);
            }catch (Exception e1){
            }
            throw e;
        }
    }

    private void reloadOrStartScreen(int screenId, int taskProfileId, String serverId, int gpuStartIndex) {
        final Screen screen = getScreen(screenId);
        checkNotNull(screen, SCREEN_NOT_EXISTS);
        int type = screen.getWallPosition().getWall().getType();
        Task screenTask = null;
        if (type == 1) {
            screenTask = taskService.createOrGetTask(screenId, TaskType.IP_STREAM_COMPOSE, serverId, taskProfileId,gpuStartIndex);
        } else if (type == 2) {
            screenTask = taskService.createOrGetTask(screenId, TaskType.SDI_STREAM_COMPOSE,
                    screen.getWallPosition().getSdiOutput().getServer().getId(), taskProfileId,gpuStartIndex);
        }
        checkNotNull(screenTask, TASK_NOT_EXIST);
        if (screenTask.isStopped()) {
            taskExecutor.start(screenTask.getId());
           waitForStatus(screenTask.getId(), TaskStatus.RUNNING);
        }
        else if(screenTask.isStatusEqual(TaskStatus.RUNNING)){
            taskExecutor.reload(screenTask.getId());
        }
    }

    private void startScreen(final int screenId, int taskProfileId) {
        startScreen(screenId, taskProfileId, null,-1);
    }


    private void stopScreen(int screenId) {
        Screen screen = getScreen(screenId);
        checkNotNull(screen, SCREEN_NOT_EXISTS);
        int type = screen.getWallPosition().getWall().getType();
        Task task = null;
        if (type == 1) {
            task = taskService.getByTypeAndReferenceId(screenId, TaskType.IP_STREAM_COMPOSE);
        } else if (type == 2) {
            task = taskService.getByTypeAndReferenceId(screenId, TaskType.SDI_STREAM_COMPOSE);
        }
        //checkNotNull(task, TASK_NOT_EXIST);
        if (task != null && task.isStatusEqual(TaskStatus.RUNNING)) {
            taskExecutor.stop(task.getId());
            waitForStatus(task.getId(), TaskStatus.STOP);
        }
    }

    /**
     * Returns the screen contains belongs {@code WallPosition, Wall, ServerComponent and ServerComponent's server}.
     * <p>This method will execute in {@code SessionTemplate's callback}.</p>
     *
     * @param screenId the identify value of screen
     */
    private Screen getScreen(final int screenId) {
        return sessionTemplate.execute(new SessionCallBack<Screen>() {
            @Override
            public Screen doInSession() {
                Screen screen = screenService.getById(screenId);
                //Force loading data
                screen.getWallPosition().getWall().getType();
                if (screen.getWallPosition().getWall().getType() == 2
                        && screen.getWallPosition().getSdiOutput() != null
                        && screen.getWallPosition().getSdiOutput().getServer() != null) {
                    screen.getWallPosition().getSdiOutput().getServer().getId();
                }
                return screen;
            }
        });
    }

    private void checkNotNull(Object obj, Description description) throws ObjectNotExistsException {
        if (obj == null) {
            throw BusinessException.create(description);
        }
    }

    protected void waitForStatus(int taskId, TaskStatus status) {
        List<Integer> ids = new ArrayList<>();
        ids.add(taskId);
        waitForStatus(ids, status);
    }

    /**
     * Waits the given {@code status} for {@code taskIds}.
     *
     * @param taskIds the item of task id
     * @param status  the status of task
     * @throws BusinessException thrown with {@link BusinessExceptionDescription#TASK_START_OR_STOP_TIMEOUT} if timeout occur
     *                           for wait for the given status
     */
    protected void waitForStatus(List<Integer> taskIds, TaskStatus status) {
        if (taskIds.isEmpty()) {
            return;
        }
        int waitSeconds = 0;
        while (!taskIds.isEmpty()) {
            for (Iterator<Integer> taskIdItr = taskIds.iterator(); taskIdItr.hasNext(); ) {
                Task task = taskService.getById(taskIdItr.next());
                if (task == null || task.isStatusEqual(status)) {
                    taskIdItr.remove();
                }else if(task.isStatusEqual(TaskStatus.ERROR)){
                    throw TASK_CANNOT_RUNNING.exception();
                }/*else if( status != TaskStatus.STOP && task.isStatusEqual(TaskStatus.STOP)){
                    throw TASK_CANNOT_RUNNING.exception();
                }*/
            }
            if (taskIds.isEmpty()) {
                break;
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                logger.error("", e);
            }
            waitSeconds++;
            if (waitSeconds == DEFAULT_TIME_OUT_SECONDS) {
                throw TASK_START_OR_STOP_TIMEOUT.exception();
            }
        }
    }

    @Override
    public void restartChannelTask(int channelId) {
        taskRecordLock.acquireChannelLock(channelId);
        try {
            Channel channel = channelService.getByIdWithOutLazy(channelId);
            checkNotNull(channel, CHANNEL_NOT_EXIST);
            List<Integer> needStopTaskIds = new ArrayList<>();
            Task rtspTask = taskService.createOrGetTask(channel.getId(), TaskType.RTSP);
            if (rtspTask != null && StringUtils.isNotBlank(rtspTask.getStatus())) {
                needStopTaskIds.add(rtspTask.getId());
                taskExecutor.stop(rtspTask.getId());
            }
            waitForStatus(needStopTaskIds, TaskStatus.STOP);

            List<Integer> needStartTaskIds = new ArrayList<>();
            if (/*(null != channel.getEnableRecord() && channel.getEnableRecord())
                    ||*/ (null != channel.getIsSupportMobile() && channel.getIsSupportMobile())) {
                needStartTaskIds.add(rtspTask.getId());
            }

            if (!needStartTaskIds.isEmpty()) {
                for (Integer taskId : needStartTaskIds) {
                    taskExecutor.start(taskId);
                }
                waitForStatus(needStartTaskIds, TaskStatus.RUNNING);
            }
        } finally {
            taskRecordLock.releaseChannelLock(channelId);
        }
    }

    @Override
    public void stopChannelTask(int channelId) {
        taskRecordLock.acquireChannelLock(channelId);
        try {
            Channel channel = channelService.getByIdWithOutLazy(channelId);
            checkNotNull(channel, CHANNEL_NOT_EXIST);
            List<Integer> needStopTaskIds = new ArrayList<>();
            Task rtspTask = taskService.getByTypeAndReferenceId(channel.getId(), TaskType.RTSP);
            if (rtspTask != null && StringUtils.isNotBlank(rtspTask.getStatus())) {
                needStopTaskIds.add(rtspTask.getId());
                taskExecutor.stop(rtspTask.getId());
            }
            waitForStatus(needStopTaskIds, TaskStatus.STOP);

        } finally {
            taskRecordLock.releaseChannelLock(channelId);
        }
    }

    @Override
    public boolean isTaskRunning(int channelId) {
        taskRecordLock.acquireChannelLock(channelId);
        try {
            Channel channel = channelService.getByIdWithOutLazy(channelId);
            checkNotNull(channel, CHANNEL_NOT_EXIST);
            Task rtspTask = taskService.getByTypeAndReferenceId(channel.getId(), TaskType.RTSP);
            return  taskExecutor.isTaskRunning(rtspTask.getId());

        } finally {
            taskRecordLock.releaseChannelLock(channelId);
        }
    }
}
