package com.arcsoft.supervisor.repository.settings;

import com.arcsoft.supervisor.model.domain.settings.DNS;
import com.arcsoft.supervisor.utils.app.ShellException;

import java.util.List;

/**
 * @author hxiang
 * 
 */
public interface DnsRepository {

	public abstract List<DNS> getDnsList() throws ShellException;

	public abstract void add(DNS dns) throws ShellException;

	public abstract void delete(DNS dns) throws ShellException;

    public void reset(List<DNS> dnsItems) throws ShellException;

}
