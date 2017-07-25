package com.arcsoft.supervisor.repository.settings;

import com.arcsoft.supervisor.model.domain.settings.FirewallRule;
import com.arcsoft.supervisor.utils.app.ShellException;

import java.util.List;

/**
 * 
 * 
 * @author hxiang
 *
 */
public interface FirewallRepository {
	public abstract List<FirewallRule> getFirewallRules() throws ShellException;

	public abstract void add(FirewallRule rule) throws ShellException;

	public abstract void delete(FirewallRule rule) throws ShellException;

	public abstract void start() throws ShellException;

	public abstract void stop() throws ShellException;
	
	public abstract boolean  isServiceOn();
}