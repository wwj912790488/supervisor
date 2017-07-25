package com.arcsoft.supervisor.web.settings;

import com.arcsoft.supervisor.cluster.ClusterType;
import com.arcsoft.supervisor.model.domain.system.SystemSettings;
import com.arcsoft.supervisor.service.server.ServerService;
import com.arcsoft.supervisor.service.settings.LocalEthService;
import com.arcsoft.supervisor.service.settings.LocalHostService;
import com.arcsoft.supervisor.service.settings.RemoteHostService;
import com.arcsoft.supervisor.service.system.SystemService;
import com.arcsoft.supervisor.utils.app.ShellException;
import com.arcsoft.supervisor.web.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;


/**
 * Controller class for {@code Host}.
 *
 * @author zw.
 */
@Controller
@RequestMapping("/host")
public class HostController extends SettingsControllerSupport {

    private SystemService systemService;

    private LocalEthService localEthService;

    private static final String DEFAULT_CLUSTER_IP = "239.22.33.44";

    private static final int DEFAULT_CLUSTER_PORT = 8901;

    @Autowired
    private LocalHostService localHostService;

    @Autowired
    private RemoteHostService remoteHostService;

    @Autowired
    private ServerService serverService;

    @Autowired
    public HostController(SystemService systemService, LocalEthService localEthService) {
        this.systemService = systemService;
        this.localEthService = localEthService;
    }

    @RequestMapping(value = "/saveDeleteBeforeDays", method = RequestMethod.POST)
    @ResponseBody
    public void restart(Integer day) throws Exception {
        SystemSettings settings = systemService.getSettings();
        settings.setAlertAutoDeleteDays(day);
        systemService.saveSettings(settings);
    }


    @RequestMapping(value = "/getSettings", method = RequestMethod.GET)
    @ResponseBody
    public JsonResult getSystemSettings() throws IOException, ShellException {
        JsonResult result = JsonResult.fromSuccess();
        result.put("settings",  getAndSetDefaultSystemSettings());
        result.put("eths", localEthService.getAllEthsIpAndIdPair());
        return result;
    }

    private SystemSettings getAndSetDefaultSystemSettings() {
        SystemSettings settings = systemService.getSettings();
        if (settings.getClusterIp() == null){
            settings.setClusterIp(DEFAULT_CLUSTER_IP);
        }
        if (settings.getClusterPort() == null){
            settings.setClusterPort(DEFAULT_CLUSTER_PORT);
        }
        if (settings.getClusterType() == null){
            settings.setClusterType(ClusterType.CORE);
        }
        return settings;
    }

    @RequestMapping(value = "/init", method = RequestMethod.POST)
    @ResponseBody
    public void initializeSystem(SystemSettings settings) {
        settings.setClusterType(ClusterType.CORE);
        systemService.saveSettings(settings);
    }

    @RequestMapping(value = "/restart/{id}", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult restart(@PathVariable String id) throws Exception {
        if (CLUSTER_SERVER_ID.equals(id)) {
            localHostService.reboot();
        } else {
            remoteHostService.reboot(serverService.getServer(id));
        }
        return JsonResult.fromSuccess();
    }

    @RequestMapping(value = "/shutdown/{id}", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult shutdown(@PathVariable String id) throws Exception {
        if (CLUSTER_SERVER_ID.equals(id)) {
            localHostService.shutdown();
        } else {
            remoteHostService.shutdown(serverService.getServer(id));
        }
        return JsonResult.fromSuccess();
    }

}
