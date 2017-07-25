package com.arcsoft.supervisor.service.settings.impl;

import com.arcsoft.supervisor.model.domain.settings.DNS;
import com.arcsoft.supervisor.repository.settings.DnsRepository;
import com.arcsoft.supervisor.service.settings.LocalDNSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The implementation of LocalDNSService.
 * 
 * @author hxiang
 * @author zw
 */
@Service
public class LocalDNSServiceImpl implements LocalDNSService {

    @Autowired
	private DnsRepository dnsRepository;

    public void setDnsRepository(DnsRepository dnsRepository) {
        this.dnsRepository = dnsRepository;
    }

    @Override
	public List<DNS> getDnSList() throws Exception {
		return dnsRepository.getDnsList();
	}

	@Override
	public void addDns(DNS dns) throws Exception {
		dnsRepository.add(dns);
	}

	@Override
	public void deleteDns(DNS dns) throws Exception {
		dnsRepository.delete(dns);
	}

    @Override
    public void reset(List<DNS> dnsItems) throws Exception {
        dnsRepository.reset(dnsItems);
    }

}
