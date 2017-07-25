package com.arcsoft.supervisor.web.device;

import com.arcsoft.supervisor.commons.SupervisorDefs;
import com.arcsoft.supervisor.commons.json.JsonMapper;
import com.arcsoft.supervisor.model.domain.graphic.Wall;
import com.arcsoft.supervisor.model.domain.server.*;
import com.arcsoft.supervisor.repository.server.SSHConnectInfoRepository;
import com.arcsoft.supervisor.service.graphic.WallService;
import com.arcsoft.supervisor.service.server.ServerComponentService;
import com.arcsoft.supervisor.service.server.ServerService;
import com.arcsoft.supervisor.service.task.TaskService;
import com.arcsoft.supervisor.web.ControllerSupport;
import com.arcsoft.supervisor.web.JsonResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * @author zw.
 */
@Controller
@RequestMapping("/device")
public class DeviceController extends ControllerSupport {

    private static final String VIEW_INDEX = "/device/index";

    @Autowired
    private ServerService serverService;

    @Autowired
    private ServerComponentService componentService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private SSHConnectInfoRepository sshConnectInfoRepository;

    @Autowired
    private WallService wallService;

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(Integer pageNo, Model model) {
        pageNo = pageNo == null || pageNo <= 0 ? 0 : pageNo - 1;
        Page<Server> page = serverService.list(pageNo, SupervisorDefs.Constants.PAGE_SIZE);
        model.addAttribute("pageObject", page);
        return VIEW_INDEX;
    }

    @RequestMapping(value = "/scan", method = RequestMethod.GET)
    @ResponseBody
    public List<Server> scan() {
        try {
            return serverService.addAndScanServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @RequestMapping(value = "/gets", method = RequestMethod.GET)
    @ResponseBody
    public List<Server> listByIds(String ids) {
        if (StringUtils.isNotBlank(ids)) {
            return serverService.getByIds(Arrays.asList(ids.split(",")));
        }
        return Collections.emptyList();
    }

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    @ResponseBody
    public Server get(String id) {
        return serverService.getServer(id);
    }

    @RequestMapping(value = "/get_ssh_info", method = RequestMethod.GET)
    @ResponseBody
    public SSHConnectInfo getSshInfo(String id) {
        Server server = serverService.getServer(id);
        return  sshConnectInfoRepository.findByIp(server.getIp());
    }

    @RequestMapping(value = "/join", method = RequestMethod.POST)
    @ResponseBody
    public void join(String deviceJson) {
        try {
            List<Server> servers = JsonMapper.getMapper().readValue(deviceJson, JsonMapper.getMapper().getTypeFactory().constructCollectionType(List.class, Server.class));
            if (servers != null) {
                for (Server server : servers) {
                    serverService.updateToJoinAndSetNameAndRemarkAndActiveFunctions(server);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to join servers.", e);
        }

    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public void update(Server server) {
        serverService.updateNameAndRemarkAndActiveFunctions(server);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public void delete(String json) {
        try {
            List<Server> servers = JsonMapper.getMapper().readValue(json, JsonMapper.getMapper().getTypeFactory().constructCollectionType(List.class, Server.class));
            if (servers != null) {
                serverService.removeServers(servers);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/getStatus", method = RequestMethod.GET)
    @ResponseBody
    public Map<ComponentType, List<ServerComponent>> getStatus(Server server) {
        HashMap<ComponentType, List<ServerComponent>> map = new HashMap<>();
        List<ServerComponent> all = componentService.getByServer(server);
        for (ServerComponent component : all) {
            List<ServerComponent> typedComponent = map.get(component.getType());
            if (typedComponent == null) {
                typedComponent = new ArrayList<>();
                typedComponent.add(component);
                map.put(component.getType(), typedComponent);
            } else {
                typedComponent.add(component);
            }
        }
        return map;
    }

    /**
     * Checks the device name is exits or not.
     *
     * @param name the device name will be checked
     */
    @RequestMapping(value = "/check", method = RequestMethod.GET)
    @ResponseBody
    public JsonResult checkName(String name) {
        JsonResult result = JsonResult.fromSuccess();
        try {
            result.put("exists", serverService.isExistsServerName(name));
        } catch (Exception e) {

        }
        return result;
    }

    @RequestMapping(value = "/tasks/{id}", method = RequestMethod.GET)
    @ResponseBody
    public JsonResult getChannelsAndCountOfTasks(@PathVariable("id") String id) {
        return JsonResult.fromSuccess()
                .put(KEY_OF_RESULT, taskService.getRunningTasksChannelNameOnServer(id));
    }

    @RequestMapping(value = "/al/{wallId}", method = RequestMethod.GET)
    @ResponseBody
    public JsonResult getAliveServers(@PathVariable("wallId") Integer wallId) {
        Wall wall = wallService.getById(wallId);
        if (wall == null || wall.getType() == null) {
            return JsonResult.fromSuccess().put(KEY_OF_RESULT, Collections.emptyList());
        }
        ServerFunction function = wall.getType() == 1 ? ServerFunction.IP_STREAM_COMPOSE : ServerFunction.SDI_STREAM_COMPOSE;
        List<Server> servers = serverService.getJoinedAndAlivedAgentServersWithFunction(function);
        return JsonResult.fromSuccess()
                .put(KEY_OF_RESULT, servers);
    }
}
