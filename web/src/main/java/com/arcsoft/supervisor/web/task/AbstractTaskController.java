package com.arcsoft.supervisor.web.task;

import com.arcsoft.supervisor.commons.spring.SpringUtils;
import com.arcsoft.supervisor.model.domain.graphic.Screen;
import com.arcsoft.supervisor.model.domain.server.OpsServer;
import com.arcsoft.supervisor.model.domain.system.TranscoderTemplate;
import com.arcsoft.supervisor.model.domain.task.Task;
import com.arcsoft.supervisor.model.vo.task.TaskStatus;
import com.arcsoft.supervisor.service.graphic.ScreenService;
import com.arcsoft.supervisor.service.server.OpsServerOperator;
import com.arcsoft.supervisor.service.settings.RtspConfigurationService;
import com.arcsoft.supervisor.service.task.TaskDispatcherFacade;
import com.arcsoft.supervisor.service.task.TaskService;
import com.arcsoft.supervisor.web.ControllerSupport;
import com.arcsoft.supervisor.web.JsonResult;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static com.arcsoft.supervisor.exception.service.BusinessExceptionDescription.ERROR;
import static com.arcsoft.supervisor.exception.service.BusinessExceptionDescription.OK;

/**
 * @author zw.
 */
@RequestMapping("/task")
public abstract class AbstractTaskController extends ControllerSupport {

    public static final String KEY_OF_SCREEN_URL = "url";
    public static final String KEY_OF_MOBILE_URL = "mobile";

    private final TaskDispatcherFacade taskDispatcherFacade;
    private final ScreenService screenService;
    private final TaskService taskService;

    @Autowired
    private OpsServerOperator<OpsServer> opsServerOperator;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private RtspConfigurationService rtspConfigurationService;


    protected AbstractTaskController(
            TaskDispatcherFacade taskDispatcherFacade,
            ScreenService screenService,
            TaskService taskService) {
        this.taskDispatcherFacade = taskDispatcherFacade;
        this.screenService = screenService;
        this.taskService = taskService;
    }

    public TaskDispatcherFacade getTaskDispatcherFacade() {
        return taskDispatcherFacade;
    }

    public ScreenService getScreenService() {
        return screenService;
    }

    public TaskService getTaskService() {
        return taskService;
    }

    @RequestMapping(value = "/screen/control/start", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> startByScreenId(final Integer screenId) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        try {
            Task task = taskService.getScreenTask(screenId);
            Integer profileId = transactionTemplate.execute(new TransactionCallback<Integer>() {
                @Override
                public Integer doInTransaction(TransactionStatus status) {
                    Task task = taskService.getScreenTask(screenId);
                    if (task != null) {
                        return task.getProfile().getId();
                    }
                    return null;
                }
            });

            if (task != null) {
                start(screenId, profileId, task.getServerId(), task.getGpudIndex());
                result.put("code", OK.getCode());
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.put("code", ERROR.getCode());
        return result;
    }

    @RequestMapping(value = "/screen/start", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> start(Integer screenId, Integer taskProfileId, String serverId, Integer gpuIndex) {
        final Integer screen_id = screenId;
        transactionTemplate.execute(new TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus status) {
                taskService.resetTaskStatusByRefid(screen_id);
                return null;
            }
        });
        taskDispatcherFacade.startScreenTask(
                screenId,
                taskProfileId,
                StringUtils.isEmpty(serverId) || serverId.startsWith("-") ? null : serverId,
                gpuIndex
        );

        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("code", OK.getCode());
        Screen screen = screenService.getById(screenId);
        final Integer screenTranscodeId = screen.getId();
        String OutPutString = transactionTemplate.execute(new TransactionCallback<String>() {
            @Override
            public String doInTransaction(TransactionStatus status) {
                Screen screen = screenService.getById(screenTranscodeId);
                return screen.getWallPosition().getOutput();
            }
        });
        if (!StringUtils.isEmpty(OutPutString) && OutPutString.startsWith("rtmp")) {
            result.put(KEY_OF_MOBILE_URL, OutPutString);
            result.put(KEY_OF_SCREEN_URL, "");

        } else {
            if (screen.getRtspFileName() != null) {
                result.put(KEY_OF_MOBILE_URL, rtspConfigurationService.composeUrl(screen.getRtspFileName(), SpringUtils.getThreadBoundedHttpServletRequest().getRemoteAddr()));
            }
            if (OutPutString==null || !"http".startsWith(OutPutString)) {
                result.put(KEY_OF_SCREEN_URL, screenService.getById(screenId).getAddress());
            } else {
                result.put(KEY_OF_SCREEN_URL, "");
            }
        }

        return result;
    }

    @RequestMapping(value = "/screen/control/stop", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> stopByScreenId(Integer screenId) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        try {
            stop(screenId);
            result.put("code", OK.getCode());
            return result;
        } catch (Exception e) {
            result.put("code", ERROR.getCode());
            return result;
        }

    }

    @RequestMapping(value = "/screen/stop", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult stop(final int screenId) {
        transactionTemplate.execute(new TransactionCallback<Void>() {

            @Override
            public Void doInTransaction(TransactionStatus status) {
                Screen screen = screenService.getById(screenId);
                if (screen != null && screen.getWallPosition() != null && screen.getWallPosition().getOpsServer() != null) {
                    OpsServer server = screen.getWallPosition().getOpsServer();
                    server.getId(); //trigger lazying loading
                    opsServerOperator.stop(server);
                }
                return null;
            }
        });
        taskDispatcherFacade.stopScreenTask(screenId);
        return JsonResult.fromSuccess();
    }

    @RequestMapping(value = "/channel", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult startOrStopChannel(Integer channelId) {
        taskDispatcherFacade.restartChannelTask(channelId);
        return JsonResult.fromSuccess();
    }

    @RequestMapping(value = "/getChannelTasks", method = RequestMethod.GET)
    @ResponseBody
    public JsonResult getChannelTasks(@RequestParam(value = "ids[]") Integer[] channelIds) {
        return JsonResult.fromSuccess().put(KEY_OF_RESULT, taskService.getChannelTasksByChannelIds(
                Lists.newArrayList(channelIds)));
    }

    @RequestMapping(value = "/getScreenTask", method = RequestMethod.GET)
    @ResponseBody
    public JsonResult getScreenTask(Integer screenId) {
        return JsonResult.fromSuccess().put(KEY_OF_RESULT, taskService.getScreenTask(screenId));
    }

    @RequestMapping(value = "/xml/{taskId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public String getTranscoderXml(@PathVariable Integer taskId) {
        return getTaskService().getTranscoderXml(taskId);
    }
}
