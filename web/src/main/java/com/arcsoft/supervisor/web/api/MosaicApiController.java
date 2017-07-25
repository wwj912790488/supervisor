package com.arcsoft.supervisor.web.api;

import com.arcsoft.supervisor.commons.HttpClientUtils;
import com.arcsoft.supervisor.commons.json.JsonMapper;
import com.arcsoft.supervisor.exception.service.BusinessException;
import com.arcsoft.supervisor.exception.service.BusinessExceptionDescription;
import com.arcsoft.supervisor.exception.user.TokenExpireException;
import com.arcsoft.supervisor.exception.user.TokenNotExistException;
import com.arcsoft.supervisor.model.domain.channel.Channel;
import com.arcsoft.supervisor.model.domain.graphic.*;
import com.arcsoft.supervisor.model.domain.master.Master;
import com.arcsoft.supervisor.model.domain.server.OpsServer;
import com.arcsoft.supervisor.model.domain.task.Task;
import com.arcsoft.supervisor.model.domain.task.TaskProfile;
import com.arcsoft.supervisor.model.domain.user.User;
import com.arcsoft.supervisor.model.vo.task.profile.TaskProfileDto;
import com.arcsoft.supervisor.repository.master.MasterRepository;
import com.arcsoft.supervisor.service.channel.ChannelService;
import com.arcsoft.supervisor.service.converter.Converter;
import com.arcsoft.supervisor.service.graphic.ScreenService;
import com.arcsoft.supervisor.service.task.TaskDispatcherFacade;
import com.arcsoft.supervisor.service.task.TaskService;
import com.arcsoft.supervisor.service.user.UserService;
import com.arcsoft.supervisor.utils.FileUtils;
import com.arcsoft.supervisor.utils.NetworkHelper;
import com.arcsoft.supervisor.utils.app.Environment;
import com.arcsoft.supervisor.web.mosic.MosicChannelBean;
import com.arcsoft.supervisor.web.mosic.MosicScreenBean;
import com.arcsoft.supervisor.web.mosic.SchemaPosChannel;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.template.TemplateException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static com.arcsoft.supervisor.exception.service.BusinessExceptionDescription.*;

/**
 * Created by yshe on 2016/6/15.
 */
@Api(value = "马赛克接口", description = "设置马赛克面监控频道信息")
@Controller
public class MosaicApiController extends RestApiControllerSupport {

    @Autowired
    private UserService<User> userService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ScreenService screenService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private TaskDispatcherFacade taskDispatcherFacade;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    protected Converter<TaskProfileDto, TaskProfile> taskProfileConverter;

    @Autowired
    MasterRepository masterDao;


    @RequestMapping(value = "/remotelogin", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String login(@RequestParam(value = "username", required = false) String username, String password) throws
            IOException, TemplateException {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            throw INVALID_ARGUMENTS.exception();
        }

        User user = userService.login(username, password,true);
        if (user == null) {
            return renderResponseCodeJson(BusinessExceptionDescription.USER_LOGIN_NAME_OR_PASSWORD_INCORRECT);
        }

        Map<String, Object> model = new HashMap<>();
        model.put("statusCode", BusinessExceptionDescription.OK.getCode());
        model.put("user", user);
        return freemarkerService.renderFromTemplateFile("mosicuser.ftl", model);
    }

    @ApiOperation(value = "马赛克启动", notes = "设置马赛克导航频道信息")
    @RequestMapping(method = RequestMethod.POST, value = "/setscreeninput", produces = "application/json;charset=UTF-8")
    @ResponseBody
    @Transactional
    public String updateScreen(HttpServletRequest request,@RequestBody String jsonString) {
        int code = 0;
        UserScreenLayout finalLayout = null;
        try{
            Boolean mosaic = Environment.getProfiler().isMosaic();
            if(!mosaic){
                throw BusinessExceptionDescription.MOSAIC_NOT_SUPPORT.exception();
            }
            if (StringUtils.isBlank(jsonString)) {
                throw BusinessExceptionDescription.MOSAIC_INVALID_PARAM.exception();
            }
            MosicScreenBean screenBean = getBeanFromString(jsonString);
            if (screenService.getById(screenBean.getScreenid()) != null) {
                List<MosicChannelBean>  Channels=screenBean.getChannels();
                for (int i = 0; i < Channels.size(); i++) {
                   final Channel channel=channelService.getByOrigchannelid(Channels.get(i).getChannelid());
                    if(channel!=null){
                        channel.setApiHeart(Channels.get(i).getHeart());
                        channelService.save(channel);
                    }

                }
            }
            resetTaskStaus(screenBean.getScreenid());
            finalLayout = excuteUpdate(screenBean,"setscreeninput");
        }catch (BusinessException e){
            code = e.getDescription().getCode();
            throw e;
        }
        finally {
            printStringParams("setscreeninput",jsonString,code,finalLayout,request.getRemoteAddr());
        }

        return renderSuccessResponse();
    }
    @ApiOperation(value = "马赛克启动", notes = "设置马赛克导航频道信息和频道位置")
    @RequestMapping(method = RequestMethod.POST, value = "/remote_updatescreen", produces = "application/json;charset=UTF-8")
    @ResponseBody
    @Transactional
    public String updateScreenLayout(HttpServletRequest request, @RequestBody String jsonString) {
        MosicScreenBean screenBean = getBeanFromString(jsonString);
        if (screenService.getById(screenBean.getScreenid()) != null) {
            List<MosicChannelBean>  Channels=screenBean.getChannels();
            for (int i = 0; i < Channels.size(); i++) {
                final Channel channel=channelService.getByOrigchannelid(Channels.get(i).getChannelid());
                if(channel!=null){
                    channel.setApiHeart(Channels.get(i).getHeart());
                    channelService.save(channel);
                }

            }
        }
        resetTaskStaus(screenBean.getScreenid());
        int code = 0;
        UserScreenLayout finalLayout = null;
        try{
            finalLayout = excuteUpdate(screenBean,"remote_updatescreen");
        }catch (BusinessException e){
            code = e.getDescription().getCode();
            throw e;
        }finally {
            printStringParams("remote_updatescreen",jsonString,code,finalLayout,request.getRemoteAddr());
        }

        return renderSuccessResponse();
    }

    private void resetTaskStaus(Integer screenId){
        if(screenId==null)
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

    private MosicScreenBean getBeanFromString(String json){
        try
        {
            return JsonMapper.getMapper().readValue(json, MosicScreenBean.class);
        }
        catch (Exception e)
        {
            throw BusinessExceptionDescription.MOSAIC_INVALID_PARAM.exception();
        }
    }
    private UserScreenLayout excuteUpdate(MosicScreenBean screenBean,String api){
        checkParams(screenBean,false);

        Boolean mosaic = Environment.getProfiler().isMosaic();

        UpdateScreenSchemaResult result = doUpdateScreenschame(screenBean,mosaic);
        taskDispatcherFacade.reloadOrStartScreenTask(screenBean.getScreenid(),result.getProfileId(),result.getServerId(),result.getGpuStartIndex());

        syncScreenchangeToTargets(api,screenBean);

        return result.getLayout();
    }

    private List<String> getMasterOrSlaveIp(){
        List<String> targets = new ArrayList<>();
        List<Master> devices = masterDao.findAll();
        List<String> ips = NetworkHelper.getLocalIps();
        for(Master device:devices){
            device.getIp();
            if(ips.indexOf(device.getIp())<0)
                targets.add(device.getIp());
        }

        return targets;
    }

    private void printStringParams(String api,String json,int code,UserScreenLayout layout,String caller){
        try{
            logger.info(String.format("excute screen update api(%s) from (%s): code = %d, params=%s",api,caller,code,json));
            if(layout!=null){
                ObjectMapper mapper = new ObjectMapper();
                mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                String finalLayout = mapper.writeValueAsString(layout);
                logger.info(String.format("excute screen,final layout = %s",finalLayout));
            }
        }catch (Exception e){
        }
    }

    private void syncScreenchangeToTargets(final String api,MosicScreenBean mosicScreenBean){
        if(mosicScreenBean.getFlag()!=null && mosicScreenBean.getFlag())
            return;

        final List<String> targetIps = getMasterOrSlaveIp();
        final MosicScreenBean param = mosicScreenBean;
        param.setFlag(true);
        new Timer().schedule(new TimerTask(){
            public void run() {

                for(String ip:targetIps){
                    String uri = String.format("http://%s/%s",ip,api);
                    try{
                        String result = HttpClientUtils.doPostJSON(uri,param);
                        logger.info(String.format("sync to %s, result:%s",uri,result));
                        continue;
                    }catch (Exception e){
                        logger.info(String.format("sync to %s failed",uri));
                    }
                }
                //如果只要这个延迟一次，用cancel方法取消掉．
                this.cancel();

            }}, 1000);
    }

    @RequestMapping(value = "/remotelogout", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String loginOut(String token) {

        final String usertoken = token;
        transactionTemplate.execute(new TransactionCallback<Void>() {

            @Override
            public Void doInTransaction(TransactionStatus status) {

                Integer userId = userService.getIdByToken(usertoken);
                userService.logout(userId);

                return null;
            }
        });

        return renderSuccessResponse();
    }

    private void checkParams(MosicScreenBean screenBean,boolean checkToken)
    {
        if(screenBean == null)
            throw BusinessExceptionDescription.MOSAIC_INVALID_PARAM.exception();

        try {
            ScreenDynamicLayout screenDynamicLayout = screenService.getScreenDynamicLaout(screenBean.getScreenid());
            if(screenDynamicLayout!=null){
                Date lastUpdate = screenDynamicLayout.getLastupdate();
                if(lastUpdate!=null){
                    Date now = new Date();
                    long beofre = (now.getTime() - lastUpdate.getTime())/(1000);
                    long apiInterval = Long.valueOf(Environment.getProperty("api.interval","0"));
                    if(apiInterval > 0 && beofre<apiInterval)
                        throw MOSAIC_APICALL_TOOFREQUENT.exception();
                }
            }
        }catch (Exception e){
        }

        if(screenBean.getChannels() == null ||
                screenBean.getChannelcount() > screenBean.getChannels().size()||
                screenBean.getChannelcount() <1 )
        {
            logger.info("channel count does not match");
            throw BusinessExceptionDescription.MOSAIC_INVALID_PARAM.exception();
        }

        if(!StringUtils.isEmpty(screenBean.getForeground())){
         try {
             File file = new File(screenBean.getForeground());
             if(!file.exists()){
                 screenBean.setForeground(null);
             }
         }catch (Exception e){
             screenBean.setForeground(null);
         }
        }else{
            screenBean.setForeground(null);
        }

        if(checkToken){
            User user = null;
            try
            {
                user = userService.getUserByToken(screenBean.getToken());
            }
            catch (TokenExpireException e)
            {
                throw BusinessExceptionDescription.MOSAIC_TOKEN_EXPIRED.exception();
            }
            catch (TokenNotExistException e)
            {
                throw BusinessExceptionDescription.MOSAIC_INVALID_TOKEN.exception();
            }

            if (user == null) {
                throw BusinessExceptionDescription.MOSAIC_INVALID_TOKEN.exception();
            }
        }

        BusinessExceptionDescription code = OK;
        MosicChannelBean temp = screenBean.getChannels().get(0);
        if(temp.getPosition() != null){
            Rectangle screen = new Rectangle(0,0,screenBean.getWidth(),screenBean.getHeight());
            List<MosicChannelBean> channelBeans = screenBean.getChannels();
            for(MosicChannelBean channel:channelBeans ){
                if(channel.getPosition() == null ){
                    code = MOSAIC_INVALID_POSITION;
                    break;
                }
                if(!screen.contains(channel.getPosition().getRectangle())){
                    logger.info("pos out of range:"+channel.getPosition().toString());
                    code = MOSAIC_POSITION_OUTRANGE;
                    break;
                }

                if(channelService.getByOrigchannelid(channel.getChannelid())==null){
                    logger.info("Invalid channel id:"+channel.getChannelid());
                    code = MOSAIC_INVALID_CHANNEL_ID;
                    break;
                }
            }

            if(code==OK){//check intersection
               int nCount = channelBeans.size();
                for(int i = 0;i<nCount;i++){
                    boolean intersect = false;
                    Rectangle chanRec = channelBeans.get(i).getPosition().getRectangle();
                    for(int j=i+1;j<nCount;j++){
                        Rectangle chanRecTemp = channelBeans.get(j).getPosition().getRectangle();
                        if(chanRecTemp.intersects(chanRec)){
                            logger.info("channel pos intersect:"+channelBeans.get(i).getPosition().toString()+channelBeans.get(j).getPosition().toString());
                            intersect = true;
                            break;
                        }
                    }
                    if(intersect){
                        code = MOSAIC_POSITION_INTERSECT;
                        break;
                    }
                }
            }
        }else{
            for(MosicChannelBean channel:screenBean.getChannels() ){
                if(channel.getPos() == null ){
                    code = MOSAIC_INVALID_POSITION;
                    break;
                }
            }
        }

        if(code != OK)
            throw code.exception();
    }

    private UpdateScreenSchemaResult doUpdateScreenschame(final MosicScreenBean mosicScreenBean,final Boolean mosaic)
    {
        return transactionTemplate.execute(new TransactionCallback<UpdateScreenSchemaResult>() {

            @Override
            public UpdateScreenSchemaResult doInTransaction(TransactionStatus status) {

                UpdateScreenSchemaResult result = new UpdateScreenSchemaResult();

                Integer screenId = mosicScreenBean.getScreenid();
                Screen  screen = null;
                try
                {
                    screen = screenService.getById(screenId);
                }
                catch (Exception e)
                {
                    screen = null;
                }

                if(screen==null)
                {
                    throw BusinessExceptionDescription.MOSAIC_SCREEN_NOT_EXIST.exception();
                }

                ScreenSchema schema = screen.getActiveSchema();
                if(schema==null)
                {
                    throw BusinessExceptionDescription.MOSAIC_SCREEN_NOT_INITIALIZED.exception();
                }

                //  Integer profileId = taskService.getUsedTaskProfileIdByScreenId(screenId);
                Task task = taskService.getScreenTask(screenId);
                try
                {
                    task = taskService.getScreenTask(screenId);
                }
                catch (Exception e)
                {
                    task = null;
                }

                if(task==null)
                {
                    throw BusinessExceptionDescription.MOSAIC_SCREEN_NOT_INITIALIZED.exception();
                }

                if(mosaic){
                    try {
                        TaskProfileDto taskProfileDto = taskProfileConverter.doBack(task.getProfile());
                        if(taskProfileDto==null){
                            throw BusinessExceptionDescription.MOSAIC_SCREEN_NOT_INITIALIZED.exception();
                        }

                        if( taskProfileDto.getOutputs().size()>1){
                            throw BusinessExceptionDescription.MOSAIC_TOOMANY_OUTPUT.exception();
                        }

                        if(mosicScreenBean.getChannels().get(0).getPosition()!=null) {
                            int width = taskProfileDto.getOutputProfiles().get(0).getVideoprofiles().get(0).getWidth();
                            int height = taskProfileDto.getOutputProfiles().get(0).getVideoprofiles().get(0).getHeight();
                            if (width != mosicScreenBean.getWidth() ||
                                    height != mosicScreenBean.getHeight()) {
                                throw BusinessExceptionDescription.MOSAIC_RESOLUTION_NOT_MATCH.exception();
                            }
                        }
                    }catch (Exception e){
                        throw BusinessExceptionDescription.MOSAIC_INVALID_PARAM.exception();
                    }
                }

                List<ScreenPosition> screenPositions = schema.getScreenPositions();
                int nMatchCount = 0;
                List<MosicChannelBean>Channels = mosicScreenBean.getChannels();
                if(screenPositions.size() > Channels.size())
                    nMatchCount = Channels.size();
                else
                    nMatchCount = screenPositions.size();

                if(mosicScreenBean.getChannels().get(0).getPos() != null){
                    //update channel information first
                    List<SchemaPosChannel> Positions = new ArrayList<SchemaPosChannel>();

                    for(int i = 0;i<nMatchCount;i++)
                    {
                        MosicChannelBean newpos = Channels.get(i);
                        try
                        {

                            Positions.add( new SchemaPosChannel(newpos.getPos(),channelService.getByOrigchannelid(newpos.getChannelid())));
                        }
                        catch (Exception e)
                        {
                            logger.info("update channel failed:"+newpos);
                        }
                    }

                    //we should clear the empty channel here,yshe

                    screenService.updateScreenPositionChannels(schema.getId(),Positions);
                    screenService.updateUserLayout(screenId,null);
                }else{
                    //update customized layout
                    mosicScreenBean.sortChannels();
                    UserScreenLayout userScreenLayout = new UserScreenLayout();
                    userScreenLayout.setScreenid(screenId);
                    userScreenLayout.setWidth(mosicScreenBean.getWidth());
                    userScreenLayout.setHeight(mosicScreenBean.getHeight());
                    userScreenLayout.setBackground(mosicScreenBean.getForeground());

                    List<UserChannelDesc> userChannelDescList = new ArrayList<UserChannelDesc>();
                    List<SchemaPosChannel> Positions = new ArrayList<SchemaPosChannel>();
                    List<MosicChannelBean> channelBeens = mosicScreenBean.getChannels();
                    for(int i = 0;i<nMatchCount;i++)
                    {
                        try
                        {
                            MosicChannelBean channelBean = channelBeens.get(i);
                            UserChannelDesc userChannelDesc = new UserChannelDesc();
                            userChannelDesc.setPosition(channelBean.getPosition());
                            Channel channel = channelService.getByOrigchannelid(channelBean.getChannelid());
                            userChannelDesc.setChannelid(channel.getId());
                            userChannelDesc.setChannelname(channel.getName());
                            userChannelDesc.setCd(channelBean.getCd());
                            userChannelDesc.setSd(channelBean.getSd());
                            userChannelDescList.add(userChannelDesc);

                            Positions.add( new SchemaPosChannel(i,channelService.getByOrigchannelid(channelBean.getChannelid())));
                        }
                        catch (Exception e)
                        {
                            logger.info("update channel failed:");
                        }

                    }
                    userScreenLayout.setChannels(userChannelDescList);

                    if(!screenService.updateUserLayout(screenId,userScreenLayout))
                    {
                        throw BusinessExceptionDescription.MOSAIC_UPDATE_LAYOUT_FAILED.exception();
                    }
                    //we should clear the empty channel here,yshe
                    for(int i = nMatchCount;i<screenPositions.size();i++){
                        Positions.add( new SchemaPosChannel(i,null));
                    }
                    screenService.updateScreenPositionChannels(schema.getId(),Positions);

                    result.setLayout(userScreenLayout);
                }

                result.setServerId(task.getServerId());
                result.setProfileId(task.getProfile().getId());
                result.setGpuStartIndex(task.getGpudIndex());
                return result;
            }
        });
    }

    class UpdateScreenSchemaResult{
        private String ServerId;
        private Integer ProfileId;
        private Integer gpuStartIndex;
        private UserScreenLayout layout;

        public UpdateScreenSchemaResult(){
            ServerId = "";
            ProfileId = -1;
            gpuStartIndex = -1;
        }

        public String getServerId(){return ServerId;}
        public void setServerId(String serverId){this.ServerId=serverId;}

        public Integer getProfileId(){return ProfileId;}
        public void setProfileId(Integer profileId){this.ProfileId=profileId;}

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
