package com.arcsoft.supervisor.web.settings;

import com.arcsoft.supervisor.model.domain.settings.DNS;
import com.arcsoft.supervisor.model.domain.settings.Eth;
import com.arcsoft.supervisor.model.domain.settings.Route;
import com.arcsoft.supervisor.service.server.ServerService;
import com.arcsoft.supervisor.service.settings.*;
import com.arcsoft.supervisor.utils.app.ShellException;
import com.arcsoft.supervisor.web.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping(value = "/network")
public class NetworkController extends SettingsControllerSupport {

    private static final String VIEW_INDEX = "/settings/network/index";

    @Autowired
    private LocalEthService localEthService;

    @Autowired
    private LocalDNSService localDNSService;

    @Autowired
    private LocalRouteService localRouteService;

    @Autowired
    private RemoteDNSService remoteDNSService;

    @Autowired
    private RemoteRouteService remoteRouteService;

    @Autowired
    private RemoteEthService remoteEthService;

    @Autowired
    private ServerService serverService;


    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index() {
        return VIEW_INDEX;
    }

    @RequestMapping(value = "/eth/{id}", method = RequestMethod.GET)
    @ResponseBody
    public List<Eth> listEth(@PathVariable("id") String id) throws ShellException, IOException {
        if (CLUSTER_SERVER_ID.equals(id)) {
            return localEthService.findAllEths();
        }
        return remoteEthService.findAllEths(serverService.getServer(id));
    }

    @RequestMapping(value = "/eth/{id}", method = {RequestMethod.POST, RequestMethod.PUT})
    @ResponseBody
    public JsonResult updateEth(@PathVariable("id") String id, @RequestBody Eth eth) throws ShellException, IOException {
        if (CLUSTER_SERVER_ID.equals(id)) {
            localEthService.updateEth(eth);
        } else {
            remoteEthService.updateEth(serverService.getServer(id), eth);
        }
        return JsonResult.fromSuccess();
    }

    @RequestMapping(value = "/dns/{id}", method = RequestMethod.GET)
    @ResponseBody
    public List<DNS> getDns(@PathVariable("id") String id) throws Exception {
        if (CLUSTER_SERVER_ID.equals(id)) {
            return localDNSService.getDnSList();
        }
        return remoteDNSService.getDnsList(serverService.getServer(id));
    }

    @RequestMapping(value = "/dns/{id}", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult updateDns(@PathVariable("id") String id, @RequestBody DNS dns) throws Exception {
        if (CLUSTER_SERVER_ID.equals(id)) {
            localDNSService.addDns(dns);
        } else {
            remoteDNSService.addDns(serverService.getServer(id), dns);
        }
        return JsonResult.fromSuccess();
    }

    @RequestMapping(value = "/dns/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public JsonResult deleteDns(@PathVariable("id") String id, @RequestBody DNS dns) throws Exception {
        if (CLUSTER_SERVER_ID.equals(id)) {
            localDNSService.deleteDns(dns);
        } else {
            remoteDNSService.deleteDns(serverService.getServer(id), dns);
        }
        return JsonResult.fromSuccess();
    }

    @RequestMapping(value = "/route/{id}", method = RequestMethod.GET)
    @ResponseBody
    public List<Route> listRoute(@PathVariable("id") String id) throws Exception {
        if (CLUSTER_SERVER_ID.equals(id)) {
            return localRouteService.getRoutes();
        }
        return remoteRouteService.getRoutes(serverService.getServer(id));
    }

    @RequestMapping(value = "/route/{id}", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult addRoute(@PathVariable("id") String id, @RequestBody Route route) throws Exception {
        if (CLUSTER_SERVER_ID.equals(id)) {
            localRouteService.addRoute(route);
        } else {
            remoteRouteService.addRoute(serverService.getServer(id), route);
        }
        return JsonResult.fromSuccess();
    }

    @RequestMapping(value = "/route/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public JsonResult deleteRoute(@PathVariable("id") String id, @RequestBody Route route) throws Exception {
        if (CLUSTER_SERVER_ID.equals(id)) {
            localRouteService.deleteRoute(route);
        } else {
            remoteRouteService.deleteRoute(serverService.getServer(id), route);
        }
        return JsonResult.fromSuccess();
    }


}
