package com.arcsoft.supervisor.agent.service.task.impl;

import com.arcsoft.supervisor.agent.service.agent.AgentService;
import com.arcsoft.supervisor.agent.service.remote.RemoteExecutorServiceSupport;
import com.arcsoft.supervisor.agent.service.task.*;
import com.arcsoft.supervisor.agent.service.task.support.AbstractComposeStreamTaskProcessor;
import com.arcsoft.supervisor.agent.service.task.support.AbstractIpComposeStreamTaskProcessorSupport;
import com.arcsoft.supervisor.cd.CheckResultListener;
import com.arcsoft.supervisor.cd.MediaCheckerApp;
import com.arcsoft.supervisor.cd.data.AbstractInfo;
import com.arcsoft.supervisor.cd.data.CheckResultInfo;
import com.arcsoft.supervisor.cluster.action.ActionErrorCode;
import com.arcsoft.supervisor.cluster.action.ActionHandler;
import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.task.*;
import com.arcsoft.supervisor.cluster.app.ActionException;
import com.arcsoft.supervisor.cluster.app.Request;
import com.arcsoft.supervisor.cluster.app.Response;
import com.arcsoft.supervisor.model.vo.task.TaskAlertContent;
import com.arcsoft.supervisor.model.vo.task.TaskStateChange;
import com.arcsoft.supervisor.model.vo.task.TaskStatus;
import com.arcsoft.supervisor.model.vo.task.TaskType;
import com.arcsoft.supervisor.model.vo.task.cd.ContentDetectResult;
import com.arcsoft.supervisor.model.vo.task.compose.ComposeTaskParams;
import com.arcsoft.supervisor.transcoder.*;
import com.arcsoft.supervisor.utils.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * A default implementation of {@code TaskManager} to manage the all of task.
 *
 * @author zw.
 */
public class DefaultTaskManagerImpl extends RemoteExecutorServiceSupport implements TaskManager, ActionHandler, CheckResultListener,
        ITranscodingStatusListener, ITranscodingMessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultTaskManagerImpl.class);

    /**
     * A queue to holds all of status of tasks.
     */
    private final BlockingQueue<StateChangeRequest> stateChangeRequestBlockingQueue;

    /**
     * A queue to holds all of content detect result.
     */
    private BlockingQueue<ContentDetectResult> contentDetectResultBlockingQueue;

    /**
     * A queue to holds all of alert content of task.
     */
    private BlockingQueue<TaskAlertContent> alertContentBlockingQueue;

    /**
     * A thread pool for worker execute.
     */
    private final ExecutorService workerPool;

    /**
     * Holds all of instance of <code>Processor</code>
     */
    private Map<TaskType, TaskProcessor> taskProcessor;

    /**
     *  The filters for alert message.
     */
    private List<AlertMessageFilter> alertMessageFilters;

    /**
     * The <code>transcoder</code> instance in here is just used to
     * report status and destroy task before shutdown.
     */
    private Transcoder transcoder;

    /**
     * The <code>mediaCheckerApp</code> instance in here is just used to
     * report status and destroy content result tasks before shutdown.
     */
    private MediaCheckerApp mediaCheckerApp;

    /**
     * Currently is support media check functional or not.
     */
    private boolean isSupportMediaChecker;
    /**
     * The <code>streamFileResourceManager</code> instance in here is just used to
     * deletes rtsp stream file before destroy.
     */
    private RtspStreamFileResourceManager streamFileResourceManager;

    private AgentService agentService;

    private TaskStateChangeFactory stateChangeFactory;

    public DefaultTaskManagerImpl() {
        this.stateChangeRequestBlockingQueue = new LinkedBlockingQueue<>();
        this.alertContentBlockingQueue = new LinkedBlockingQueue<>();
        this.workerPool = Executors.newFixedThreadPool(3, NamedThreadFactory.create("TaskManager"));
        workerPool.execute(new StateChangeWorker());
        workerPool.execute(new TaskAlertWorker());
    }


    public void setTranscoder(Transcoder transcoder) {
        this.transcoder = transcoder;
    }

    public void setMediaCheckerApp(MediaCheckerApp mediaCheckerApp) {
        this.mediaCheckerApp = mediaCheckerApp;
    }

    public void setTaskProcessor(Map<TaskType, TaskProcessor> taskProcessor) {
        this.taskProcessor = taskProcessor;
    }

    public void setStreamFileResourceManager(RtspStreamFileResourceManager streamFileResourceManager) {
        this.streamFileResourceManager = streamFileResourceManager;
    }

    public void setAgentService(AgentService agentService) {
        this.agentService = agentService;
    }

    public void setAlertMessageFilters(List<AlertMessageFilter> alertMessageFilters) {
        this.alertMessageFilters = alertMessageFilters;
    }

    @Override
    public void init(String functions) {
        if (functions.contains("cd")) { //TODO: Don't hard code it.
            isSupportMediaChecker = true;
            mediaCheckerApp.Init();
            this.contentDetectResultBlockingQueue = new LinkedBlockingQueue<>();
            workerPool.execute(new ContentDetectResultReportWorker());
        }
    }

    public void destroy() {
        beforeStopTranscoderTasks();
        transcoder.destroy();
        shutdownMediaCheckApp();
        closeQueue();
        workerPool.shutdown();
        waitForWorkerPoolShutdown(60);

    }

    @Override
    public void setStateChangeFactory(TaskStateChangeFactory stateChangeFactory) {
        this.stateChangeFactory = stateChangeFactory;
    }

    /**
     * Forces shutdown the thread pool after given {@code seconds}.
     *
     * @param seconds the max seconds waiting after shutdown pool
     */
    private void waitForWorkerPoolShutdown(int seconds) {
        LOGGER.error("Wait for worker thread pool complete, will be force shutdown it after " +seconds+" seconds.");
        try {
            if (workerPool.awaitTermination(seconds, TimeUnit.SECONDS)) {
                workerPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            LOGGER.error("Failed to shutdown worker thread pool.");
        }
    }

    /**
     * Closes the {@link #stateChangeRequestBlockingQueue} and {@link #contentDetectResultBlockingQueue}.
     *
     * @return {@code true} if failed during doing close
     */
    private boolean closeQueue() {
        boolean failedStopped = false;
        try {
            stateChangeRequestBlockingQueue.put(new StopStateChangeRequest());
        } catch (InterruptedException e) {
            LOGGER.error("Failed to stop StateChange worker.");
            failedStopped = true;
        }

        try {
            alertContentBlockingQueue.put(new StopTaskAlertContent());
        } catch (InterruptedException e) {
            LOGGER.error("Failed to stop TaskAlert worker.");
            failedStopped = true;
        }

        if (isSupportMediaChecker) {
            try {
                contentDetectResultBlockingQueue.put(new StopContentDetectResult());
            } catch (InterruptedException e) {
                LOGGER.error("Failed to stop ContentDetectResult worker.");
                failedStopped = true;
            }
        }

        return failedStopped;
    }

    /**
     * Closes the {@link #mediaCheckerApp}.The method only called when {@link #isSupportMediaChecker} is true.
     */
    private void shutdownMediaCheckApp() {
        if (isSupportMediaChecker) {
            mediaCheckerApp.shutdown();
        }
    }

    /**
     * Do some operators before destroy transcoder.for now it will do below things:
     * <ul>
     * <li>Remove rtsp resource in tracker if exist.</li>
     * <li>Send stop state change request of each task to commander.</li>
     * </ul>
     */
    private void beforeStopTranscoderTasks() {
        ITranscodingTracker[] transcodingTrackers = transcoder.getAllTranscodingTrackers();
        if (transcodingTrackers != null) {
            for (ITranscodingTracker tracker : transcodingTrackers) {
                if (tracker == null) {
                    continue;
                }
                //remove the rtsp .stream file in tracker.
                streamFileResourceManager.deleteStreamFile(tracker);
                TranscodingKey transcodingKey = tracker.getTranscodingKey();
                Integer pid = tracker.getPid();
                publishStateChange(transcodingKey.getTaskId(), TaskStatus.STOP, pid);
            }
        }
    }

    @Override
    public int[] getActions() {
        return new int[]{
                Actions.START_TASK,
                Actions.STOP_TASK,
                Actions.TASK_COMPOSE_RELOAD,
                Actions.TASK_GET_TRANSCODER_XML,
                Actions.TASK_COMPOSE_STREAM_SCREEN_WARNING_BORDER,
                Actions.GET_TASK_THUMBNAIL,
                Actions.TASK_COMPOSE_STREAM_DISPLAY_MESSAGE,
                Actions.TASK_PROCESS_DETECT,
                Actions.GET_TASK_PROGRESS,
                Actions.TASK_DISPLAY_STYLED_MESSAGE
        };
    }

    @Override
    public Response execute(Request request) throws ActionException {
        if (request instanceof StartRequest) {
            return start((StartRequest) request);
        } else if (request instanceof StopRequest) {
            return stop((StopRequest) request);
        } else if (request instanceof ReloadRequest) {
            return reload((ReloadRequest) request);
        } else if (request instanceof GetTranscoderXmlRequest) {
            return getTranscoderXml((GetTranscoderXmlRequest) request);
        } else if (request instanceof GetTaskThumbnailRequest) {
            return getTaskThumbnailResponse((GetTaskThumbnailRequest) request);
        } else if (request instanceof ScreenWarningBorderRequest) {
            return processWarningBorderRequest((ScreenWarningBorderRequest) request);
        } else if (request instanceof DisplayMessageRequest) {
            return processDisplayMessageRequest((DisplayMessageRequest) request);
        } else if (request instanceof TaskProcessDetectRequest) {
            return detectTaskIsExist((TaskProcessDetectRequest) request);
        } else if (request instanceof GetTaskProgressRequest) {
            return getTaskProgressResponse((GetTaskProgressRequest) request);
        } else if (request instanceof DisplayStyledMessageRequest) {
            return processDisplayStyledMessageRequest((DisplayStyledMessageRequest) request);
        }
        return null;
    }


    private StartResponse start(StartRequest request) {
        StartResponse response = new StartResponse();
        response.setErrorCode(ActionErrorCode.SUCCESS);
        try {
            if (request.getTask().getTaskType() == TaskType.IP_STREAM_COMPOSE) {
                AbstractIpComposeStreamTaskProcessorSupport composeStreamProcessor = (AbstractIpComposeStreamTaskProcessorSupport) taskProcessor
                        .get(request.getTask().getTaskType());
                ComposeTaskParams composeTask = (ComposeTaskParams) request.getTask();
                response.setPort(composeStreamProcessor.start(composeTask));
            } else {
                taskProcessor.get(request.getTask().getTaskType()).start(request.getTask());
            }
        } catch (Exception e) {
            response.setErrorCode(ActionErrorCode.UNKNOWN_ERROR);
            LOGGER.error("", e);
        }
        return response;
    }


    private StopResponse stop(StopRequest request) {
        StopResponse response = new StopResponse();
        response.setErrorCode(ActionErrorCode.SUCCESS);
        try {
            List<Integer> taskIds = request.getTaskIds();
            if (taskIds != null && taskIds.size() > 0) {
                for (Integer id : taskIds) {
                    taskProcessor.get(request.getTaskType()).stop(id);
                }
            }
        } catch (Exception e) {
            response.setErrorCode(ActionErrorCode.UNKNOWN_ERROR);
            LOGGER.error("", e);
        }
        return response;
    }

    private ReloadResponse reload(ReloadRequest request) {
        ReloadResponse reloadResponse = new ReloadResponse();
        reloadResponse.setErrorCode(ActionErrorCode.SUCCESS);
        // Reload functionality only support for compose task
        AbstractComposeStreamTaskProcessor composeStreamProcessor = (AbstractComposeStreamTaskProcessor) taskProcessor
                .get(request.getTaskParams().getTaskType());
        try {
            composeStreamProcessor.reload(request.getTaskParams());
        } catch (Exception e) {
            LOGGER.error("", e);
            reloadResponse.setErrorCode(ActionErrorCode.UNKNOWN_ERROR);
        }
        return reloadResponse;
    }

    private GetTranscoderXmlResponse getTranscoderXml(GetTranscoderXmlRequest request) {
        GetTranscoderXmlResponse response = new GetTranscoderXmlResponse();
        response.setErrorCode(ActionErrorCode.SUCCESS);
        TaskProcessor processor = taskProcessor.get(request.getTaskType());
        try {
            response.setTranscoderXml(processor.getTranscoderXml(request.getTaskId()));
        } catch (Exception e) {
            LOGGER.error("", e);
            response.setErrorCode(ActionErrorCode.UNKNOWN_ERROR);
        }
        return response;
    }

    private TaskProcessDetectResponse detectTaskIsExist(TaskProcessDetectRequest request) {
        TaskProcessDetectResponse response = new TaskProcessDetectResponse();
        response.setErrorCode(ActionErrorCode.SUCCESS);
        try {
            response.setProcessExists(taskProcessor.get(request.getTaskType()).isRunning(request.getTaskId()));
        } catch (Exception e) {
            response.setErrorCode(ActionErrorCode.UNKNOWN_ERROR);
            LOGGER.error("", e);
        }
        return response;
    }


    /**
     * Retrieves the progress as xml string with compose stream type transcoder task.
     * <p><b>Note: This method just for compose stream task to get progress.</b></p>
     *
     * @param request the GetTaskProgressRequest object
     * @return GetTaskProgressResponse
     */
    private GetTaskProgressResponse getTaskProgressResponse(GetTaskProgressRequest request) {
        GetTaskProgressResponse response = new GetTaskProgressResponse();
        response.setErrorCode(ActionErrorCode.SUCCESS);
        try {
            response.setXml(
                    (taskProcessor.get(TaskType.IP_STREAM_COMPOSE)).getProgressXml(request.getId())
            );
        } catch (Exception e) {
            response.setErrorCode(ActionErrorCode.UNKNOWN_ERROR);
            LOGGER.error("Failed to get task [id=" + request.getId() + "] progress", e);
        }
        return response;
    }


    /**
     * Retrieves the byte stream of image with rtsp type transcoder task.
     * <p><b>Note: This method just for rtsp type task to get thumbnail.</b></p>
     *
     * @param request the GetTaskThumbnailRequest object
     * @return GetTaskThumbnailResponse
     */
    private GetTaskThumbnailResponse getTaskThumbnailResponse(GetTaskThumbnailRequest request) {
        GetTaskThumbnailResponse response = new GetTaskThumbnailResponse();
        response.setErrorCode(ActionErrorCode.SUCCESS);
        try {
            response.setData(taskProcessor.get(TaskType.RTSP).getThumbnail(request.getId()));
        } catch (Exception e) {
            response.setErrorCode(ActionErrorCode.UNKNOWN_ERROR);
            LOGGER.error("Failed to get task [id=" + request.getId() + "] thumbnail", e);
        }
        return response;
    }

    /**
     * Show or hide warning border on screen.
     * <p><b>Note: This method only used by compose stream task.</b></p>
     *
     * @param request the ScreenWarningBorderRequest object
     * @return ScreenWarningBorderResponse
     */
    private ScreenWarningBorderResponse processWarningBorderRequest(ScreenWarningBorderRequest request) {
        ScreenWarningBorderResponse response = new ScreenWarningBorderResponse();
        response.setErrorCode(ActionErrorCode.SUCCESS);
        try {
            AbstractComposeStreamTaskProcessor composeStreamTaskProcessor = (AbstractComposeStreamTaskProcessor) taskProcessor
                    .get(TaskType.IP_STREAM_COMPOSE);
            composeStreamTaskProcessor.warnBorder(request.getTaskId(), request.getIndex(), request.isShow());
        } catch (Exception e) {
            response.setErrorCode(ActionErrorCode.UNKNOWN_ERROR);
            LOGGER.error("Failed to handle warning border with task [id=" + request.getTaskId() + "]", e);
        }
        return response;
    }

    /**
     * Display or hide message on screen.
     * <p/>
     * <p><b>Note: This method only used by compose stream task.</b></p>
     *
     * @param request the DisplayMessageRequest object
     * @return DisplayMessageResponse
     */
    private DisplayMessageResponse processDisplayMessageRequest(DisplayMessageRequest request) {
        DisplayMessageResponse response = new DisplayMessageResponse();
        response.setErrorCode(ActionErrorCode.SUCCESS);
        try {
            AbstractComposeStreamTaskProcessor composeStreamTaskProcessor = (AbstractComposeStreamTaskProcessor) taskProcessor
                    .get(TaskType.IP_STREAM_COMPOSE);
            composeStreamTaskProcessor.displayMessage(request.getComposeTaskId(), request.getTaskType(), request.getMessage());
        } catch (Exception e) {
            response.setErrorCode(ActionErrorCode.UNKNOWN_ERROR);
            LOGGER.error("Failed to handle {}", request);
        }
        return response;
    }

    private Response processDisplayStyledMessageRequest(DisplayStyledMessageRequest request) {
        DisplayStyledMessageResponse response = new DisplayStyledMessageResponse();
        response.setErrorCode(ActionErrorCode.SUCCESS);
        try {
            AbstractComposeStreamTaskProcessor composeStreamTaskProcessor = (AbstractComposeStreamTaskProcessor) taskProcessor.get(TaskType.IP_STREAM_COMPOSE);
            composeStreamTaskProcessor.displayStyledMessage(request.getTaskId(), request.getFont(), request.getSize(), request.getColor(), request.getAlpha(), 0, 0, request.getX(), request.getY(), request.getWidth(), request.getHeight(), request.getMessage());
        } catch(Exception e) {
            response.setErrorCode(ActionErrorCode.UNKNOWN_ERROR);
            LOGGER.error("Failed to handle DisplayStyledMessageRequest");
        }
        return response;
    }

    @Override
    public void receive(AbstractInfo data) {
        if (data != null && data instanceof CheckResultInfo) {
            LOGGER.info("Receive content detect result: " + data);
            CheckResultInfo checkResultInfo = (CheckResultInfo) data;
            ContentDetectResult result = new ContentDetectResult();
            BeanUtils.copyProperties(checkResultInfo, result);
            result.setGuid(checkResultInfo.getResultId());
            result.setChannelId(checkResultInfo.getChannel());
            try {
                contentDetectResultBlockingQueue.put(result);
            } catch (InterruptedException e) {
                LOGGER.error("Failed to report content detect result " + result);
            }
        }
    }

    @Override
    public void receivedTaskState(int taskId, int state) {
        // this method not used for now
    }

    @Override
    public void handleTaskStatusChanged(ITranscodingNotifier transcodingNotifier, com.arcsoft.supervisor.transcoder.type.TaskStatus status) {
        LOGGER.info("Receive task status change, [id={}, status={}]", transcodingNotifier.getTranscodingKey().getTaskId(), status);
        ITranscodingTracker tracker = (ITranscodingTracker) transcodingNotifier;
        clearStreamFileBeforeNotifyStatus(tracker, status);
        TaskStateChange stateChange = stateChangeFactory.create(
                transcodingNotifier.getTranscodingKey().getTaskId(),
                status,
                tracker
        );
        if (stateChange != null) {
            LOGGER.info("Send TaskStateChange {}", stateChange);
            publishStateChange(stateChange);
        }
    }

    private void clearStreamFileBeforeNotifyStatus(ITranscodingTracker transcodingNotifier, com.arcsoft.supervisor.transcoder.type.TaskStatus status) {
        if (status == com.arcsoft.supervisor.transcoder.type.TaskStatus.ERROR
                || status == com.arcsoft.supervisor.transcoder.type.TaskStatus.COMPLETED) {
            streamFileResourceManager.deleteStreamFile(transcodingNotifier);
        }
    }

    private void publishStateChange(int id, TaskStatus status, Integer pid) {
        publishStateChange(TaskStateChange.from(id, status, pid));
    }

    private void publishStateChange(TaskStateChange stateChange) {
        StateChangeRequest request = new StateChangeRequest();
        request.setStates(Collections.singletonList(stateChange));
        try {
            this.stateChangeRequestBlockingQueue.put(request);
        } catch (InterruptedException e) {
            LOGGER.error("Failed to publish StateChange request.", e);
        }
    }

    @Override
    public void fireTaskErrorMessage(ITranscodingNotifier transcodingNotifier, int level, int code, String msg) {
        if (isFilteredAlertMessage(level, code, msg)) {
            return;
        }
        TaskAlertContent taskAlertContent = new TaskAlertContent(transcodingNotifier.getTranscodingKey().getTaskId(),
                level, code, msg, agentService.getAgent().getNode().getDescription().getIp());
        LOGGER.info("Receive alert: " + taskAlertContent);
        try {
            this.alertContentBlockingQueue.put(taskAlertContent);
        } catch (InterruptedException e) {
            LOGGER.error("Failed to add task alert {} to queue.", taskAlertContent);
        }
    }

    private boolean isFilteredAlertMessage(int level, int code, String msg) {

        for (AlertMessageFilter filter : alertMessageFilters) {
            if (filter.doFilter(level, code, msg)) {
                return true;
            }
        }
        return false;
    }

    /**
     * A worker thread for send task state to commander.
     */
    private final class StateChangeWorker implements Runnable {

        @Override
        public void run() {
            while (true) {
                StateChangeRequest request;
                try {
                    request = stateChangeRequestBlockingQueue.take();
                } catch (InterruptedException e) {
                    LOGGER.error("", e);
                    break;
                }
                /**
                 * if the item is a stop flag then clear queue and exit worker.
                 */
                if (request instanceof StopStateChangeRequest) {
                    stateChangeRequestBlockingQueue.clear();
                    break;
                }
                try {
                    remoteExecutorService.remoteExecute(request);
                } catch (ActionException e) {
                    LOGGER.error("Failed to send StateChangeRequest " + request.getStates(), e);
                } catch (Exception e) {
                    LOGGER.error("Failed to send StateChangeRequest ", e);
                }
            }
            LOGGER.info("The StateChangeWorker exit.");
        }
    }

    /**
     * A worker thread for send content detect result to commander.
     */
    private final class ContentDetectResultReportWorker implements Runnable {

        @Override
        public void run() {
            while (true) {
                ContentDetectResult result;
                try {
                    result = contentDetectResultBlockingQueue.take();
                } catch (InterruptedException e) {
                    LOGGER.error("", e);
                    break;
                }
                /**
                 * if the item is a stop flag then clear queue and exit worker.
                 */
                if (result instanceof StopContentDetectResult) {
                    contentDetectResultBlockingQueue.clear();
                    break;
                }
                try {
                    remoteExecutorService.remoteExecute(new ContentDetectResultRequest(result));
                } catch (ActionException e) {
                    LOGGER.error("Failed to send ContentDetectResult [" + result + "]", e);
                }
            }
            LOGGER.info("The ContentDetectResultReportWorker exit.");
        }
    }

    /**
     * A worker thread to send alert of task to commander.
     */
    private final class TaskAlertWorker implements Runnable {

        @Override
        public void run() {
            while (true) {
                TaskAlertContent alertContent;
                try {
                    alertContent = alertContentBlockingQueue.take();
                } catch (InterruptedException e) {
                    LOGGER.error("", e);
                    break;
                }

                if (alertContent instanceof StopTaskAlertContent) {
                    alertContentBlockingQueue.clear();
                    break;
                }

                try {
                    remoteExecutorService.remoteExecute(new AlertRequest(alertContent));
                } catch (ActionException e) {
                    LOGGER.error("Failed to send task alert [" + alertContent + "]", e);
                }
            }
            LOGGER.info("The TaskAlertWorker exit.");
        }
    }

    /**
     * A child class of <code>StateChangeRequest</code> to indicate the <code>stateChangeRequestBlockingQueue</code>
     * need to stop.
     */
    private final class StopStateChangeRequest extends StateChangeRequest {
    }

    /**
     * A child class of <code>ContentDetectResult</code> to indicate the <code>contentDetectResultBlockingQueue</code>
     * need to stop.
     */
    private final class StopContentDetectResult extends ContentDetectResult {
    }

    /**
     * A child class to indicates the {@link #alertContentBlockingQueue} need to stop.
     */
    private final class StopTaskAlertContent extends TaskAlertContent {
    }


}
