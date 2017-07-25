package com.arcsoft.supervisor.service.settings;

import com.arcsoft.supervisor.model.domain.settings.FirewallRule;
import com.arcsoft.supervisor.utils.app.ShellException;

import java.util.List;

/**
 * Local firewall service.
 * 
 * @author hxiang
 */
public interface LocalFirewallService {

	List<FirewallRule> getFirewalls() throws Exception;

	void addFirewall(FirewallRule rule) throws Exception;

	void deleteFirewall(FirewallRule rule) throws Exception;

	void startFirewall() throws ShellException;

	void stopFirewall() throws ShellException;

	boolean isFirewallOn();

}
