package com.arcsoft.supervisor.web.api;


import com.arcsoft.supervisor.commons.json.JsonMapper;
import com.arcsoft.supervisor.commons.spring.SpringUtils;
import com.arcsoft.supervisor.exception.service.BusinessExceptionDescription;
import com.arcsoft.supervisor.exception.user.TokenExpireException;
import com.arcsoft.supervisor.exception.user.TokenNotExistException;
import com.arcsoft.supervisor.model.domain.channel.*;
import com.arcsoft.supervisor.model.domain.graphic.*;
import com.arcsoft.supervisor.model.domain.log.ContentDetectLog;
import com.arcsoft.supervisor.model.domain.server.OpsServer;
import com.arcsoft.supervisor.model.domain.server.Server;
import com.arcsoft.supervisor.model.domain.task.Task;
import com.arcsoft.supervisor.model.domain.task.TaskProfile;
import com.arcsoft.supervisor.model.domain.user.User;
import com.arcsoft.supervisor.model.dto.rest.screen.RootScreenBean;
import com.arcsoft.supervisor.model.dto.rest.screen.ScreenPreviewBean;
import com.arcsoft.supervisor.model.vo.task.TaskStatus;
import com.arcsoft.supervisor.model.vo.task.TaskType;
import com.arcsoft.supervisor.model.vo.task.profile.TaskProfileDto;
import com.arcsoft.supervisor.service.channel.ChannelService;
import com.arcsoft.supervisor.service.comparator.Comparator;
import com.arcsoft.supervisor.service.graphic.ScreenService;
import com.arcsoft.supervisor.service.profile.TaskProfileService;
import com.arcsoft.supervisor.service.server.OpsServerOperator;
import com.arcsoft.supervisor.service.server.ServerService;
import com.arcsoft.supervisor.service.settings.RtspConfigurationService;
import com.arcsoft.supervisor.service.task.TaskDispatcherFacade;
import com.arcsoft.supervisor.service.task.TaskService;
import com.arcsoft.supervisor.service.user.UserService;
import com.arcsoft.supervisor.utils.app.Environment;
import com.arcsoft.supervisor.web.JsonResult;
import com.arcsoft.supervisor.web.mosic.MosicChannelBean;
import com.arcsoft.supervisor.web.mosic.MosicScreenBean;
import com.arcsoft.supervisor.web.mosic.SchemaPosChannel;
import com.arcsoft.supervisor.web.screen.ScreenTaskBean;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import freemarker.template.TemplateException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.arcsoft.supervisor.exception.service.BusinessExceptionDescription.*;
import static com.arcsoft.supervisor.web.api.ApiErrorCode.*;
import static com.arcsoft.supervisor.web.api.ControllerUtils.*;

/**
 * Controller class for rest api of module {@code screen}.
 *
 * @author zw.
 */
@Api(value = "多画接口", description = "设置多画面监控频道信息")
@Controller
public class ScreenApiController extends RestApiControllerSupport {

    @Autowired
    private ScreenService screenService;
    @Autowired
    private TaskDispatcherFacade taskDispatcherFacade;
    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskProfileService taskProfileService;

    @Autowired
    @Qualifier("rootScreenBeanComparator")
    private Comparator<Integer, RootScreenBean> rootScreenBeanComparator;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private OpsServerOperator<OpsServer> opsServerOperator;

    @Autowired
    private RtspConfigurationService rtspConfigurationService;

    @Autowired
    private UserService<User> userService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private ServerService serverService;

    private final Object lock = new Object();

    private static final int MAX_WAIT_OUTPUT_TIMES = 20;

    @RequestMapping(value = "/getscreen_app", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getScreen(Integer id, String token) throws IOException, TemplateException {

        if (id == null || StringUtils.isBlank(token)) {
            return renderEmptyResponse();
        }

        Screen screen = screenService.getById(id);
        if (screen == null) {
            return renderResponseCodeJson(SCREEN_NOT_EXISTS);
        }
        Map<String, Object> model = new HashMap<>();
        model.put("statusCode", OK.getCode());
        model.put("screen", screen);
        return freemarkerService.renderFromTemplateFile("screen.ftl", model);
    }

    /**
     * A class holds the result of {@link #doUpdateScreens(String)}.
     */
    private class UpdateScreenHolder {

        private final RootScreenBean screenBean;

        private final boolean needRestart;

        public UpdateScreenHolder(RootScreenBean screenBean, boolean needRestart) {
            this.screenBean = screenBean;
            this.needRestart = needRestart;
        }

        public RootScreenBean getScreenBean() {
            return screenBean;
        }

        public boolean isNeedRestart() {
            return needRestart;
        }
    }

    /**
     * Update the given {@code screenJson} and restart the task of screen if need.
     *
     * @param screenJson the json string represented screen object
     * @return the result contains the {@link RootScreenBean} and the {@code boolean}
     * value to indicates the task need restart or not
     */
    private UpdateScreenHolder doUpdateScreens(String screenJson) {
        if (StringUtils.isBlank(screenJson)) {
            throw INVALID_ARGUMENTS.exception();
        }
        RootScreenBean rootScreenBean;
        try {
            rootScreenBean = JsonMapper.getMapper().readValue(screenJson, RootScreenBean.class);
        } catch (IOException e) {
            throw CONVERT_INPUT_ARGUMENTS_FAILED.withException(e);
        }

        if (rootScreenBean == null || rootScreenBean.getScreenBeans().isEmpty()) {
            throw INVALID_ARGUMENTS.exception();
        }

        boolean needRestart = checkTaskAndUpdateScreens(rootScreenBean);
        if (needRestart && !rootScreenBean.getScreenBeans().isEmpty()) {
            taskDispatcherFacade.restartScreenTask(rootScreenBean.getScreenBeans().get(0).getId(), rootScreenBean.getProfileId());
        }
        return new UpdateScreenHolder(rootScreenBean, needRestart);
    }


    @RequestMapping(value = "/setscreen_app", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String updateScreens(@RequestBody String updateScreenJson) throws IOException {
        doUpdateScreens(updateScreenJson);
        return renderResponseCodeJson(OK);
    }

    @RequestMapping(value = "/getscreen_used_profile_app", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Map<String, Object> getScreenUsedProfile(Integer id, String token) {
        Integer profileId = taskService.getUsedTaskProfileIdByScreenId(id);
        HashMap<String, Object> result = new HashMap<>();
        result.put("profile", profileId == null ? -1 : profileId);
        return result;
    }

    @RequestMapping(value = "/getprofile_app", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public List<TaskProfile> getProfile(String token) {
        return taskProfileService.findAllProfile();
    }


    @RequestMapping(value = "/preview_app", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String previewScreen(@RequestBody String previewScreenJson) throws JsonProcessingException {
        UpdateScreenHolder holder = doUpdateScreens(previewScreenJson);
        int screenId = holder.getScreenBean().getScreenBeans().get(0).getId();
        if (holder.isNeedRestart()) {
            waitForStreamOutput(screenId);
        }
        ScreenPreviewBean previewBean = screenService.getScreenPreviewBeanByScreen(screenId);
        return JsonMapper.getMapper().writeValueAsString(previewBean);
    }

    @ApiOperation(value = "多画启动", notes = "根据传入的json启动画面")
    @RequestMapping(method = RequestMethod.POST, value = "/startScreen_app", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Map<String, Object> startScreen(HttpServletRequest request, @RequestBody String jsonString) {
        ScreenTaskBean screenTaskBean = getBeanFromString(jsonString);
        List<ChannelConfig> channels = screenTaskBean.getChannels();
        //screen id is exits

        if (screenService.getById(screenTaskBean.getScreenid()) != null) {
            //update channel and location "posIdx": 0,
            for (int i = 0; i < channels.size(); i++) {
                final ChannelConfig finalChannel = channels.get(i);
                ChannelConfig NewChannelConfig = channels.get(i);
                NewChannelDesc newChannel = NewChannelConfig.getChannel();
                Integer channelId = 0;
                if (finalChannel.getChannel().getChannelid() != null) {
                    Channel channel = channelService.getById(finalChannel.getChannel().getChannelid());
                    if (channel != null) {
                        channelId = channel.getId();
                    } else {
                        return createModelMap(API_CHANNEL_FINDBYID_NOT_EXISTS, "channel find by id is not  Exist(posIdx= %d)", finalChannel.getPosIdx());
                    }

                } else {
                    //判断频道是否存在
                   /* if (channelService.isChannelNameExists(null, finalChannel.getChannel().getName())) {

                        //return createModelMap(API_CHANNEL_NAME_ISEXISTS, "channel name  is Exist(posIdx= %d)", finalChannel.getPosIdx());
                    }*/
                    if ("-1".equals(finalChannel.getChannel().getAddress())) {
                        continue;
                    } else {
                        channelId = transactionTemplate.execute(new TransactionCallback<Integer>() {
                            @Override
                            public Integer doInTransaction(TransactionStatus status) {
                                Channel channel = channelService.fromChannelDesc(finalChannel.getChannel());
                                return channel.getId();
                            }
                        });

                    }

                }

                newChannel.setChannelid(channelId);
                NewChannelConfig.setChannel(newChannel);
                channels.set(i, NewChannelConfig);
            }
            //  resetTaskStaus(screenTaskBean.getScreenid());
            excuteUpdate(screenTaskBean);
            return createSuccessMap();
        } else {
            return createModelMap(API_SCREEN_NOT_EXISTS, "screen is not  Exist");
        }

    }


    private void resetTaskStaus(Integer screenId) {
        if (screenId == null)
            return;
        final Integer screen_id = screenId;
        transactionTemplate.execute(new TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus status) {
                taskService.resetTaskStatusByRefid(screen_id);
                return null;
            }
        });
    }

    private UserScreenLayout excuteUpdate(ScreenTaskBean screenBean) {
        checkParams(screenBean, false);

        UpdateScreenSchemaResult result = doUpdateScreenschame(screenBean);
        taskDispatcherFacade.reloadOrStartScreenTask(screenBean.getScreenid(), result.getProfileId(), result.getServerId(), result.getGpuStartIndex());
        //syncScreenchangeToTargets(api, screenBean);

        return result.getLayout();
    }

    private void checkParams(ScreenTaskBean screenBean, boolean checkToken) {
        if (checkToken) {
            User user = null;
            try {
                user = userService.getUserByToken(screenBean.getToken());
            } catch (TokenExpireException e) {
                throw BusinessExceptionDescription.MOSAIC_TOKEN_EXPIRED.exception();
            } catch (TokenNotExistException e) {
                throw BusinessExceptionDescription.MOSAIC_INVALID_TOKEN.exception();
            }

            if (user == null) {
                throw BusinessExceptionDescription.MOSAIC_INVALID_TOKEN.exception();
            }
        }
        BusinessExceptionDescription code = OK;
        ChannelConfig temp = screenBean.getChannels().get(0);
        if (temp.getPosition() != null) {
            Rectangle screen = new Rectangle(0, 0, screenBean.getWidth(), screenBean.getHeight());
            List<ChannelConfig> channelBeans = screenBean.getChannels();
            for (ChannelConfig channel : channelBeans) {
                if (channel.getPosition() == null) {
                    code = MOSAIC_INVALID_POSITION;
                    break;
                }
                if (!screen.contains(channel.getPosition().getRectangle())) {
                    logger.info("pos out of range:" + channel.getPosition().toString());
                    code = MOSAIC_POSITION_OUTRANGE;
                    break;
                }

                if (channelService.getById(channel.getChannel().getChannelid()) == null) {
                    logger.info("Invalid channel id:" + channel.getChannel().getChannelid());
                    code = MOSAIC_INVALID_CHANNEL_ID;
                    break;
                }
            }

            if (code == OK) {//check intersection
                int nCount = channelBeans.size();
                for (int i = 0; i < nCount; i++) {
                    boolean intersect = false;
                    Rectangle chanRec = channelBeans.get(i).getPosition().getRectangle();
                    for (int j = i + 1; j < nCount; j++) {
                        Rectangle chanRecTemp = channelBeans.get(j).getPosition().getRectangle();
                        if (chanRecTemp.intersects(chanRec)) {
                            logger.info("channel pos intersect:" + channelBeans.get(i).getPosition().toString() + channelBeans.get(j).getPosition().toString());
                            intersect = true;
                            break;
                        }
                    }
                    if (intersect) {
                        code = MOSAIC_POSITION_INTERSECT;
                        break;
                    }
                }
            }
        } else {
            for (ChannelConfig channel : screenBean.getChannels()) {
                if (channel.getPosIdx() == null) {
                    code = MOSAIC_INVALID_POSITION;
                    break;
                }
            }
        }
        if (code != OK)
            throw code.exception();

    }

    private ScreenTaskBean getBeanFromString(String json) {
        try {
            return JsonMapper.getMapper().readValue(json, ScreenTaskBean.class);
        } catch (Exception e) {
            throw BusinessExceptionDescription.MOSAIC_INVALID_PARAM.exception();
        }
    }

    private String getStringFromBean(ScreenTaskBean screenTaskBean) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            return mapper.writeValueAsString(screenTaskBean);
        } catch (IOException e) {
            logger.info(e.getMessage());
        }

        return null;
    }

    @ApiOperation(value = "多画停止", notes = "根据id和token停止画面")
    @RequestMapping(value = "/stopScreen_app", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public JsonResult stopScreen(@ApiParam(required = true, name = "id", value = "频道id") @RequestParam(value = "id") final Integer id,
                                 @ApiParam(required = true, name = "token", value = "token") @RequestParam(value = "token") String token) {
        if (id == null || StringUtils.isBlank(token)) {
            return JsonResult.from(INVALID_ARGUMENTS.getCode());
        }
        /*
        try{
            User user = userService.getUserByToken(token);
            if(user == null) {
                return JsonResult.from(INVALID_USER_TOKEN.getCode());
            }
        }catch (Exception e){
            return JsonResult.from(INVALID_USER_TOKEN.getCode());
        }*/

        try {
            transactionTemplate.execute(new TransactionCallback<Void>() {

                @Override
                public Void doInTransaction(TransactionStatus status) {
                    Screen screen = screenService.getById(id.intValue());
                    if (screen != null && screen.getWallPosition() != null && screen.getWallPosition().getOpsServer() != null) {
                        OpsServer server = screen.getWallPosition().getOpsServer();
                        server.getId(); //trigger lazying loading
                        opsServerOperator.stop(server);
                    }
                    return null;
                }
            });
            taskDispatcherFacade.stopScreenTask(id);
            return JsonResult.fromSuccess();
        } catch (Exception e) {

        }
        return JsonResult.fromError();
    }

    @RequestMapping(value = "/setScreen_ops_url_app", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Map<String, Object> setScreenOpsUrl(Integer screen_id, Integer chan_id, Integer type, String token) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        if (screen_id == null || StringUtils.isBlank(token) || type == null) {
            result.put("code", INVALID_ARGUMENTS.getCode());
            result.put("message", INVALID_ARGUMENTS);
            return result;
        }
        /*
        User user=null;
        try{
            user = userService.getUserByToken(token);
        }
        catch (Exception e){
            user = null;
        }
        if(user == null){
            result.put("code", INVALID_USER_TOKEN.getCode());
            result.put("message",INVALID_USER_TOKEN);
            return result;
        }
        */
        Screen screen = null;
        try {
            screen = screenService.getById(screen_id);
        } catch (Exception e) {
            screen = null;
        }
        if (type == 1 && screen == null) {
            result.put("code", SCREEN_NOT_EXISTS.getCode());
            result.put("message", SCREEN_NOT_EXISTS);
            return result;
        }

        OpsServer opsServer = null;
        try {
            opsServer = screen.getWallPosition().getOpsServer();
        } catch (Exception e) {
            opsServer = null;
        }
        if (opsServer == null) {
            result.put("code", SCREEN_NOT_BIND_OPS.getCode());
            result.put("message", SCREEN_NOT_BIND_OPS);
            return result;
        }
        String targetIp = null;
        try {
            targetIp = screen.getWallPosition().getOpsServer().getIp();
        } catch (Exception e) {
            targetIp = null;
        }

        if (targetIp == null) {
            result.put("code", SCREEN_NOT_BIND_OPS.getCode());
            result.put("message", SCREEN_NOT_BIND_OPS);
            return result;
        }

        Task screenTask = null;
        if (type == 1) {
            try {
                screenTask = taskService.getScreenTask(screen_id);
            } catch (Exception e) {
                screenTask = null;
            }

            if (screenTask == null) {
                result.put("code", TASK_NOT_RUNNING.getCode());
                result.put("message", TASK_NOT_RUNNING);
                return result;
            }

            if (screenTask.isStopped()) {
                result.put("code", TASK_NOT_RUNNING.getCode());
                result.put("message", TASK_NOT_RUNNING);

                return result;
            }
        }

        Server server = null;
        try {
            if (screenTask != null)
                server = serverService.getServer(screenTask.getServerId());
        } catch (Exception e) {
            server = null;
        }

        String url = "";
        if (type == 0) {//channel url
            Channel channel = null;
            try {
                if (chan_id != null)
                    channel = channelService.getById(chan_id);
            } catch (Exception e) {

            }

            if (channel == null) {
                result.put("code", CHANNEL_NOT_EXIST.getCode());
                result.put("message", CHANNEL_NOT_EXIST);

                return result;
            }

            if (!channel.getIsSupportMobile()) {
                result.put("code", CHANNEL_NOT_SUPPORT_MOBILE.getCode());
                result.put("message", CHANNEL_NOT_SUPPORT_MOBILE);

                return result;
            }

            String rtmp = null;
            List<ChannelMobileConfig> configs = channel.getMobileConfigs();
            for (ChannelMobileConfig config : configs) {
                if (config.getType() == 1) {
                    rtmp = config.getAddress();
                    if (rtmp != null && !StringUtils.isBlank(rtmp))
                        break;
                } else
                    rtmp = config.getAddress();
            }

            if (rtmp != null) {
                url = rtspConfigurationService.composeUrl(rtmp, targetIp);
            }
        } else {//compose url
            url = screen.getAddress();
            if (url == null) {
                String rtmp = screen.getRtspFileName();
                if (rtmp != null) {
                    url = rtspConfigurationService.composeUrl(rtmp, targetIp);
                }
            }
        }

        if (url != null && !StringUtils.isBlank(url)) {
            opsServerOperator.start(opsServer, url, server != null ? server.getIp() : null);
        }

        if (url != null && !StringUtils.isBlank(url)) {
            result.put("code", OK.getCode());
            result.put("url", url);
        } else {
            result.put("code", TASK_NOT_RUNNING.getCode());
            result.put("url", "");
        }

        return result;
    }

    private void waitForStreamOutput(int screenId) {
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
        }
        int times = 0;
        while (MAX_WAIT_OUTPUT_TIMES > times
                && !taskService.isIPStreamComposeTaskHasOutput(screenId)) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {

            }
            times++;
        }
    }

    @RequestMapping(value = "/getscreen_status_app", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public JsonResult getScreenTaskstatus(Integer id) {
        Task task = null;
        try {
            task = taskService.getScreenTask(id);
        } catch (Exception e) {
            task = null;
        }

        if (task == null)
            return JsonResult.from(TASK_NOT_EXIST.getCode());

        return JsonResult.fromSuccess().put("task", task);
    }

    @RequestMapping(value = "/getscreen_url_app", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getScreenUrl(Integer id) throws JsonProcessingException {
        ScreenPreviewBean previewBean = screenService.getScreenPreviewBeanByScreen(id);
        return JsonMapper.getMapper().writeValueAsString(previewBean);
    }

    @RequestMapping(value = "/getscreen_url2_app", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Map<String, Object> getScreenUrl2(Integer id) {
        HashMap<String, Object> result = new HashMap<>();
        try {
            Screen screen = null;
            try {
                screen = screenService.getById(id);
            } catch (Exception e) {
                screen = null;
            }

            if (screen != null) {

                String output = screen.getWallPosition().getOutput();
                if (!StringUtils.isEmpty(output) && output.startsWith("rtmp")) {
                    result.put("code", OK.getCode());
                    result.put("url", output);

                    return result;
                }
                //check the screen task status first
                Task screenTask = null;
                try {
                    screenTask = taskService.getScreenTask(id);
                } catch (Exception e) {
                    screenTask = null;
                }

                if (screenTask == null || screenTask.isStopped()) {
                    result.put("code", TASK_NOT_RUNNING.getCode());

                    return result;
                }

                boolean bMobileSupport = false;
                String rtsp = screen.getRtspFileName();
                if (rtsp != null) {
                    result.put("code", OK.getCode());
                    String url = rtspConfigurationService.composeUrl(rtsp, SpringUtils.getThreadBoundedHttpServletRequest().getRemoteAddr());
                    result.put("url", url);
                    bMobileSupport = true;
                } else {
                    result.put("code", SCREEN_NOTCONFIG_MOBILE.getCode());//no task for this screen
                    bMobileSupport = false;
                }
                String opsurl = screen.getAddress();
                if (opsurl != null) {
                    if (!bMobileSupport && (opsurl.startsWith("rtmp://") || opsurl.startsWith("rtsp://"))) {
                        result.put("code", OK.getCode());
                        result.put("url", opsurl);
                    }
                }
            } else {
                result.put("code", SCREEN_NOT_EXISTS.getCode());//id is not exist
            }
        } catch (Exception e) {
            result.clear();
            result.put("code", ERROR.getCode());
        }

        return result;
    }

    /**
     * Checks the <code>rootScreenBean</code> is same as the persist screen in the database or
     * not.if it is same then we do not need update it otherwise we need persist the <code>rootScreenBean</code>.
     * <p>if <code>rootScreenBean</code> is same as persist screen then we need confirm the status of
     * the screen id representations task and if the status is not running then we need start it.</p>
     * <p/>
     * <p>if <code>rootScreenBean</code> is not same as persist screen then we need restart the task</p>
     *
     * @param rootScreenBean the bean contains screen data
     * @return <code>true</code> indicates the screen representations task need restart.
     */
    private boolean checkTaskAndUpdateScreens(RootScreenBean rootScreenBean) {
        int screenId = rootScreenBean.getScreenBeans().get(0).getId();
        synchronized (lock) {
            boolean isEqualed = rootScreenBeanComparator.compare(screenId, rootScreenBean);
            boolean needRestartTask = true;
            if (isEqualed) { //if not changed then we just need confirm the status of task
                Task task = taskService.getByTypeAndReferenceId(screenId, TaskType.IP_STREAM_COMPOSE);
                needRestartTask = (task == null || !task.isStatusEqual(TaskStatus.RUNNING));
            } else {
                screenService.updateWith(rootScreenBean);
            }
            return needRestartTask;
        }
    }

    private UpdateScreenSchemaResult doUpdateScreenschame(final ScreenTaskBean ScreenTaskBean) {
        return transactionTemplate.execute(new TransactionCallback<UpdateScreenSchemaResult>() {

            @Override
            public UpdateScreenSchemaResult doInTransaction(TransactionStatus status) {

                UpdateScreenSchemaResult result = new UpdateScreenSchemaResult();

                Integer screenId = ScreenTaskBean.getScreenid();
                Screen screen = null;
                try {
                    screen = screenService.getById(screenId);
                } catch (Exception e) {
                    screen = null;
                }

                if (screen == null) {
                    throw BusinessExceptionDescription.MOSAIC_SCREEN_NOT_EXIST.exception();
                }

                ScreenSchema schema = screen.getActiveSchema();
                if (schema == null) {
                    throw BusinessExceptionDescription.MOSAIC_SCREEN_NOT_INITIALIZED.exception();
                }

                //  Integer profileId = taskService.getUsedTaskProfileIdByScreenId(screenId);
                Task task = taskService.getScreenTask(screenId);
                try {
                    task = taskService.getScreenTask(screenId);
                } catch (Exception e) {
                    task = null;
                }

                if (task == null) {
                    throw BusinessExceptionDescription.MOSAIC_SCREEN_NOT_INITIALIZED.exception();
                }


                List<ScreenPosition> screenPositions = schema.getScreenPositions();
                int nMatchCount = 0;
                List<ChannelConfig> Channels = ScreenTaskBean.getChannels();
                if (screenPositions.size() > Channels.size())
                    nMatchCount = Channels.size();
                else
                    nMatchCount = screenPositions.size();

                if (ScreenTaskBean.getChannels().get(0).getPosIdx() != null) {
                    //update channel information first
                    List<SchemaPosChannel> Positions = new ArrayList<SchemaPosChannel>();
                    for (int i = 0; i < nMatchCount; i++) {
                        ChannelConfig newpos = Channels.get(i);
                        try {
                            if ("-1".equals(newpos.getChannel().getAddress())) {
                                Positions.add(new SchemaPosChannel(newpos.getPosIdx(), null));
                            } else {
                                Positions.add(new SchemaPosChannel(newpos.getPosIdx(), channelService.getById(newpos.getChannel().getChannelid())));
                            }

                        } catch (Exception e) {
                            logger.info("update channel failed:" + newpos);
                        }
                    }

                    //we should clear the empty channel here,yshe

                    screenService.updateScreenPositionChannels(schema.getId(), Positions);
                    screenService.updateUserLayout(screenId, null);
                } else {
                    UserScreenLayout userScreenLayout = new UserScreenLayout();
                    userScreenLayout.setScreenid(screenId);
                    userScreenLayout.setWidth(ScreenTaskBean.getWidth());
                    userScreenLayout.setHeight(ScreenTaskBean.getHeight());
                    userScreenLayout.setBackground(ScreenTaskBean.getForeground());

                    List<UserChannelDesc> userChannelDescList = new ArrayList<UserChannelDesc>();
                    List<SchemaPosChannel> Positions = new ArrayList<SchemaPosChannel>();
                    List<ChannelConfig> channelBeens = ScreenTaskBean.getChannels();
                    for (int i = 0; i < nMatchCount; i++) {
                        try {
                            ChannelConfig channelBean = channelBeens.get(i);
                            UserChannelDesc userChannelDesc = new UserChannelDesc();
                            userChannelDesc.setPosition(channelBean.getPosition());

                            Channel channel = channelService.getById(channelBean.getChannel().getChannelid());
                            if ("-1".equals(channel.getAddress())) {
                                userChannelDescList.add(userChannelDesc);
                                Positions.add(new SchemaPosChannel(i, null));
                            } else {
                                userChannelDesc.setChannelid(channel.getId());
                                userChannelDesc.setChannelname(channel.getName());
                                userChannelDesc.setCd(channelBean.getChannel().getCd());
                                userChannelDesc.setSd(channelBean.getChannel().getSd());
                                userChannelDescList.add(userChannelDesc);
                                Positions.add(new SchemaPosChannel(i, channelService.getById(channelBean.getChannel().getChannelid())));
                            }

                        } catch (Exception e) {
                            logger.info("update channel failed:");
                        }

                    }
                    userScreenLayout.setChannels(userChannelDescList);

                    if (!screenService.updateUserLayout(screenId, userScreenLayout)) {
                        throw BusinessExceptionDescription.MOSAIC_UPDATE_LAYOUT_FAILED.exception();
                    }
                    //we should clear the empty channel here,yshe
                    for (int i = nMatchCount; i < screenPositions.size(); i++) {
                        Positions.add(new SchemaPosChannel(i, null));
                    }
                    screenService.updateScreenPositionChannels(schema.getId(), Positions);

                    result.setLayout(userScreenLayout);
                }

                result.setServerId(task.getServerId());
                result.setProfileId(task.getProfile().getId());
                result.setGpuStartIndex(task.getGpudIndex());
                return result;
            }
        });
    }

    class UpdateScreenSchemaResult {
        private String ServerId;
        private Integer ProfileId;
        private Integer gpuStartIndex;
        private UserScreenLayout layout;

        public UpdateScreenSchemaResult() {
            ServerId = "";
            ProfileId = -1;
            gpuStartIndex = -1;
        }

        public String getServerId() {
            return ServerId;
        }

        public void setServerId(String serverId) {
            this.ServerId = serverId;
        }

        public Integer getProfileId() {
            return ProfileId;
        }

        public void setProfileId(Integer profileId) {
            this.ProfileId = profileId;
        }

        public Integer getGpuStartIndex() {
            return gpuStartIndex;
        }

        public void setGpuStartIndex(Integer gpuStartIndex) {
            this.gpuStartIndex = gpuStartIndex;
        }

        public UserScreenLayout getLayout() {
            return layout;
        }

        public void setLayout(UserScreenLayout layout) {
            this.layout = layout;
        }
    }

}
