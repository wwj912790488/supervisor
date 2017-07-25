package com.arcsoft.supervisor.repository.settings.impl;

import com.arcsoft.supervisor.model.domain.settings.FirewallRule;
import com.arcsoft.supervisor.repository.settings.FirewallRepository;
import com.arcsoft.supervisor.utils.app.App;
import com.arcsoft.supervisor.utils.app.ShellException;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hxiang manage iptable.
 */
@Repository
public class FirewallRepositoryImplRHEL implements FirewallRepository {

    @Override
    public List<FirewallRule> getFirewallRules() throws ShellException {
        ArrayList<FirewallRule> rules = new ArrayList<FirewallRule>();
        List<String> handledRules = new ArrayList<String>();
        List<String> strRules = App
                .runShell("iptables -S INPUT | awk '/-j ACCEPT/' | awk '/--dport/{print $(NF-4),$(NF-2)}'");

        for (String strRule : strRules) {
            if (handledRules.contains(strRule)) // filter the same rule;
                continue;
            handledRules.add(strRule);
            FirewallRule rule = new FirewallRule();
            String[] array = strRule.split(" ");
            rule.setDport(array[1]);
            rule.setProtocol(array[0]);
            rule.setTarget("ACCEPT");
            rules.add(rule);
        }
        return rules;
    }

    @Override
    public void add(FirewallRule rule) throws ShellException {
        preProcess(rule);
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(" INPUT -j ACCEPT");
        strBuilder.append(" -p ").append(rule.getProtocol());
        strBuilder.append(" --dport ").append(rule.getDport());
        App.runShell("iptables -I" + strBuilder.toString());
        App.runShell("service iptables save");
    }

    @Override
    public void delete(FirewallRule rule) throws ShellException {
        preProcess(rule);
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("iptables -L INPUT --line-number -n");
        if (rule.getDport().contains(":")) {
            strBuilder.append("| awk '$(NF-1)==\""
                    + rule.getProtocol().toLowerCase() + "\"&&"
                    + "$(NF)==\"dpts:" + rule.getDport().toLowerCase()
                    + "\"{print $1}'");
        } else {
            strBuilder.append("| awk '$(NF-1)==\""
                    + rule.getProtocol().toLowerCase() + "\"&&"
                    + "$(NF)==\"dpt:" + rule.getDport().toLowerCase()
                    + "\"{print $1}'");
        }
        List<String> indexList = App.runShell(strBuilder.toString());
        for (int i = indexList.size() - 1; i >= 0; i--) {
            // delete by index desc.
            // have the firewall in iptables,remove it.
            App.runShell("iptables -D INPUT " + indexList.get(i));
        }
        App.runShell("service iptables save");
    }

    @Override
    public void start() throws ShellException {
        App.runShell("service iptables  start");
    }

    @Override
    public void stop() throws ShellException {
        App.runShell("service iptables  stop");
    }

    public boolean isServiceOn() {
        boolean ret = true;
        try {
            App.runShell("service iptables status");
        } catch (ShellException e) {
            ret = false;
        }
        return ret;
    }

    private void preProcess(FirewallRule rule) {
        rule.setDport(rule.getDport().replace("-", ":"));
        if (rule.getDport().contains(":")) {
            String ports[] = rule.getDport().split(":");
            try {
                int start_port = new Integer(ports[0]);
                int end_port = new Integer(ports[1]);
                StringBuilder builder = new StringBuilder();
                builder.append(Math.min(start_port, end_port));
                builder.append(":");
                builder.append(Math.max(start_port, end_port));
                rule.setDport(builder.toString());
            } catch (NumberFormatException e) {
                // TODO:
            }
        }
    }
}
