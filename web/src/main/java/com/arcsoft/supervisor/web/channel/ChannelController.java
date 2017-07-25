package com.arcsoft.supervisor.web.channel;

import com.arcsoft.supervisor.commons.json.JsonMapper;
import com.arcsoft.supervisor.commons.spring.SpringUtils;
import com.arcsoft.supervisor.commons.spring.event.EventManager;
import com.arcsoft.supervisor.exception.ObjectAlreadyExistsException;
import com.arcsoft.supervisor.exception.OriginalChannelIdlareadyExistException;
import com.arcsoft.supervisor.exception.server.NameExistsException;
import com.arcsoft.supervisor.exception.service.BusinessExceptionDescription;
import com.arcsoft.supervisor.model.domain.channel.*;
import com.arcsoft.supervisor.model.domain.graphic.ScreenPosition;
import com.arcsoft.supervisor.model.domain.task.Task;
import com.arcsoft.supervisor.model.dto.channel.EditChannelForm;
import com.arcsoft.supervisor.model.vo.task.TaskStatus;
import com.arcsoft.supervisor.model.vo.task.TaskType;
import com.arcsoft.supervisor.repository.channel.ChannelTagRepository;
import com.arcsoft.supervisor.repository.graphic.ScreenPositionJPARepo;
import com.arcsoft.supervisor.service.channel.ChannelGroupService;
import com.arcsoft.supervisor.service.channel.ChannelService;
import com.arcsoft.supervisor.service.channel.event.ChannelRemovedEvent;
import com.arcsoft.supervisor.service.channel.event.ChannelSavedEvent;
import com.arcsoft.supervisor.service.commons.mediainfo.MediainfoService;
import com.arcsoft.supervisor.service.commons.mediainfo.impl.ProgramAndAudioMediainfo;
import com.arcsoft.supervisor.service.log.ContentDetectLogService;
import com.arcsoft.supervisor.service.settings.RtspConfigurationService;
import com.arcsoft.supervisor.service.task.TaskDispatcherFacade;
import com.arcsoft.supervisor.service.task.TaskExecutor;
import com.arcsoft.supervisor.service.task.TaskService;
import com.arcsoft.supervisor.utils.app.Environment;
import com.arcsoft.supervisor.web.ControllerSupport;
import com.arcsoft.supervisor.web.JsonResult;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Interners;
import com.google.common.io.Files;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.record.formula.functions.Mode;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Controller class for {@code channel}.
 *
 * @author zw.
 */
@Controller
@RequestMapping("/channel")
public class ChannelController extends ControllerSupport {

    private static final String VIEW_INDEX = "/channel/index";

    private static String MEDIAINFO_PATH = Environment.getProperty("commander.mediainfo.path", "/usr/local/arcvideo/supervisor/tmpdir/mediainfo/");

    private static final List<String> SUPPORTED_PROTOCOL_LIST = ImmutableList.of("udp://", "http://", "rtsp://", "rtmp://", "rtp://");

    @Autowired
    private ChannelGroupService channelGroupService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private EventManager eventManager;

    @Autowired
    @Qualifier("defaultMediainfoService")
    private MediainfoService mediainfoService;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private TaskService taskService;

    @Autowired(required = false)
    private ServletContext servletContext;

    @Autowired
    private ChannelTagRepository channelTagRepository;

    @Autowired
    private RtspConfigurationService rtspConfigurationService;

    @Autowired
    private ScreenPositionJPARepo screenPositionJPARepo;

    @Autowired
    private TaskDispatcherFacade dispatcherFacade;
    @Autowired
    private ContentDetectLogService contentDetectLogService;

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String toIndex(Model model) {
        Boolean mosaic = Environment.getProfiler().isMosaic();
        model.addAttribute("supportmosaic", mosaic);
        return VIEW_INDEX;
    }

    @RequestMapping(value = "/groups", method = RequestMethod.GET)
    @ResponseBody
    public List<ChannelGroup> getGroups() {
        return channelGroupService.listAll();
    }


    @RequestMapping(value = "/count", method = RequestMethod.GET)
    @ResponseBody
    public List<ChannelGroupAndCount> getCounts() {
        List<ChannelGroupAndCount> result = new ArrayList();
        //defult no group
        ChannelGroup defaultGroup = new ChannelGroup();
        defaultGroup.setId(-1);
        defaultGroup.setName("未分组");
        ChannelGroupAndCount defaultCount = new ChannelGroupAndCount(defaultGroup, channelService.getUngrouped().size());
        result.add(defaultCount);
        for (ChannelGroup channelGroup : channelGroupService.listAll()) {
            ChannelGroupAndCount channelGroupAndCount = new ChannelGroupAndCount(channelGroup, getChannels(channelGroup.getId()).size());
            result.add(channelGroupAndCount);
        }
        return result;
    }


    @RequestMapping(value = "/channels", method = RequestMethod.GET)
    @ResponseBody
    public List<Channel> getChannels(Integer groupId) {
        if (groupId != null && groupId > 0) {
            return channelService.getByGroupId(groupId);
        }
        return Collections.emptyList();
    }

    @RequestMapping(value = "/unGroupedChannels", method = RequestMethod.GET)
    @ResponseBody
    public List<Channel> getUnGroupedChannels() {
        return channelService.getUngrouped();
    }

    @RequestMapping(value = "/saveGroup", method = RequestMethod.POST)
    @ResponseBody
    public String saveGroup(ChannelGroup group) {
        try {
            channelGroupService.save(group);
            return group.getId().toString();
        } catch (Exception e) {
            logger.error("", e);
        }
        return "";
    }

    @RequestMapping(value = "/checkGroup", method = RequestMethod.POST)
    @ResponseBody
    public boolean checkGroup(Integer id) {
        boolean notExistsChannel = true;


        if (id != null && id > 0) {
            try {
                List<Channel> channelList = channelService.getByGroupId(id);
                if (channelList != null) {
                    for (Channel channel : channelList) {
                        //任务是否运行
                        if (dispatcherFacade.isTaskRunning(channel.getId())) {
                            notExistsChannel = false;
                            return notExistsChannel;
                        }
                        //画面是否运行
                        /*List<ScreenPosition> positionList = screenPositionJPARepo.findByChannel(channel);
                        if (positionList.size()>0){
                            notExistsChannel=false;
                            return  notExistsChannel;
                        }
*/
                    }
                }
            } catch (Exception e) {
                logger.error("", e);
            }
        }

        return notExistsChannel;
    }


    @RequestMapping(value = "/removeGroup", method = RequestMethod.POST)
    @ResponseBody
    public void removeGroup(Integer id) {
        if (id != null && id > 0) {
            try {
                ChannelGroup group = channelGroupService.getById(id);
                if (group != null) {
                    stopChannelTasks(group.getChannels());
                    channelGroupService.delete(id);
                    for (Channel channel : group.getChannels()) {
                        eventManager.submit(new ChannelRemovedEvent(channel.getId()));
                    }
                }
            } catch (Exception e) {
                logger.error("", e);
            }
        }
    }

    public boolean checkChannelMobileConfig(Channel channel) {
        if (channel.getId() == null) {
            return false;
        }
        if (channel.getMobileConfigs().size() <= 0) {
            return false;
        }
        Channel oldChannel = channelService.getById(channel.getId());
        List<ChannelMobileConfig> mobileConfigs = channel.getMobileConfigs();
        if (oldChannel.getMobileConfigs().size() <= 0) {
            return false;
        }
        //0 is sd ;1 is hd
        if (!oldChannel.getMobileConfigs().get(1).equals(mobileConfigs.get(1))) {
            return false;
        }
        return true;
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveChannel(Channel channel, HttpServletRequest request) {
        JsonResult result = JsonResult.fromSuccess();
        try {
            //check boolean is update(equls ChannelMobileConfig)
            boolean isUpdate = checkChannelMobileConfig(channel);
            if (channel != null) {
                if (channel.getMobileConfigs() != null) {
                    for (ChannelMobileConfig config : channel.getMobileConfigs()) {
                        if (config.getChannel() == null) {
                            config.setChannel(channel);
                        }
                    }
                }
                channel.setEnableSignalDetect(channel.getEnableSignalDetectByType());

                channelService.save(channel);
                channelService.saveAddress(channel, convertSdpPath(channel, request.getServletContext()));
                // Because analyze the media info will be block, so we need update the channel after
                // the channel is saved.
                if (channel.getAddress().startsWith("sdp")) {
                    channelService.save(channel,
                            mediainfoService.getChannelInfo(
                                    request.getServletContext().getRealPath("/WEB-INF/" + channel.getAddress()),
                                    channel.getProgramId(),
                                    channel.getAudioId()
                            )
                    );
                } else {
                    if ("sdi".equals(channel.getProtocol())) {
                        channelService.save(channel,mediainfoService.getSDIChannelInfo(channel.getPort()));

                    } else {
                        channelService.save(channel,
                                mediainfoService.getChannelInfo(
                                        channel.getAddress(),
                                        channel.getProgramId(),
                                        channel.getAudioId()
                                )
                        );
                    }
                }
                eventManager.submit(new ChannelSavedEvent(channel.getId()));
                result.put("id", channel.getId());
                result.put("channelSame", isUpdate);
               /* if(channel.getIsSupportMobile() && isUpdate){
                    dispatcherFacade.restartChannelTask(channel.getId());
                }*/

            }
        } catch (OriginalChannelIdlareadyExistException e) {
            result.setCode(4);
        } catch (NameExistsException e) {
            result.setCode(1);
        } catch (ObjectAlreadyExistsException e) {
            result.setCode(2);
        } catch (Exception e) {
            result.setCode(3);
        }
        return result;
    }

    @RequestMapping(value = "editChannels", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult editChannels(EditChannelForm form) {
        List<Integer> channelIds = form.getChannelIds();
        for (Integer channelId : channelIds) {
            Channel channel = channelService.getById(channelId);
            if (channel != null) {
                Channel newChannel = new Channel();
                BeanUtils.copyProperties(channel, newChannel);
                BeanUtils.copyProperties(form, newChannel);
                for (ChannelMobileConfig config : newChannel.getMobileConfigs()) {
                    for (ChannelMobileConfig oldconfig : channel.getMobileConfigs()) {//copy the url to new object
                        if (config.getType() == oldconfig.getType()) {
                            config.setAddress(oldconfig.getAddress());
                            break;
                        }
                    }
                    config.setChannel(newChannel);
                }
                channelService.save(newChannel);
                eventManager.submit(new ChannelSavedEvent(channel.getId()));
            }
        }
        return JsonResult.fromSuccess();
    }

    private String convertSdpPath(Channel channel, ServletContext context) {
        String sdpPath = channel.getAddress();
        if (sdpPath == null) {
            return null;
        } else {
            if (sdpPath.startsWith("sdptemp")) {
                String sdpTempPath = context.getRealPath("/WEB-INF/" + sdpPath);
                File file = new File(sdpTempPath);
                if (file.exists()) {
                    String presistSdpPath = context.getRealPath("/WEB-INF/sdp/" + channel.getId() + ".sdp");
                    File presistSdpFile = new File(presistSdpPath);
                    try {
                        Files.copy(file, presistSdpFile);
                    } catch (IOException e) {
                        logger.debug("convert logo failed");
                        throw BusinessExceptionDescription.SAVE_SDP_FAILED.exception();
                    }
                    return "sdp/" + channel.getId() + ".sdp";
                } else {
                    throw BusinessExceptionDescription.SDP_NOT_FOUND.exception();
                }
            } else {
                return sdpPath;
            }
        }
    }

    @RequestMapping(value = "/uploadSdp", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult uploadSdp(@RequestParam("file") MultipartFile multipartFile, HttpServletRequest request) {
        JsonResult result = JsonResult.fromSuccess();
        String logoSdpPath = request.getServletContext()
                .getRealPath("/WEB-INF/sdptemp/");
        try {
            byte[] bytes = multipartFile.getBytes();
            BufferedOutputStream stream = new BufferedOutputStream(
                    new FileOutputStream(new File(logoSdpPath
                            + File.separator
                            + multipartFile.getOriginalFilename())));
            stream.write(bytes);
            stream.close();
            result.put("url", "sdptemp/" + multipartFile.getOriginalFilename());
        } catch (IOException e) {
            result.setCode(1);
            e.printStackTrace();
        }
        return result;
    }

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    @ResponseBody
    public Channel getChannel(Integer id) {
        return channelService.getById(id);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public void deleteChannel(String channelsStr) {
        try {
            List<Channel> channels = JsonMapper.getMapper().readValue(channelsStr, JsonMapper.getMapper().getTypeFactory()
                    .constructCollectionType(List.class, Channel.class));
            if (channels != null) {
                stopChannelTasks(channels);
                channelService.deleteByIds(channels);
                for (Channel channel : channels) {
                    //delete content
                    contentDetectLogService.deleteByChannelId(Integer.valueOf(channel.getId()));
                    eventManager.submit(new ChannelRemovedEvent(channel.getId()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/move", method = RequestMethod.POST)
    @ResponseBody
    public void moveChannel(String channelsStr, Integer groupId) {
        try {
            List<Channel> channels = JsonMapper.getMapper().readValue(channelsStr, JsonMapper.getMapper().getTypeFactory()
                    .constructCollectionType(List.class, Channel.class));
            if (channels != null && groupId != null) {
                channelService.updateGroup(channels, groupId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/sdi/counts", method = RequestMethod.GET)
    @ResponseBody
    public SDIChannel getSdiCounts() {
        JsonResult result = JsonResult.fromSuccess();
        SDIChannel sdiChannel = mediainfoService.getSDI(MEDIAINFO_PATH);
        return sdiChannel;
    }

    @RequestMapping(value = "/sdi/{port}", method = RequestMethod.GET)
    @ResponseBody
    public JsonResult getSdiCounts(@PathVariable("port") Integer port) {
        JsonResult result = JsonResult.fromSuccess();
        try {
            ChannelInfo channelInfo = mediainfoService.getSDIChannelInfo(port);
            result.put("channelInfo", channelInfo);
        } catch (Exception e) {
            result = JsonResult.fromError();
        }

        return result;
    }

    @RequestMapping(value = "/mediainfo", method = RequestMethod.GET)
    @ResponseBody
    public JsonResult analyzeMediainfo(final String ip, HttpServletRequest request) {
        JsonResult result = JsonResult.fromError();
        String sdpTemp = "sdptemp", sdp = "sdp";
        boolean isSupported = FluentIterable.from(SUPPORTED_PROTOCOL_LIST).anyMatch(new Predicate<String>() {
            @Override
            public boolean apply(@Nullable String input) {
                return ip.startsWith(input);
            }
        });
        try {
            if (isSupported) {
                List<ProgramAndAudioMediainfo> programAndAudioMediainfos = mediainfoService.getProgramAndAudio(ip);
                result.put("m", programAndAudioMediainfos);
                result.success();
            }
            if (ip.startsWith(sdpTemp) || ip.startsWith(sdp)) {
                String localPath = request.getServletContext().getRealPath("/WEB-INF/" + ip);
                List<ProgramAndAudioMediainfo> programAndAudioMediainfos = mediainfoService.getProgramAndAudio(localPath);
                result.put("m", programAndAudioMediainfos);
                result.success();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/start", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult startChannels(String channelsStr) {
        if (channelsStr != null) {
            logger.info("startChannels:" + channelsStr);
        }

        List<Integer> channelList = null;
        try {
            channelList = JsonMapper.getMapper().readValue(channelsStr, JsonMapper.getMapper().getTypeFactory()
                    .constructCollectionType(List.class, Integer.class));
        } catch (Exception e) {
            logger.info("startChannels: failed to format string to channle id list.");
        }
        if (channelList != null) {
            logger.info("startChannels: channel count=" + channelList.size());

            for (Integer chanid : channelList) {
                logger.info("startChannels, chan id = " + chanid);
                try {
                    Channel channel = channelService.getById(chanid);
                    if (channel != null) {
                        if (channel.getIsSupportMobile()) {
                            logger.info("startChannels, support mobile = " + channel.getIsSupportMobile());
                            dispatcherFacade.restartChannelTask(chanid);
                        }

/*                        try {
                            List<ChannelMobileConfig> configs = channel.getMobileConfigs();
                            if(configs!=null)
                            {
                                logger.info("startChannels, mobile config= " + channel.getMobileConfigs().toString());
                            }
                            else{
                                logger.info("startChannels, mobile config is null");
                            }
                        }catch (Exception e){
                            logger.info("startChannels, get mobileconfig failed = ");
                        }*/
                    }
//                    if (channel.getIsSupportMobile() && channel.getMobileConfigs() != null && !channel.getMobileConfigs().isEmpty()) {
//                        dispatcherFacade.restartChannelTask(chanid);
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return JsonResult.fromSuccess();
    }

    @RequestMapping(value = "/stop", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult stopChannels(String channelsStr) {
        try {
            if (channelsStr != null) {
                logger.info("stopChannels:" + channelsStr);
            }
            List<Integer> channels = JsonMapper.getMapper().readValue(channelsStr, JsonMapper.getMapper().getTypeFactory()
                    .constructCollectionType(List.class, Integer.class));
            if (channels != null) {
                for (Integer chanid : channels) {
/*                    Task rtspTask = taskService.getByTypeAndReferenceId(chanid, TaskType.RTSP);
                    if (rtspTask != null) {
                        taskExecutor.stop(rtspTask);
                    }*/
                    dispatcherFacade.stopChannelTask(chanid);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return JsonResult.fromSuccess();
    }

    private void stopChannelTasks(List<Channel> channels) {
        for (Channel channel : channels) {
            Task rtspTask = taskService.getByTypeAndReferenceId(channel.getId(), TaskType.RTSP);
            if (rtspTask != null) {
                taskExecutor.stop(rtspTask);
                taskService.delete(rtspTask.getId());
            }
        }
        ;
    }

    @RequestMapping(value = "/frame/{channel}/{index}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public byte[] getChannelFrame(@PathVariable("channel") Integer channelId, @PathVariable("index") Integer index) throws IOException {
        InputStream in = servletContext.getResourceAsStream("/WEB-INF/images/frame.jpg");
        byte[] data = taskService.getRtspTaskThumbnail(channelId);
        return data.length == 0 ? IOUtils.toByteArray(in) : data;
    }

    @RequestMapping(value = "channelInfo", method = RequestMethod.GET)
    @ResponseBody
    public ChannelDetailInfo getChannelInfo(Integer channelId) {
        Channel channel = channelService.getById(channelId);
        if (channel != null) {
            String output = null;
            HashSet hashSet = new HashSet();
            try {
                List<ScreenPosition> positionList = screenPositionJPARepo.findByChannel(channel);
                for (ScreenPosition screenPosition : positionList) {
                    String id = screenPosition.getScreenSchema().getScreen().getId().toString();
                    //总行
                    Integer columnCount = screenPosition.getScreenSchema().getScreen().getWallPosition().getWall().getColumnCount();

                    Integer column = screenPosition.getScreenSchema().getScreen().getWallPosition().getColumn();
                    Integer row = screenPosition.getScreenSchema().getScreen().getWallPosition().getRow();
                    Integer localtion = columnCount * row + column + 1;
                    //System.out.println(id+"  "+( columnCount*row+column+1));

                    String str = screenPosition.getScreenSchema().getScreen().getWallPosition().getWall().getName();
                    hashSet.add("屏幕墙名称:&nbsp" + str + "&nbsp;&nbsp;&nbsp;屏幕id:&nbsp" + id + "&nbsp;&nbsp;&nbsp;在第" + localtion + "块屏幕墙上");
                }

                if (channel.getIsSupportMobile() && channel.getMobileConfigs() != null && !channel.getMobileConfigs().isEmpty()) {
                    Task rtspTask = taskService.getByTypeAndReferenceId(channel.getId(), TaskType.RTSP);
                    if (rtspTask != null && rtspTask.isStatusEqual(TaskStatus.RUNNING)) {
                        for (ChannelMobileConfig config : channel.getMobileConfigs()) {
                            if (StringUtils.isNotBlank(config.getAddress())) {
                                String url = rtspConfigurationService.composeUrl(config.getAddress(), SpringUtils.getThreadBoundedHttpServletRequest().getRemoteAddr());
                                if (config.getType() != 0) {
                                    output = url;
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                output = null;
            }
            return new ChannelDetailInfo(channel.getChannelInfo(), output, hashSet, channel.getAddress());
        } else {
            return null;
        }
    }

    @RequestMapping(value = "tags", method = RequestMethod.GET)
    @ResponseBody
    public List<ChannelTag> getChannelTag() {
        return channelTagRepository.findChannelsNotEmpty();
    }


}
