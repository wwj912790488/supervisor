package com.arcsoft.supervisor.service.settings.impl;

import com.arcsoft.supervisor.repository.settings.HostRepository;
import com.arcsoft.supervisor.service.settings.LocalHostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The implementation of LocalHostService
 *
 * @author hxiang
 * @author zw
 */
@Service
public class LocalHostServiceImpl implements LocalHostService {

    @Autowired
    private HostRepository hostRepository;

    public void setHostRepository(HostRepository hostRepository) {
        this.hostRepository = hostRepository;
    }

    @Override
    public void reboot() throws Exception {
        hostRepository.reboot();
    }

    @Override
    public void shutdown() throws Exception {
        hostRepository.shutdown();
    }

}
