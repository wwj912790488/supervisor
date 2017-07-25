package com.arcsoft.supervisor.service.task.processor;

import com.arcsoft.supervisor.cluster.action.task.ReloadRequest;
import com.arcsoft.supervisor.cluster.action.task.ReloadResponse;
import com.arcsoft.supervisor.cluster.action.task.StartRequest;
import com.arcsoft.supervisor.cluster.action.task.StartResponse;
import com.arcsoft.supervisor.exception.service.BusinessException;
import com.arcsoft.supervisor.exception.service.BusinessExceptionDescription;
import com.arcsoft.supervisor.model.domain.channel.Channel;
import com.arcsoft.supervisor.model.domain.channel.ChannelContentDetectConfig;
import com.arcsoft.supervisor.model.domain.channel.ChannelSignalDetectTypeConfig;
import com.arcsoft.supervisor.model.domain.graphic.*;
import com.arcsoft.supervisor.model.domain.server.Server;
import com.arcsoft.supervisor.model.domain.task.Task;
import com.arcsoft.supervisor.model.domain.task.TaskPort.PortType;
import com.arcsoft.supervisor.model.domain.task.TaskProfile;
import com.arcsoft.supervisor.model.dto.graphic.ScreenPositionConfig;
import com.arcsoft.supervisor.model.vo.task.MediaCheckType;
import com.arcsoft.supervisor.model.vo.task.TaskType;
import com.arcsoft.supervisor.model.vo.task.cd.ContentDetectParam;
import com.arcsoft.supervisor.model.vo.task.cd.ScreenContentDetectConfig;
import com.arcsoft.supervisor.model.vo.task.compose.ComposeTaskParams;
import com.arcsoft.supervisor.model.vo.task.profile.TaskOutput;
import com.arcsoft.supervisor.model.vo.task.profile.TaskProfileDto;
import com.arcsoft.supervisor.repository.channel.ChannelRepository;
import com.arcsoft.supervisor.repository.graphic.ScreenDynamicLayoutRepository;
import com.arcsoft.supervisor.repository.graphic.ScreenRepository;
import com.arcsoft.supervisor.service.converter.Converter;
import com.arcsoft.supervisor.service.settings.RtspConfigurationService;
import com.arcsoft.supervisor.service.task.TranscoderXmlBuilder;

import com.arcsoft.supervisor.service.task.impl.SEIMessageBuilder;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ServletContextAware;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

/**
 * An support class to providers skeletal implementation for {@link AbstractTaskProcessorSupport}.
 *
 * @author zw.
 */
public abstract class AbstractComposeTaskProcessorSupport extends AbstractTaskProcessorSupport implements ServletContextAware {

    @Autowired
    protected ScreenRepository screenRepository;

    @Autowired
    protected TranscoderXmlBuilder transcoderXmlBuilder;

    @Autowired
    protected RtspConfigurationService rtspConfigurationService;

    @Autowired
    protected Converter<TaskProfileDto, TaskProfile> taskProfileConverter;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private ScreenDynamicLayoutRepository screenDynamicLayoutRepository;

    protected ServletContext servletContext;

    private static final String UDP_URL = "udp:\\/\\/(([01][0-9][0-9]\\.|2[0-4][0-9]\\.|[0-9][0-9]\\.|25[0-5]\\.|[0-9]\\.)([01][0-9][0-9]\\.|2[0-4][0-9]\\.|[0-9][0-9]\\.|25[0-5]\\.|[0-9]\\.)([01][0-9][0-9]\\.|2[0-4][0-9]\\.|[0-9][0-9]\\.|25[0-5]\\.|[0-9]\\.)([01][0-9][0-9]|2[0-4][0-9]|25[0-5]|[0-9][0-9]|[0-9])):([0-9]+)";

    private static final String HTTP_URL = "http:\\/\\/(([01][0-9][0-9]\\.|2[0-4][0-9]\\.|[0-9][0-9]\\.|25[0-5]\\.|[0-9]\\.)([01][0-9][0-9]\\.|2[0-4][0-9]\\.|[0-9][0-9]\\.|25[0-5]\\.|[0-9]\\.)([01][0-9][0-9]\\.|2[0-4][0-9]\\.|[0-9][0-9]\\.|25[0-5]\\.|[0-9]\\.)([01][0-9][0-9]|2[0-4][0-9]|25[0-5]|[0-9][0-9]|[0-9])):([0-9]+)";

    private static final String RTMP_URL = "^(rtmp|http)\\://([a-zA-Z0-9\\.\\-]+(\\:[a-zA-Z0-9\\.&amp;%\\$\\-]+)*@)?((25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])|([a-zA-Z0-9\\-]+\\.)*[a-zA-Z0-9\\-]+\\.[a-zA-Z]{2,4})(\\:[0-9]+)?(/[^/][a-zA-Z0-9\\.\\,\\?\\'\\\\/\\+&amp;%\\$#\\=~_\\-@]*)*$";

    private static final Pattern PATTERN_UDP_URL = Pattern.compile(UDP_URL);

    private static final Pattern PATTERN_HTTP_URL = Pattern.compile(HTTP_URL);

    private static final Pattern PATTERN_RTMP_URL = Pattern.compile(RTMP_URL);

    @Override
    public void reload(Task task) {
        Screen screen = screenRepository.findOne(task.getReferenceId());
        Server server = serverRepository.findOne(task.getServerId());
        ComposeTaskParams composeTaskParams = createComposeTaskParams(
                task,
                task.getTypeAsEnum(),
                screen,
                server.getId(),
                task.getGpudIndex()
        );
        ReloadRequest request = new ReloadRequest();
        request.setTaskParams(composeTaskParams);
        ReloadResponse response = (ReloadResponse) execute(request, server);
        if (!response.isSuccess()) {
            throw BusinessExceptionDescription.ERROR.exception();
        }
    }

    protected ComposeTaskAndResponse startComposeTask(Task task, Server server, Screen screen) {
        ComposeTaskParams composeTask = createComposeTaskParams(
                task,
                task.getTypeAsEnum(),
                screen,
                server.getId(),
                task.getGpudIndex()
        );
        StartRequest request = new StartRequest();
        request.setTask(composeTask);

        StartResponse response = (StartResponse) execute(request, server);
        if (!response.isSuccess()) {
            throw BusinessExceptionDescription.ERROR.exception();
        }
        return new ComposeTaskAndResponse(composeTask, response);
    }

    protected static class ComposeTaskAndResponse {
        private final ComposeTaskParams taskParams;
        private final StartResponse response;

        public ComposeTaskAndResponse(ComposeTaskParams taskParams, StartResponse response) {
            this.taskParams = taskParams;
            this.response = response;
        }

        public ComposeTaskParams getTaskParams() {
            return taskParams;
        }

        public StartResponse getResponse() {
            return response;
        }
    }

    protected ComposeTaskParams createComposeTaskParams(Task task, TaskType taskType, Screen screen, String serverId, Integer gpuStartIndex) {

        ComposeTaskParams composeTask = new ComposeTaskParams();
        TaskProfileDto taskProfileDto;
        try {
            taskProfileDto = taskProfileConverter.doBack(task.getProfile());
        } catch (Exception e) {
            throw BusinessExceptionDescription.ERROR.withException(e);
        }

        List<TaskOutput> taskOutputs = taskProfileDto.getOutputs();
        for (TaskOutput output : taskOutputs) {
            if (output.isMobileType()) {
                composeTask.setEnableRtsp(true);
                //break;
            } else if (output.isScreenWithRTMP()) {
                composeTask.setScreenWithRTMP(true);
            }
        }

        String targetIp = null;
        Integer screenPort = 0;

        if (screen.getWallPosition().getOutput() == null) {
            check(screen, taskType, composeTask.isScreenWithRTMP());
            targetIp = screen.getWallPosition().getOpsServer().getIp();
            screenPort = getTaskPortWithPortType(task, PortType.SCREEN).getPortNumber();
        } else {
            Matcher matcher2 = PATTERN_RTMP_URL.matcher(screen.getWallPosition().getOutput());
            if (matcher2.matches()) {
                if(screen.getWallPosition().getOutput().startsWith("http")){
                    ImmutablePair<String, Integer> output = parseFromHttpOutput(screen.getWallPosition().getOutput());
                    targetIp = output.getLeft().replace("http","udp");
                    screenPort = output.getRight();
                }else {
                    composeTask.setTargetRtmpUrl(screen.getWallPosition().getOutput());
                }

            } else {
                ImmutablePair<String, Integer> output = parseFromOutput(screen.getWallPosition().getOutput());
                targetIp = output.getLeft();
                screenPort = output.getRight();
            }

        }

        composeTask.setId(task.getId());
        composeTask.setTaskType(taskType);
        composeTask.setContentDetectConfigs(contentDetectConvert(screen));
        composeTask.setSdpFiles(sdpFilesConvert(screen));

        UserScreenLayout userScreenLayout = null;
        try {
            Integer screenDynamicLayout = screen.getUserLayoutId();
            if (screenDynamicLayout != null) {
                try {
                    userScreenLayout = screenDynamicLayoutRepository.findOne(screenDynamicLayout).getUserLayout();
                } catch (Exception e) {
                    userScreenLayout = null;
                }
            }
        } catch (Exception e) {
        }

        int maxLayouts = calculateMaxChannel(screen.getActiveSchema());//screen.getActiveSchema().getRowCount()*screen.getActiveSchema().getColumnCount();
        composeTask.setAmountOfDecodedInputs(maxLayouts);
        if (userScreenLayout == null) {//normal mode
            composeTask.setRowCount(screen.getActiveSchema().getRowCount());//yshe, should modify
            composeTask.setColumnCount(screen.getActiveSchema().getColumnCount());
        } else {//api mode, dynamic change the layout
            composeTask.setRowCount(userScreenLayout.getHeight());
            composeTask.setColumnCount(userScreenLayout.getWidth());
            composeTask.setBackground(userScreenLayout.getBackground());
        }

        composeTask.setGroupCount(screen.getActiveSchema().getGroupCount());
        composeTask.setSwitchTime(screen.getActiveSchema().getSwitchTime());

        boolean isIpComposeStream = taskType == TaskType.IP_STREAM_COMPOSE;
        if (!composeTask.isScreenWithRTMP())
            composeTask.setTargetIp(isIpComposeStream ? targetIp
                    : screen.getWallPosition().getSdiOutput().getName());

//        TaskProfileDto taskProfileDto;
//        try {
//            taskProfileDto = taskProfileConverter.doBack(task.getProfile());
//        } catch (Exception e) {
//            throw BusinessExceptionDescription.ERROR.withException(e);
//        }
//
//        List<TaskOutput> taskOutputs = taskProfileDto.getOutputs();
//        for (TaskOutput output : taskOutputs) {
//            if (output.isMobileType()) {
//                composeTask.setEnableRtsp(true);
//                //break;
//            }else if(output.isScreenWithRTMP())
//            	composeTask.setScreenWithRTMP(true);
//        }

        TranscoderXmlBuilder.BuilderResourceAndXml builderResourceAndXml = transcoderXmlBuilder.build(
                new TranscoderXmlBuilder.BuilderParameters(composeTask, userScreenLayout != null ? convert(userScreenLayout, maxLayouts) : convert(screen), serverId, gpuStartIndex)
        );
        composeTask.setTranscoderTemplate(builderResourceAndXml.getTranscoderXml());
        composeTask.setResolutionAndIndexMappers(
                builderResourceAndXml.getResource().getTaskOutputResolutionAndIndexMappers()
        );
        composeTask.setSeiMessages(builderResourceAndXml.getSeimessageXmls());
        if (composeTask.isScreenWithRTMP()) {
            composeTask.setRtspHostIp(rtspConfigurationService.getIp());
            composeTask.setRtmpOPSFileName(DigestUtils.md5Hex("OPS-" + task.getId()));
        }

        composeTask.setScreenOutputPort(screenPort);

        if (composeTask.isEnableRtsp()) {
            composeTask.setMobileOutputPort(getTaskPortWithPortType(task, PortType.MOBILE).getPortNumber());
            setCommonRtspParams(composeTask);
            //rtsp sdp name use IP- prefix
            composeTask.setRtspFileName(DigestUtils.md5Hex("IP-" + task.getId()));
        }

        return composeTask;
    }

    private ImmutablePair<String, Integer> parseFromOutput(String output) {
        String ip;
        String portString;
        Integer port = 0;
        Matcher matcher = PATTERN_UDP_URL.matcher(output);
        if (matcher.matches()) {
            ip = matcher.group(1);
            portString = matcher.group(6);
            port = Integer.parseInt(portString);
        } else {
            throw BusinessExceptionDescription.TASK_OUTPUT_INVALID.exception();
        }
        return ImmutablePair.of(ip, port);
    }

    private ImmutablePair<String, Integer> parseFromHttpOutput(String output) {
        String ip;
        String portString;
        Integer port = 0;
        Matcher matcher = PATTERN_HTTP_URL.matcher(output);
        if (matcher.matches()) {
            ip = matcher.group(1);
            portString = matcher.group(6);
            port = Integer.parseInt(portString);
        } else {
            throw BusinessExceptionDescription.TASK_OUTPUT_INVALID.exception();
        }
        return ImmutablePair.of(ip, port);
    }


    private Map<String, byte[]> sdpFilesConvert(Screen screen) {
        ScreenSchema activeSchema = screen.getActiveSchema();
        if (activeSchema == null) {
            throw BusinessExceptionDescription.TASK_NO_SCREEN_CONFIG.exception();
        }
        Map<String, ScreenPosition> screenPositionMap = toScreenPositionMap(activeSchema.getScreenPositions());
        Map<String, byte[]> sdpFiles = new HashMap<String, byte[]>();
        Integer groupCount = activeSchema.getGroupCount();
        for (int group = 0; group < groupCount; group++) {
            for (int row = 0; row < activeSchema.getRowCount(); row++) {
                for (int column = 0; column < activeSchema.getColumnCount(); column++) {
                    ScreenPosition position = screenPositionMap.get(getKeyOfScreenPositionMap(row, column, group));
                    byte[] sdpFile = null;
                    if (position != null && position.getChannel() != null && position.getChannel().getAddress() != null && position.getChannel().getAddress().startsWith("sdp")) {
                        String localPath = servletContext.getRealPath("/WEB-INF/" + position.getChannel().getAddress());
                        File file = new File(localPath);
                        if (file.exists()) {
                            try {
                                sdpFile = Files.readAllBytes(file.toPath());
                            } catch (IOException e) {
                            }
                        }
                    }
                    if (sdpFile != null) {
                        sdpFiles.put(position.getChannel().getAddress(), sdpFile);
                    }
                }
            }
        }
        return sdpFiles;
    }

    /**
     * Checks and validate the config of {@code screen}.
     *
     * @param screen the instance of screen
     * @param type   the type of task
     * @throws BusinessException Thrown with below <ul>
     *                           <li>{@link BusinessExceptionDescription#TASK_NO_SCREEN_CONFIG} if there is no config existed
     *                           in screen.</li><li>{@link BusinessExceptionDescription#TASK_WALL_POSITION_NOT_EXIST} if there
     *                           is no position existed in wall of screen</li>
     *                           <li>{@link BusinessExceptionDescription#TASK_OPS_SERVER_NOT_EXIST} if the {@code type} is not
     *                           the sdi compose task and there is no ops server existed</li> </ul>
     */
    protected void check(Screen screen, TaskType type) {
//        if (screen == null) {
//            throw BusinessExceptionDescription.TASK_NO_SCREEN_CONFIG.exception();
//        }
//        WallPosition wallPosition = screen.getWallPosition();
//        if (wallPosition == null) {
//            throw BusinessExceptionDescription.TASK_WALL_POSITION_NOT_EXIST.exception();
//        }
//        if (type != TaskType.SDI_STREAM_COMPOSE && wallPosition.getOpsServer() == null) {
//            throw BusinessExceptionDescription.TASK_OPS_SERVER_NOT_EXIST.exception();
//        }

        check(screen, type, false);
    }

    protected void check(Screen screen, TaskType type, boolean bskipOPS) {
        if (screen == null) {
            throw BusinessExceptionDescription.TASK_NO_SCREEN_CONFIG.exception();
        }
        WallPosition wallPosition = screen.getWallPosition();
        if (wallPosition == null) {
            throw BusinessExceptionDescription.TASK_WALL_POSITION_NOT_EXIST.exception();
        }
        if (type != TaskType.SDI_STREAM_COMPOSE && (!bskipOPS && wallPosition.getOpsServer() == null)) {
            throw BusinessExceptionDescription.TASK_OPS_SERVER_NOT_EXIST.exception();
        }
    }

    /**
     * Converts the given <code>screen</code> to {@code List<ScreenPositionConfig>}.
     *
     * @param screen the instance of screen
     * @return the {@code List<ScreenPositionConfig>} converted from {@code screen}
     * @throws BusinessException Thrown with {@link BusinessExceptionDescription#TASK_NO_SCREEN_CONFIG} if
     *                           there is no screen position in screen
     */
    protected List<ScreenPositionConfig> convert(Screen screen) {
        ScreenSchema activeSchema = screen.getActiveSchema();
        if (activeSchema == null) {
            throw BusinessExceptionDescription.TASK_NO_SCREEN_CONFIG.exception();
        }
        Map<String, ScreenPosition> screenPositionMap = toScreenPositionMap(activeSchema.getScreenPositions());
        Integer groupCount = activeSchema.getGroupCount();
        List<ScreenPositionConfig> configs = new ArrayList<>(activeSchema.getRowCount() * activeSchema.getColumnCount() * groupCount);
        int index = 0;
        for (int group = 0; group < groupCount; group++) {
            for (int row = 0; row < activeSchema.getRowCount(); row++) {
                for (int column = 0; column < activeSchema.getColumnCount(); column++) {
                    ScreenPosition position = screenPositionMap.get(getKeyOfScreenPositionMap(row, column, group));
                    if (position != null) {
                        configs.add(
                                position == null || position.getChannel() == null
                                        ? ScreenPositionConfig.placeHolderConfig(position.getRow(), position.getColumn(), position.getX(), position.getY(), position.getGroupIndex(), index)
                                        : ScreenPositionConfig.from(position.getChannel(), index, position.getRow(), position.getColumn(), position.getX(), position.getY(), position.getGroupIndex())
                        );
                        index++;
                    }
                }
            }
        }
        if (configs.isEmpty()) {
            throw BusinessExceptionDescription.TASK_NO_SCREEN_CONFIG.exception();
        }
        return configs;

    }

    protected int calculateMaxChannel(ScreenSchema activeSchema) {
        int index = 0;
        List<ScreenPosition> positions = activeSchema.getScreenPositions();
        if (positions != null) {
            return positions.size();
        } else {
            Integer groupCount = activeSchema.getGroupCount();
            Map<String, ScreenPosition> screenPositionMap = toScreenPositionMap(activeSchema.getScreenPositions());
            for (int group = 0; group < groupCount; group++) {
                for (int row = 0; row < activeSchema.getRowCount(); row++) {
                    for (int column = 0; column < activeSchema.getColumnCount(); column++) {
                        ScreenPosition position = screenPositionMap.get(getKeyOfScreenPositionMap(row, column, group));
                        if (position != null) {
                            index++;
                        }
                    }
                }
            }
        }
        return index;
    }

    protected List<ScreenPositionConfig> convert(UserScreenLayout userScreenLayout, int maxLayout) {
        List<ScreenPositionConfig> configs = new ArrayList<ScreenPositionConfig>();
        List<UserChannelDesc> userChannelDescList = userScreenLayout.getChannels();
        if (CollectionUtils.isEmpty(userChannelDescList))
            return null;
        int total = userChannelDescList.size();
        int index = 0;
        for (int i = 0; i < maxLayout; i++) {
            if (i >= total) {
                configs.add(ScreenPositionConfig.placeHolderConfig(0, 0, 0, 0, 0, index));
                index++;
                continue;
            }
            UserChannelDesc userChannelDesc = userChannelDescList.get(i);
            Channel channel = null;
            try {
                channel = channelRepository.findOne(userChannelDesc.getChannelid());
            } catch (Exception e) {
            }

            UserChannelPos pos = userChannelDesc.getPosition();
            if (pos == null)
                continue;
            configs.add(
                    channel == null
                            ? ScreenPositionConfig.placeHolderConfig(pos.getY(), pos.getX(), pos.getWidth(), pos.getHeight(), 0, index)
                            : ScreenPositionConfig.from(channel, index, pos.getY(), pos.getX(), pos.getWidth(), pos.getHeight(), 0)
            );
            index++;
        }

        if (configs.isEmpty()) {
            throw BusinessExceptionDescription.TASK_NO_SCREEN_CONFIG.exception();
        }
        return configs;
    }

    private Map<String, ScreenPosition> toScreenPositionMap(List<ScreenPosition> screenPositions) {
        if (screenPositions == null || screenPositions.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, ScreenPosition> screenPositionMap = new HashMap<>();
        for (ScreenPosition screenPosition : screenPositions) {
            screenPositionMap.put(getKeyOfScreenPositionMap(screenPosition.getRow(), screenPosition.getColumn(), screenPosition.getGroupIndex()), screenPosition);
        }
        return screenPositionMap;
    }

    private String getKeyOfScreenPositionMap(int row, int column, int group) {
        return row + "-" + column + "-" + group;
    }

    /**
     * Converts the given <code>screen</code> to {@code List<ScreenContentDetectConfig>}.
     *
     * @param screen the instance of screen
     * @return the {@code List<ScreenContentDetectConfig>} converted from {@code screen}
     */
    protected List<ScreenContentDetectConfig> contentDetectConvert(Screen screen) {
        ScreenSchema activeSchema = screen.getActiveSchema();
        if (activeSchema != null) {
            List<ScreenPosition> positions = activeSchema.getScreenPositions();
            if (positions != null && positions.size() > 0) {
                List<ScreenContentDetectConfig> screenPositionConfigs = new ArrayList<>();
                int index = 0;
                for (ScreenPosition position : positions) {
                    if (position.getChannel() != null) {
                        Channel channel = position.getChannel();
                        if (channel.getEnableContentDetect() && channel.getContentDetectConfig() != null ||
                                channel.getEnableSignalDetect() && channel.getSignalDetectByTypeConfig() != null) {
                            ScreenContentDetectConfig config = new ScreenContentDetectConfig();
                            config.setIndex(index);
                            config.setChannelId(channel.getId());
                            //config.setIsAlive(true);
                            setContentDetectConfig(config, channel.getContentDetectConfig(), channel.getSignalDetectByTypeConfig());
                            screenPositionConfigs.add(config);
                        } else {
                            ScreenContentDetectConfig config = new ScreenContentDetectConfig();
                            config.setIndex(index);
                            config.setChannelId(channel.getId());
                            screenPositionConfigs.add(config);
                        }
                    } else {
                        ScreenContentDetectConfig config = new ScreenContentDetectConfig();
                        config.setIndex(index);
                        config.setChannelId(-1);
                        screenPositionConfigs.add(config);
                    }
                    index++;
                }
                return screenPositionConfigs;
            }
        }
        return Collections.emptyList();
    }

    private void setContentDetectConfig(ScreenContentDetectConfig config, ChannelContentDetectConfig contentDetectConfig, ChannelSignalDetectTypeConfig signalDetectConfig) {
//        if (contentDetectConfig.getEnableBoomSonic()) {
//            ContentDetectParam param = new ContentDetectParam();
//            Map<String, String> breakDetectParams = new HashMap<>();
//            breakDetectParams.put(ScreenContentDetectConfig.DETECT_TIME_KEY, ScreenContentDetectConfig.BREAK_DETECT_DEFAULT_TIME_VALUE);
//            breakDetectParams.put(ScreenContentDetectConfig.DETECT_DB_KEY, contentDetectConfig.getBoomSonicThreshold().toString());
//            param.setParams(breakDetectParams);
//            param.setIndex(MediaCheckType.CHECK_TYPE_BREAK_INDEX);
//            config.getDetectSettings().add(param);
//        }
        try {
            if (contentDetectConfig != null) {
                if (contentDetectConfig.getBlackSeconds() != null && contentDetectConfig.getBlackSeconds() > 0) {
                    ContentDetectParam param = new ContentDetectParam();
                    Map<String, String> blackDetectParams = new HashMap<>();
                    blackDetectParams.put(ScreenContentDetectConfig.DETECT_TIME_KEY, Integer.toString((int) (contentDetectConfig.getBlackSeconds() * 1000)));
                    //blackDetectParams.put(ScreenContentDetectConfig.DETECT_AREA_KEY, ScreenContentDetectConfig.DETECT_DEFAULT_AREA_VALUE);
                    param.setParams(blackDetectParams);
                    param.setIndex(MediaCheckType.CHECK_TYPE_BLACK_FIELD_INDEX);
                    config.getDetectSettings().add(param);
                }
//        if (contentDetectConfig.getGreenSeconds() != null && contentDetectConfig.getGreenSeconds() > 0) {
//            ContentDetectParam param = new ContentDetectParam();
//            Map<String, String> greenDetectParams = new HashMap<>();
//            greenDetectParams.put(ScreenContentDetectConfig.DETECT_TIME_KEY, Integer.toString((int) (contentDetectConfig.getGreenSeconds() * 1000)));
//            param.setParams(greenDetectParams);
//            param.setIndex(MediaCheckType.CHECK_TYPE_GREEN_FIELD_INDEX);
//            config.getDetectSettings().add(param);
//        }
                if (contentDetectConfig.getNoFrameSeconds() != null && contentDetectConfig.getNoFrameSeconds() > 0) {
                    ContentDetectParam param = new ContentDetectParam();
                    Map<String, String> staticDetectParams = new HashMap<>();
                    staticDetectParams.put(ScreenContentDetectConfig.DETECT_TIME_KEY, Integer.toString((int) (contentDetectConfig.getNoFrameSeconds() * 1000)));
                    //staticDetectParams.put(ScreenContentDetectConfig.DETECT_AREA_KEY, ScreenContentDetectConfig.DETECT_DEFAULT_AREA_VALUE);
                    param.setParams(staticDetectParams);
                    param.setIndex(MediaCheckType.CHECK_TYPE_STATIC_FRAME_INDEX);
                    config.getDetectSettings().add(param);
                }
                if (contentDetectConfig.getSilenceSeconds() != null && contentDetectConfig.getSilenceSeconds() > 0) {
                    ContentDetectParam param = new ContentDetectParam();
                    Map<String, String> muteDetectParams = new HashMap<>();
                    muteDetectParams.put(ScreenContentDetectConfig.DETECT_TIME_KEY, Integer.toString((int) (contentDetectConfig.getSilenceSeconds() * 1000)));
                    muteDetectParams.put(ScreenContentDetectConfig.DETECT_DB_KEY, contentDetectConfig.getSilenceThreshold().toString());
                    param.setParams(muteDetectParams);
                    param.setIndex(MediaCheckType.CHECK_TYPE_MUTE_THRESHOLD_INDEX);
                    config.getDetectSettings().add(param);
                }
                if (contentDetectConfig.getLowVolumeSeconds() != null && contentDetectConfig.getLowVolumeSeconds() > 0) {
                    ContentDetectParam param = new ContentDetectParam();
                    Map<String, String> muteDetectParams = new HashMap<>();
                    muteDetectParams.put(ScreenContentDetectConfig.DETECT_TIME_KEY, Integer.toString((int) (contentDetectConfig.getLowVolumeSeconds() * 1000)));
                    muteDetectParams.put(ScreenContentDetectConfig.DETECT_DB_KEY, contentDetectConfig.getLowVolumeThreshold().toString());
                    param.setParams(muteDetectParams);
                    param.setIndex(MediaCheckType.CHECK_TYPE_VOLUME_LOW_INDEX);
                    config.getDetectSettings().add(param);
                }
                if (contentDetectConfig.getLoudVolumeSeconds() != null && contentDetectConfig.getLoudVolumeSeconds() > 0) {
                    ContentDetectParam param = new ContentDetectParam();
                    Map<String, String> muteDetectParams = new HashMap<>();
                    muteDetectParams.put(ScreenContentDetectConfig.DETECT_TIME_KEY, Integer.toString((int) (contentDetectConfig.getLoudVolumeSeconds() * 1000)));
                    muteDetectParams.put(ScreenContentDetectConfig.DETECT_DB_KEY, contentDetectConfig.getLoudVolumeThreshold().toString());
                    param.setParams(muteDetectParams);
                    param.setIndex(MediaCheckType.CHECK_TYPE_VOLUME_LOUD_INDEX);
                    config.getDetectSettings().add(param);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {

            if (signalDetectConfig != null && signalDetectConfig.getWarningAudioLossTimeout() != null && signalDetectConfig.getEnableWarningAudioLoss()) {
                ContentDetectParam audioLossParam = new ContentDetectParam();
                Map<String, String> audioLossParams = new HashMap<>();
                audioLossParams.put(ScreenContentDetectConfig.DETECT_TIME_KEY, Integer.toString(signalDetectConfig.getWarningAudioLossTimeout()));
                audioLossParam.setParams(audioLossParams);
                audioLossParam.setIndex(MediaCheckType.SIGNAL_STREAM_NOAUDIO);
                config.getDetectSettings().add(audioLossParam);
            }
            if (signalDetectConfig != null && signalDetectConfig.getWarningVideoLossTimeout() != null && signalDetectConfig.getEnableWarningVideoLoss()) {
                ContentDetectParam videoLossParam = new ContentDetectParam();
                Map<String, String> videoLossParams = new HashMap<>();
                videoLossParams.put(ScreenContentDetectConfig.DETECT_TIME_KEY, Integer.toString(signalDetectConfig.getWarningVideoLossTimeout()));
                videoLossParam.setParams(videoLossParams);
                videoLossParam.setIndex(MediaCheckType.SIGNAL_STREAM_NOVIDEO);
                config.getDetectSettings().add(videoLossParam);
            }
            if (signalDetectConfig != null && signalDetectConfig.getWarningCcErrorTimeout() != null && signalDetectConfig.getEnableWarningCcError()) {
                ContentDetectParam ccErrorParam = new ContentDetectParam();
                Map<String, String> ccErrorParams = new HashMap<>();
                ccErrorParams.put(ScreenContentDetectConfig.DETECT_TIME_KEY, Integer.toString(signalDetectConfig.getWarningCcErrorTimeout()));
                ccErrorParam.setParams(ccErrorParams);
                ccErrorParam.setIndex(MediaCheckType.SIGNAL_STREAM_CCERROR);
                config.getDetectSettings().add(ccErrorParam);
            }
            if (signalDetectConfig != null && signalDetectConfig.getWarningSignalBrokenTimeout() != null && signalDetectConfig.getEnableWarningSignalBroken()) {
                ContentDetectParam interruptParam = new ContentDetectParam();
                Map<String, String> interruptParams = new HashMap<>();
                interruptParams.put(ScreenContentDetectConfig.DETECT_TIME_KEY, Integer.toString(signalDetectConfig.getWarningSignalBrokenTimeout()));
                interruptParam.setParams(interruptParams);
                interruptParam.setIndex(MediaCheckType.SIGNAL_STREAM_INTERRUPT);
                config.getDetectSettings().add(interruptParam);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

}
