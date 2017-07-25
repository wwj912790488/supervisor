package com.arcsoft.supervisor.web.graphic;

import com.arcsoft.supervisor.commons.json.JsonMapper;
import com.arcsoft.supervisor.commons.spring.SpringUtils;
import com.arcsoft.supervisor.exception.service.BusinessExceptionDescription;
import com.arcsoft.supervisor.model.domain.channel.Channel;
import com.arcsoft.supervisor.model.domain.graphic.*;
import com.arcsoft.supervisor.model.domain.layouttemplate.LayoutPositionTemplate;
import com.arcsoft.supervisor.model.domain.server.OpsServer;
import com.arcsoft.supervisor.model.domain.server.Server;
import com.arcsoft.supervisor.model.domain.server.ServerComponent;
import com.arcsoft.supervisor.model.domain.task.Task;
import com.arcsoft.supervisor.model.dto.graphic.*;
import com.arcsoft.supervisor.model.vo.task.TaskType;
import com.arcsoft.supervisor.service.channel.ChannelService;
import com.arcsoft.supervisor.service.graphic.MessageStyleService;
import com.arcsoft.supervisor.service.graphic.ScreenService;
import com.arcsoft.supervisor.service.graphic.WallService;
import com.arcsoft.supervisor.service.layouttemplate.LayoutPositionTemplateService;
import com.arcsoft.supervisor.service.server.OpsServerOperator;
import com.arcsoft.supervisor.service.server.OpsServerService;
import com.arcsoft.supervisor.service.server.ServerComponentService;
import com.arcsoft.supervisor.service.settings.RtspConfigurationService;
import com.arcsoft.supervisor.service.task.TaskService;
import com.arcsoft.supervisor.utils.app.Environment;
import com.arcsoft.supervisor.web.ControllerSupport;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.View;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/screen")
public class ScreenController extends ControllerSupport {

    private static final String VIEW_INDEX = "/screen/index";

    public static final String VIEW_SCREEN_INDEX = "/screen/screen-control";

    private final String SUFFIX_TASK_PARAM_FILE = "_transcoder_task.xml";

    @Autowired
    private ScreenService screenService;

    @Autowired
    private MessageStyleService messageStyleService;

    @Autowired
    private WallService wallService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private OpsServerOperator<OpsServer> opsServerOperator;

    @Autowired
    private OpsServerService opsServerService;

    @Autowired
    private ServerComponentService serverComponentService;
    @Autowired
    private TaskService taskService;

    @Autowired
    private LayoutPositionTemplateService layoutPositionTemplateService;

    @Autowired
    private RtspConfigurationService rtspConfigurationService;


    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(Model model) {
        Boolean mosaic = Environment.getProfiler().isMosaic();
        model.addAttribute("supportmosaic", mosaic);
        model.addAttribute("debug", false);
        return VIEW_INDEX;
    }

    @RequestMapping(value = "/index", method = RequestMethod.GET, params = "debug")
    public String indexDebug(Model model) {
        model.addAttribute("debug", true);
        return VIEW_INDEX;
    }

    @RequestMapping(value = "/saveWall", method = RequestMethod.POST)
    @ResponseBody
    public String saveWall(String name) {
        return wallService.save(name).getId().toString();
    }


    @RequestMapping(value = "updateWall", method = RequestMethod.POST)
    @ResponseBody
    public WallWebBean updateWall(WallWebBean wallbean) {
        Wall updatedWall = wallService.updateWith(wallbean);
        return new WallWebBean(updatedWall);
    }

    @RequestMapping(value = "/settingScreenName", method = RequestMethod.POST)
    @ResponseBody
    public Integer settingScreenName(Integer wallId, String rowAndColumn, String settingScreenName) {

        //return wallService.save(name).getId().toString();
        int row = Integer.valueOf(rowAndColumn.substring(0, 1));
        int Column = Integer.valueOf(rowAndColumn.substring(1, 2));
        WallPosition wallPosition = wallService.getWallPositionWithRowAndColumn(wallId, row, Column);
        wallPosition.setWallName(settingScreenName);
        wallService.saveOrUpdateWallPosition(wallId, wallPosition);
        return wallId;
    }

    @RequestMapping(value = "/getScreenName", method = RequestMethod.GET)
    @ResponseBody
    public List<WallScreenName> getScreenName(Integer wallId) {

        Wall wall = wallService.getById(wallId);
        List<WallPosition> wallPositionList = wall.getWallPositions();
        List<WallScreenName> resultList = new ArrayList<>();
        for (WallPosition wallPosition : wallPositionList) {
            WallScreenName wallScreenName = new WallScreenName(wallPosition.getId(), wallPosition.getRow(), wallPosition.getColumn(), wallPosition.getWallName());
            resultList.add(wallScreenName);
        }
        return resultList;
    }

    @RequestMapping(value = "addWall", method = RequestMethod.POST)
    @ResponseBody
    public WallWebBean addWall(WallWebBean wallbean) {
        Wall updatedWall = wallService.updateWith(wallbean);
        return new WallWebBean(updatedWall);
    }

    @RequestMapping(value = "/updateWallPositionOps", method = RequestMethod.POST)
    @ResponseBody
    public void updateWallPositionOps(String opsPositions) throws IOException {
        OpsServerPositionWebBean bean = JsonMapper.getMapper().readValue(opsPositions, OpsServerPositionWebBean.class);
        wallService.updateWallPositionOps(bean.getWallId(), bean.getOpsIds(), bean.getPositions());
    }

    @RequestMapping(value = "/walls", method = RequestMethod.GET)
    @ResponseBody
    public List<WallWebBean> getWalls() {
        List<Wall> walls = wallService.findAll();
        List<WallWebBean> wallsbean = new ArrayList<>();
        for (Wall wall : walls) {
            wallsbean.add(new WallWebBean(wall));
        }
        return wallsbean;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/control")
    public String toScreenIndex(Model model) {
        return VIEW_SCREEN_INDEX;
    }


    @RequestMapping(value = "/removeWall", method = RequestMethod.POST)
    @ResponseBody
    public void removeWall(Integer id) {
        wallService.delete(id);
    }

    @RequestMapping(value = "/findTaskXMLByTaskId", method = RequestMethod.GET, produces = "application/xml;charset=UTF-8")
    @ResponseBody
    public String findTaskXMLByTaskId(final Integer taskId) {
        File tmpdir = new File("/usr/local/arcvideo/supervisor/tmpdir");
        if (tmpdir.exists()) {
            File[] tmpfs = tmpdir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(SUFFIX_TASK_PARAM_FILE) && name.startsWith(taskId.toString());
                }
            });
            File finalFile = tmpfs[0];
            for (File file : tmpfs) {
                if (file.lastModified() >= finalFile.lastModified()) {
                    finalFile = file;
                }else {
                    file.delete();
                }
            }
            try {
                String str = FileUtils.readFileToString(finalFile);
                return str;
            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }
        }

        return "";
    }

    @RequestMapping(value = "/allScreen", method = RequestMethod.GET)
    @ResponseBody
    public List allScreen() {
        List<Wall> wallList = wallService.findAll();
        List ResultList = new ArrayList();
        if (wallList != null) {
            for (Wall wall : wallList) {
                HashMap<String, Object> mp = new HashMap<>();
                List<ScreenWebBeanAndTask> list = new ArrayList();
                List<WallPosition> WallList = wall.getWallPositions();
                for (WallPosition wallPosit : WallList) {
                    ScreenWebBean screen = getScreen(wallPosit.getWall().getId(), (byte) wallPosit.getRow().intValue(), (byte) wallPosit.getColumn().intValue());
                    if (screen.getStatus() != null) {
                        ScreenWebBeanAndTask bean=new ScreenWebBeanAndTask(screen,taskService.getScreenTask(screen.getId()));
                        list.add(bean);
                    }
                }
                mp.put("list", list);
                mp.put("name", wall.getName());
                ResultList.add(mp);
            }
            return ResultList;
        }
        return null;
    }

    @RequestMapping(value = "/allScreenByWallId", method = RequestMethod.GET)
    @ResponseBody
    public List allScreenByWallId(Integer wallId) {
        Wall wall = wallService.getById(wallId);
        if (wall != null) {
            List ResultList = new ArrayList();
            List<WallPosition> WallList = wall.getWallPositions();
            for (WallPosition wallPosit : WallList) {
                ScreenWebBean screen = getScreen(wallId, (byte) wallPosit.getRow().intValue(), (byte) wallPosit.getColumn().intValue());
                ResultList.add(screen);
            }
            return ResultList;
        }
        return null;
    }

    @RequestMapping(value = "screen", method = RequestMethod.GET)
    @ResponseBody
    public ScreenWebBean getScreen(Integer wallId, Byte row, Byte column) {
        WallPosition wallPosition = wallService.getWallPositionWithRowAndColumn(wallId, row, column);
        Screen screen = wallPosition.getScreen();
        if (screen == null) {
            screen = new Screen(wallPosition);
            screen = screenService.save(screen);
        }
        if (screen.getStyle() == null) {
            MessageStyle style = messageStyleService.getDefault();
            screen = screenService.updateStyle(screen.getId(), style);
        }
        TaskType taskType = wallPosition.getWall().getType() == 1 ? TaskType.IP_STREAM_COMPOSE : TaskType.SDI_STREAM_COMPOSE;
        Task task = taskService.getByTypeAndReferenceId(screen.getId(), taskType);
        String status = task == null ? null : task.getStatus();
        ScreenWebBean bean = new ScreenWebBean(screen, status);
        if (bean != null && screen.getRtspFileName() != null) {
            bean.setOutputAddr2(rtspConfigurationService.composeUrl(screen.getRtspFileName(), SpringUtils.getThreadBoundedHttpServletRequest().getRemoteAddr()));
        }
        return bean;
    }

    @RequestMapping(value = "screenPosition", method = RequestMethod.GET)
    @ResponseBody
    public ScreenPositionWebBean getScreenPosition(Integer schemaId, Byte row, Byte column, Integer group) {
        ScreenPosition screenPosition = screenService.getScreenPositionByRowAndColumnAndGroup(schemaId, row, column, group);
        return new ScreenPositionWebBean(screenPosition);
    }

    @RequestMapping(value = "updateScreenPosition", method = RequestMethod.POST)
    @ResponseBody
    public ScreenSchemaWebBean updateScreenPosition(ScreenPositionWebBean bean) {
        Channel channel = null;
        if (bean.getChannelId() != -1) {
            channel = channelService.getById(bean.getChannelId());
            if (channel == null) {
                throw BusinessExceptionDescription.CHANNEL_NOT_EXIST.exception();
            }
        }
        ScreenPosition screenPosition = screenService.updateScreenPositionChannel(bean.getSchemaId(), bean.getRow(),
                bean.getColumn(), bean.getGroup(), channel);
        return new ScreenSchemaWebBean(screenPosition.getScreenSchema());
    }

    @RequestMapping(value = "switchScreenPositionChannel", method = RequestMethod.POST)
    @ResponseBody
    public void switchScreenPositionChannel(Integer schemaId, Integer rowOne, Integer columnOne, Integer rowTwo, Integer columnTwo, Integer group) {
        screenService.updateScreenPositionChannel(schemaId, rowOne, columnOne, rowTwo, columnTwo, group);
    }

    @RequestMapping(value = "updateScreenPositionBundle", method = RequestMethod.POST)
    @ResponseBody
    public ScreenSchemaWebBean updateScreenPositionBundle(Integer schemaId, int group, @RequestParam(required = false, defaultValue = "false") Boolean allowgroup, String channels) throws IOException {
        List<Integer> channelList = JsonMapper.getMapper().readValue(channels, JsonMapper.getMapper().getTypeFactory()
                .constructCollectionType(List.class, Integer.class));
        ScreenSchema schema = screenService.updateScreenSchemaChannels(schemaId, group, channelList);
        return new ScreenSchemaWebBean(schema);
    }

    @RequestMapping(value = "activeSchema", method = RequestMethod.POST)
    @ResponseBody
    public void updateActiveSchema(Integer screenId, Integer schemaId) {
        Screen screen = screenService.getById(screenId);
        if (screen.getActiveSchema().getId() != schemaId) {
            screenService.updateUserLayout(screen.getId(), null);
        }
        ScreenSchema schema = screenService.getScreenSchemaById(schemaId);
        screen.setActiveSchema(schema);
        screenService.save(screen);
    }

    @RequestMapping(value = "updateSchemaName", method = RequestMethod.POST)
    @ResponseBody
    public void updateSchemaName(Integer schemaId, String name) {
        ScreenSchema schema = screenService.getScreenSchemaById(schemaId);
        schema.setName(name);
        screenService.save(schema);
    }

    @RequestMapping(value = "updateSchema", method = RequestMethod.POST)
    @ResponseBody
    public ScreenSchemaWebBean updateScreenSchema(ScreenSchemaWebBean schema) {
        ScreenSchema screenSchema = screenService.getScreenSchemaById(schema.getId());
        ScreenSchema updated = screenService.updateScreenSchema(screenSchema, schema.getRow(), schema.getColumn());
        return new ScreenSchemaWebBean(updated);
    }

    @RequestMapping(value = "updateSchemaTemplate", method = RequestMethod.POST)
    @ResponseBody
    public ScreenSchemaWebBean updateScreenSchemaTemplate(Integer schemaId, Integer template) {
        ScreenSchema schema = screenService.updateScreenSchemaTemplate(schemaId, template);
        if (schema != null) {
            screenService.updateUserLayout(schema.getScreen().getId(), null);
        }
        return new ScreenSchemaWebBean(schema);
    }

    @RequestMapping(value = "updateSchemaGroup", method = RequestMethod.POST)
    @ResponseBody
    public ScreenSchemaWebBean updateScreenSchemaGroup(Integer schemaId, Integer group) {
        ScreenSchema schema = screenService.updateScreenSchemaGroup(schemaId, group);
        return new ScreenSchemaWebBean(schema);
    }

    @RequestMapping(value = "updateSchemaSwitchTime", method = RequestMethod.POST)
    @ResponseBody
    public ScreenSchemaWebBean updateScreenSchemaSwitchTime(Integer schemaId, Integer switchTime) {
        ScreenSchema schema = screenService.updateScreenSchemaSwitchTime(schemaId, switchTime);
        return new ScreenSchemaWebBean(schema);
    }

    @RequestMapping(value = "updateScreenStyle", method = RequestMethod.POST)
    @ResponseBody
    public MessageStyle updateScreenStyle(@RequestBody ScreenStyleBean bean) {
        Integer screenId = bean.getScreenId();
        MessageStyle style = bean.getStyle();
        MessageStyle defaultStyle = messageStyleService.getDefault();
        if (defaultStyle.getId() == style.getId() && !defaultStyle.equals(style)) {
            style.setId(null);
        }
        MessageStyle savedStyle = messageStyleService.save(style);
        screenService.updateStyle(screenId, savedStyle);
        return savedStyle;
    }

    @RequestMapping(value = "resetScreenStyle", method = RequestMethod.POST)
    @ResponseBody
    public MessageStyle resetScreenStyle(@RequestBody ScreenStyleBean bean) {
        Integer screenId = bean.getScreenId();
        MessageStyle defaultStyle = messageStyleService.getDefault();
        screenService.updateStyle(screenId, defaultStyle);
        return defaultStyle;
    }

    @RequestMapping(value = "updateScreenDefaultStyle", method = RequestMethod.POST)
    @ResponseBody
    public MessageStyle updateScreenDefaultStyle(@RequestBody ScreenStyleBean bean) {
        Integer screenId = bean.getScreenId();
        MessageStyle style = bean.getStyle();
        MessageStyle defaultStyle = messageStyleService.getDefault();
        if (defaultStyle.getId() != style.getId()) {
            style.setId(defaultStyle.getId());
        }
        MessageStyle savedStyle = messageStyleService.save(style);
        screenService.updateStyle(screenId, savedStyle);
        return savedStyle;
    }

    @RequestMapping(value = "updateScreenMessage", method = RequestMethod.POST)
    @ResponseBody
    public void updateScreenMessage(Integer screenId, String message) {
        screenService.updateMessage(screenId, message);
        Screen screen = screenService.getById(screenId);
        taskService.displayStyledMessageOnScreen(screenId, screen.getStyle(), message);
    }

    @RequestMapping(value = "channels", method = RequestMethod.GET)
    @ResponseBody
    public List<Channel> getChannels() {
        return channelService.listAll();
    }

    @RequestMapping(value = "opsServers", method = RequestMethod.GET)
    @ResponseBody
    public List<OpsServer> getOpsServers() {
        List<OpsServer> opsServers = opsServerService.findAll();
        final Comparator<String> comparator = createComparatorForStringEndsWithNumber(false);
        Collections.sort(opsServers, new Comparator<OpsServer>() {
            @Override
            public int compare(OpsServer o1, OpsServer o2) {
                return comparator.compare(o1.getIp(), o2.getIp());
            }

        });
        return opsServers;
    }

    @RequestMapping(value = "updateTaskProfile", method = RequestMethod.POST)
    @ResponseBody
    public void updateScreenTaskprofile(Integer screenId, Integer taskProfileId, String serverId, Integer gpuIndex) {
        try {
            Screen screen = screenService.getById(screenId);
            int type = screen.getWallPosition().getWall().getType();
            if (type == 1) {
                taskService.createOrGetTask(screenId, TaskType.IP_STREAM_COMPOSE,
                        StringUtils.isEmpty(serverId) || serverId.startsWith("-") ? null : serverId,
                        taskProfileId, gpuIndex);
            } else if (type == 2) {
                taskService.createOrGetTask(screenId, TaskType.SDI_STREAM_COMPOSE,
                        screen.getWallPosition().getSdiOutput().getServer().getId(), taskProfileId, gpuIndex);
            }
        } catch (Exception e) {
            logger.info("updateScreenTaskprofile failed", e);
        }
    }

    /**
     * Create comparator for string which end with numbers.
     *
     * @param ignoreCase - compare ignore case
     * @return the string comparator.
     */
    public static Comparator<String> createComparatorForStringEndsWithNumber(final boolean ignoreCase) {
        return new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                String regex = "\\d+$";
                Pattern pattern = Pattern.compile(regex);
                Matcher m1 = pattern.matcher(o1);
                Matcher m2 = pattern.matcher(o2);
                if (!m1.find() || !m2.find()) {
                    return ignoreCase ? o1.compareToIgnoreCase(o2) : o1.compareTo(o2);
                } else {
                    String s1 = o1.replaceAll(regex, "");
                    String s2 = o2.replaceAll(regex, "");
                    int ret = ignoreCase ? s1.compareToIgnoreCase(s2) : s1.compareTo(s2);
                    if (ret != 0)
                        return ret;
                    return Integer.valueOf(m1.group()).compareTo(Integer.valueOf(m2.group()));
                }
            }
        };
    }

    @RequestMapping(value = "sdiOutputs", method = RequestMethod.GET)
    @ResponseBody
    public List<DeviceSdiWebBean> getSDIOutputs() {
        List<ServerComponent> sdis = serverComponentService.getSdiOutputs();
        List<DeviceSdiWebBean> deviceSdis = new ArrayList<DeviceSdiWebBean>();
        if (sdis.size() > 0) {
            DeviceSdiWebBean device = new DeviceSdiWebBean();
            Server server = sdis.get(0).getServer();
            device.setId(server.getId());
            device.setName(server.getName());
            device.getSdis().add(new SdiOutputWebBean(sdis.get(0)));
            deviceSdis.add(device);
            for (int i = 1; i < sdis.size(); i++) {
                Server s = sdis.get(i).getServer();
                if (!s.getId().equals(device.getId())) {
                    device = new DeviceSdiWebBean();
                    device.setId(s.getId());
                    device.setName(s.getName());
                    deviceSdis.add(device);
                }
                device.getSdis().add(new SdiOutputWebBean(sdis.get(i)));
            }
        }
        return deviceSdis;
    }

    @RequestMapping(value = "recognize", method = RequestMethod.POST)
    @ResponseBody
    public void recognizeOpsServers(Integer wallId) {
        Wall wall = wallService.getById(wallId);
        List<WallPosition> wallPositions = wall.getWallPositions();
        for (WallPosition position : wallPositions) {
            if (wall.getType() == 1) {
                if (position.getOpsServer() != null) {
                    opsServerOperator.recognize(
                            position.getOpsServer(),
                            position.getRow() * wall.getColumnCount()
                                    + position.getColumn() + 1);
                }
            } else if (wall.getType() == 2) {
                ServerComponent sdi = position.getSdiOutput();
                if (sdi != null) {
                    taskService.recognize(
                            sdi.getServer(),
                            sdi,
                            position.getRow() * wall.getColumnCount()
                                    + position.getColumn() + 1);
                }
            }
        }

    }

    @RequestMapping(value = "unbindOps", method = RequestMethod.POST)
    @ResponseBody
    public void unbindOps(Integer wallPosition) {
        wallService.resetWallPositionOps(wallPosition);
    }

    @RequestMapping(value = "bindOps", method = RequestMethod.POST)
    @ResponseBody
    public OpsServer bindOps(Integer wallPosition, String opsId) {
        OpsServer ops = wallService.updateWallPositionOps(wallPosition, opsId);
        return ops;
    }

    @RequestMapping(value = "updateOutput", method = RequestMethod.POST)
    @ResponseBody
    public void updateWallPositionOutput(Integer wallPosition, String output) {
        wallService.updateWallPositionOutput(wallPosition, output);
    }

    @RequestMapping(value = "activeChannels", method = RequestMethod.GET)
    @ResponseBody
    public List<Channel> getActiveChannels() {
        return screenService.getAllActiveChannels();
    }

    @RequestMapping(value = "templates", method = RequestMethod.GET)
    @ResponseBody
    public List<LayoutPositionTemplate> getLayoutPositionTemplates() {
        return layoutPositionTemplateService.findAll();
    }
}
