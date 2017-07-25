package com.arcsoft.supervisor.service.settings.impl;

import com.arcsoft.supervisor.model.domain.settings.Eth;
import com.arcsoft.supervisor.repository.settings.EthRepository;
import com.arcsoft.supervisor.service.settings.LocalEthService;
import com.arcsoft.supervisor.utils.app.ShellException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.*;

/**
 * The implementation of LocalEthService
 *
 * @author xpeng
 * @author zw
 */
@Service
public class LocalEthServiceImpl implements LocalEthService {

    private Logger log = LoggerFactory.getLogger(LocalEthServiceImpl.class);
    @Autowired
    private EthRepository ethRepository;

    public void setEthRepository(EthRepository ethRepository) {
        this.ethRepository = ethRepository;
    }

    @Override
    public List<Eth> getValidEths() throws ShellException, IOException {
        List<Eth> newEths = new ArrayList<>();
        List<Eth> eths = ethRepository.findAllEths();
        if (eths != null && !eths.isEmpty()) {
            List<String> bonds = new ArrayList<>();
            for (Eth eth : eths) {
                if (eth.getIsbond())
                    bonds.add(eth.getId());
            }
            for (Eth eth : eths) {
                if (eth.getMaster() == null || !bonds.contains(eth.getMaster()))
                    newEths.add(eth);
            }
        }
        return newEths;
    }

    @Override
    public List<Eth> findAllEths() throws ShellException, IOException {
        return ethRepository.findAllEths();
    }

    @Override
    public void updateEth(Eth eth) throws ShellException, IOException {
        ethRepository.updateEth(eth);
    }

    private Eth getBackupEth(List<Eth> allEths, Eth srcEth) {
        Eth ret = null;
        if (srcEth.getMaster() != null) {
            for (Eth e : allEths) {
                if (srcEth.getId().equals(e.getId()))
                    continue;
                if (srcEth.getMaster().equals(e.getMaster())) {
                    ret = e;
                    break;
                }
            }
        }
        return ret;
    }

    private String getBondId(List<Eth> eths) {
        String ret = null;
        for (int i = 0; i < 16; i++) {
            ret = "bond" + i;
            boolean exist = false;
            for (Eth eth : eths) {
                if (eth.getId().equals(ret)) {
                    exist = true;
                    break;
                }
            }
            if (!exist)
                break;
        }
        return ret;
    }

    @Override
    public void bondAndUpdateEth(Eth eth, String slaveId)
            throws ShellException, IOException {
        List<Eth> eths = ethRepository.findAllEths();
        Eth eth1 = null;
        Eth eth2 = null;
        for (Eth theEth : eths) {
            if (theEth.getId().equals(eth.getId())) {
                eth1 = theEth;
            }
            if (slaveId != null && theEth.getId().equals(slaveId)) {
                eth2 = theEth;
            }
        }
        if (eth1 == null || (slaveId != null && eth2 == null)) {
            log.error("can not find the eth");
            throw new IOException("input parameter is error");
        }

        //the eth is already bonded.
        if (eth1.getMaster() != null) {
            Eth backEth = getBackupEth(eths, eth1);
            if (slaveId == null) {
                Eth master = new Eth(eth1.getMaster());
                ethRepository.bond(master, null);
                ethRepository.updateEth(eth);
            } else {
                if (slaveId.equals(backEth.getId())) {
                    eth.setId(eth1.getMaster());
                    eth.setIsbond(true);
                    ethRepository.updateEth(eth);
                } else {
                    Eth master = new Eth(eth1.getMaster());
                    ethRepository.bond(master, null);
                    String slaveId1 = eth.getId();
                    eth.setId(getBondId(eths));
                    ethRepository.bond(eth, new String[]{slaveId1, slaveId});
                }
            }
        } else {
            if (slaveId == null) {
                ethRepository.updateEth(eth);
            } else {
                String slaveId1 = eth.getId();
                eth.setId(getBondId(eths));
                ethRepository.bond(eth, new String[]{slaveId1, slaveId});
            }
        }
    }

    @Override
    public Map<String, String> getAllEthsIpAndIdPair() throws IOException, ShellException {
        HashMap<String, String> idAndIpPairs = new LinkedHashMap<>();
        List<Eth> eths = getValidEths();
        for (Eth eth : eths) {
            if (StringUtils.isNotBlank(eth.getIp())) {
                idAndIpPairs.put(eth.getIp(), eth.getId() + "(" + eth.getIp() + ")");
            }
        }
        if (idAndIpPairs.isEmpty()) {
            Enumeration<NetworkInterface> inetfs = NetworkInterface.getNetworkInterfaces();
            while (inetfs.hasMoreElements()) {
                NetworkInterface inetf = inetfs.nextElement();
                if (inetf.isLoopback() || !inetf.isUp() || inetf.isPointToPoint())
                    continue;
                Enumeration<InetAddress> addrs = inetf.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    InetAddress addr = addrs.nextElement();
                    if (!addr.isLinkLocalAddress() && !addr.isLoopbackAddress()) {
                        String hostIp = addr.getHostAddress();
                        if (StringUtils.isNotBlank(hostIp)) {
                            idAndIpPairs.put(hostIp, inetf.getName() + "(" + hostIp + ")");
                            break;
                        }

                    }
                }
            }
        }
        return idAndIpPairs;
    }

    @Override
    public int getEthUsedRate(String ethId) throws ShellException, IOException {
        return ethRepository.getEthUsedRate(ethId);
    }

}
