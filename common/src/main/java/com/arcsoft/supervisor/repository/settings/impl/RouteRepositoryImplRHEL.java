package com.arcsoft.supervisor.repository.settings.impl;

import com.arcsoft.supervisor.model.domain.settings.Route;
import com.arcsoft.supervisor.repository.settings.RouteRepository;
import com.arcsoft.supervisor.utils.StringHelper;
import com.arcsoft.supervisor.utils.app.App;
import com.arcsoft.supervisor.utils.app.ShellException;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Route manager for centos
 *
 * @author hxiang
 */
@Repository
public class RouteRepositoryImplRHEL implements RouteRepository {
    private static final String CONFIG_DIR = "/etc/sysconfig/network-scripts";

    @Override
    public List<Route> getRoutes() throws ShellException {
        ArrayList<Route> ret = new ArrayList<Route>();
        // destination, netMask, gateway, metric, iface.
        List<String> strRoutes = App
                .runShell("route -n | awk 'NR>2 {print $1,$2,$3,$4,$5,$6,$7,$8}'");
        for (String strRoute : strRoutes) {
            String[] values = strRoute.split(" ");
            Route route = new Route(values[0], values[2], values[1], values[7]);
            route.setMetric(values[4]);
            route.setFlags(values[3]);
            route.setUse(values[6]);
            route.setRef(values[5]);
            ret.add(route);
        }
        return ret;
    }

    @Override
    public void add(Route route) throws ShellException {
        App.runShell("route add " + createShellParam(route));
        // add route to route-?
        List<Route> routeList = loadRoutesByIface(route.getIface());
        int index = findRoute(route, routeList);
        if (index < 0) {
            routeList.add(route);
            update(route.getIface(), routeList);
        }
    }

    @Override
    public void delete(Route route) throws ShellException {
        App.runShell("route del" + createShellParam(route));
        List<Route> routeList = loadRoutesByIface(route.getIface());
        int index = findRoute(route, routeList);
        if (index >= 0) {
            routeList.remove(index);
            update(route.getIface(), routeList);
        }
    }

    private void update(String iface, List<Route> routes) throws ShellException {
        StringBuilder strBuild = new StringBuilder();
        for (int i = 0; i < routes.size(); i++) {
            Route route = routes.get(i);
            strBuild.append("ADDRESS").append(i).append("=")
                    .append(route.getDest()).append("\n");
            strBuild.append("NETMASK").append(i).append("=")
                    .append(route.getMask()).append("\n");
            if (!StringHelper.isEmpty(route.getGateway())) {
                strBuild.append("GATEWAY").append(i).append("=")
                        .append(route.getGateway()).append("\n");
            }
        }
        App.runShell("echo -e '" + strBuild.toString() + "' >"
                + configPath(iface));
    }

    private List<Route> loadRoutesByIface(String iface) {
        ArrayList<Route> ret = new ArrayList<Route>();
        File fRoute = new File(configPath(iface));
        if (fRoute.exists()) {
            Properties prop = new Properties();
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(fRoute);
                prop.load(fis);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < 1024; i++) {
                String gw = prop.getProperty("GATEWAY" + i);
                String dest = prop.getProperty("ADDRESS" + i);
                String mask = prop.getProperty("NETMASK" + i);
                if ((gw == null || gw.length() == 0)
                        && (dest == null || dest.length() == 0)
                        && (mask == null || mask.length() == 0))
                    break;
                Route route = new Route(dest, mask, gw, iface);
                ret.add(route);
            }
        }
        return ret;
    }

    private int findRoute(Route route, List<Route> routeList) {
        int ret = -1;
        for (int i = 0; i < routeList.size(); i++) {
            Route each = routeList.get(i);
            if (each.getIface().equals(route.getIface())
                    && each.getMask().equals(route.getMask())
                    && each.getDest().equals(route.getDest())) {
                ret = i;
                break;
            }
        }
        return ret;
    }

    private String configPath(String iface) {
        return CONFIG_DIR + "/route-" + iface;
    }

    private String createShellParam(Route route) {
        String param = " -net " + route.getDest() + " netmask "
                + route.getMask();
        /*if (!StringHelper.isEmpty(route.getGateway())) {
            param += (" gw " + route.getGateway());
        }*/

        if (!StringHelper.isEmpty(route.getIface())) {
            param += (" dev " + route.getIface());
        }
        return param;
    }
}
