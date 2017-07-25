package com.arcsoft.supervisor.service.settings.impl;

import com.arcsoft.supervisor.model.domain.settings.FirewallRule;
import com.arcsoft.supervisor.repository.settings.FirewallRepository;
import com.arcsoft.supervisor.service.settings.LocalFirewallService;
import com.arcsoft.supervisor.utils.app.ShellException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The implementation of LocalFirewallService.
 *
 * @author hxiang
 * @author zw
 */
@Service
public class LocalFirewallServiceImpl implements LocalFirewallService {

    @Autowired
    private FirewallRepository firewallRepository;

    @Override
    public List<FirewallRule> getFirewalls() throws Exception {
        return firewallRepository.getFirewallRules();
    }

    @Override
    public void addFirewall(FirewallRule rule) throws Exception {
        firewallRepository.add(rule);
    }

    @Override
    public void deleteFirewall(FirewallRule rule) throws Exception {
        firewallRepository.delete(rule);
    }

    @Override
    public void startFirewall() throws ShellException {
        firewallRepository.start();
    }

    @Override
    public void stopFirewall() throws ShellException {
        firewallRepository.stop();
    }

    @Override
    public boolean isFirewallOn() {
        return firewallRepository.isServiceOn();
    }

}
