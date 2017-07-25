package com.arcsoft.supervisor.service.settings;

import com.arcsoft.supervisor.model.domain.settings.DNS;

import java.util.List;

/**
 * Local DNS Service.
 * 
 * @author hxiang
 */
public interface LocalDNSService {

	List<DNS> getDnSList() throws Exception;

	void addDns(DNS dns) throws Exception;

	void deleteDns(DNS dns) throws Exception;

    void reset(List<DNS> dnsItems) throws Exception;

}
