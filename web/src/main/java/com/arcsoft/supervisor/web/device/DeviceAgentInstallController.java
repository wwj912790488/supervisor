package com.arcsoft.supervisor.web.device;

import com.arcsoft.supervisor.model.domain.system.SystemSettings;
import com.arcsoft.supervisor.repository.server.SSHConnectInfoRepository;
import com.arcsoft.supervisor.service.device.RemoteShellExecutorService;
import com.arcsoft.supervisor.model.domain.server.SSHConnectInfo;
import com.arcsoft.supervisor.service.system.SystemService;
import com.arcsoft.supervisor.utils.app.Environment;
import com.arcsoft.supervisor.web.api.RestApiControllerSupport;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;


@Controller
public class DeviceAgentInstallController extends RestApiControllerSupport {

    @Autowired
    private RemoteShellExecutorService remoteShellExecutorService;
    @Autowired
    private SSHConnectInfoRepository sshConnectInfoRepository;
    @Autowired
    private SystemService systemService;

    @RequestMapping(value = "/download_app", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public void getAliveServers(HttpServletRequest request, HttpServletResponse response, String fileName) {
        File file = new File(fileName);
        if (file != null && file.exists()) {
            try {
                remoteShellExecutorService.readFileContent(request, response, file);
            } catch (IOException e) {
                e.printStackTrace();

            }
        }

    }

    @RequestMapping(value = "/agentCheck_app", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    @Transactional
    public HashMap<String, Object> checkAgent(Model model, @ModelAttribute SSHConnectInfo sshConnectInfo) {
        HashMap resultMap = new HashMap();
        resultMap.put("status", false);
        resultMap.put("msg", "信息填写不正确");
        if (StringUtils.isBlank(sshConnectInfo.getIp()) || sshConnectInfo.getPort() <= 0 || StringUtils.isBlank(sshConnectInfo.getUser()) || StringUtils.isBlank(sshConnectInfo.getPassword())) {
            return resultMap;
        }
        SystemSettings systemSettings = systemService.getSettings();
        if (systemSettings.getBindAddr() == null) {
            resultMap.put("msg", "集群网卡未初始化");
            return resultMap;
        }
        if (!remoteShellExecutorService.login(sshConnectInfo)) {
            resultMap.put("msg", "服务器连接失败，请检查参数!");
            return resultMap;
        }
        resultMap.put("status", true);
        return resultMap;
    }

    @RequestMapping(value = "/agentInstall_app", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    @Transactional
    public HashMap<String, Object> installAgent(Model model, @ModelAttribute SSHConnectInfo sshConnectInfo) {
        HashMap resultMap = new HashMap();
        resultMap.put("status", false);
        resultMap.put("msg", "信息填写不正确");
        if (StringUtils.isBlank(sshConnectInfo.getIp()) || sshConnectInfo.getPort() <= 0 || StringUtils.isBlank(sshConnectInfo.getUser()) || StringUtils.isBlank(sshConnectInfo.getPassword())) {
            return resultMap;
        }
        SystemSettings systemSettings = systemService.getSettings();
        if (systemSettings.getBindAddr() == null) {
            resultMap.put("msg", "集群网卡未初始化");
            return resultMap;
        }
        String CommanderIP = "http://" + systemSettings.getBindAddr();

        Boolean mosaic = Environment.getProfiler().isMosaic();
        StringBuilder scriptCommand = new StringBuilder();
        //scriptCommand.append("tar -czvf agent.tar.gz repository logging  content-detect agent SDI transcoder ");
        scriptCommand.append("curl -o /supervisorInstallAgent.sh ").append(CommanderIP).append("/download_app?fileName=/home/backup/supervisorInstallAgent.sh");
        scriptCommand.append("&&").append("chmod 777 /supervisorInstallAgent.sh").append("&&");
        scriptCommand.append("sh /supervisorInstallAgent.sh ").append(CommanderIP).append(mosaic ? " Y " : " N ").append(sshConnectInfo.getIp());

        try {
            if (remoteShellExecutorService.execCommand(sshConnectInfo, scriptCommand.toString())) {
                if(sshConnectInfoRepository.findByIp(sshConnectInfo.getIp())==null){
                    sshConnectInfoRepository.save(sshConnectInfo);
                }
                resultMap.put("status", true);
                resultMap.put("msg", "成功运行安装");
            } else {
                resultMap.put("msg", "shell脚本执行失败");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultMap;
    }


    @RequestMapping("/testsshapi")
    @ResponseBody
    public String testSSHApi() {
        try {
            remoteShellExecutorService.execCommand(new SSHConnectInfo("172.17.230.134", 22, "root", "master007"), "df -h&&ls");
        } catch (Exception e) {
        }
        return "test ";
    }

}
