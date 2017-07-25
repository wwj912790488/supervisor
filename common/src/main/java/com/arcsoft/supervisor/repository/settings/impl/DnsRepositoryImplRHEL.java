package com.arcsoft.supervisor.repository.settings.impl;

import com.arcsoft.supervisor.model.domain.settings.DNS;
import com.arcsoft.supervisor.repository.settings.DnsRepository;
import com.arcsoft.supervisor.utils.app.App;
import com.arcsoft.supervisor.utils.app.ShellException;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Dns manager for centos
 *
 * @author hxiang
 * @author zw
 */
@Repository
public class DnsRepositoryImplRHEL implements DnsRepository {

    private static final String CONFIG_PATH = "/etc/resolv.conf";

    @Override
    public List<DNS> getDnsList() throws ShellException {
        ArrayList<DNS> ret = new ArrayList<DNS>();
        List<String> dnsList = App
                .runShell("awk '$1==\"nameserver\"{print $2}' " + CONFIG_PATH);
        for (String dns : dnsList)
            ret.add(new DNS(dns));
        return ret;
    }

    @Override
    public void add(DNS dns) throws ShellException {
        App.runShell("if [ -f " + CONFIG_PATH + " ]; then sed -i '/^nameserver \\{1,\\}" + dns.getIp() + "$/d' " + CONFIG_PATH + ";fi;");
        App.runShell("echo 'nameserver " + dns.toString() + "' >> "
                + CONFIG_PATH);
    }

    @Override
    public void delete(DNS dns) throws ShellException {
        // remove the line have 'nameserver %1'
        App.runShell("if [ -f " + CONFIG_PATH + " ]; then sed -i '/^nameserver \\{1,\\}" + dns.getIp() + "$/d' " + CONFIG_PATH + ";fi;");
    }

    @Override
    public void reset(List<DNS> dnsItems) throws ShellException {
        for (DNS dns : getDnsList()){
            delete(dns);
        }

        for (DNS dns : dnsItems){
            add(dns);
        }
    }
}
