package com.arcsoft.supervisor.repository.settings;


import com.arcsoft.supervisor.model.domain.settings.Eth;
import com.arcsoft.supervisor.repository.settings.impl.EthRepositoryImplRHEL;
import com.arcsoft.supervisor.utils.app.ShellException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class EthRepositoryTest {
	final boolean RUN_ON_RHEL = false;
	private EthRepository ethRepository;

	@Before
	public void setUp() {
		ethRepository = new EthRepositoryImplRHEL();
	}

	@Test
	public void testFindAllEths() throws ShellException, IOException {
		if (RUN_ON_RHEL) {
			List<Eth> eths = ethRepository.findAllEths();
			for(Eth eth : eths){
				System.out.println("id: " + eth.getId());
				System.out.println("ip: " + eth.getIp());
				System.out.println("speed: " + eth.getSpeed()); 
			}
			assertNotNull(eths);
			assertTrue(eths.size() > 0);
		}
	}

	@Test
	public void testUpdateEth() throws IOException, ShellException {
		if (RUN_ON_RHEL) {
			Eth oldEth = ethRepository.findAllEths().get(0);

			// case: change name
			Eth eth = Eth.copy(oldEth);			
			String expectName = "new eth name";
			eth.setName(expectName);
			ethRepository.updateEth(eth);
			Eth actual = ethRepository.findAllEths().get(0);
			assertEquals(expectName, actual.getName());
			ethRepository.updateEth(oldEth);

			// case: static, change ip, gateway, mask
			eth = Eth.copy(oldEth);		
			String expectIp = "172.17.186.91";
			String expectMask = "255.255.255.255";
			String expectGateWay = "172.17.160.1";
			eth.setIsDHCP(false);
			eth.setIp(expectIp);
			eth.setMask(expectMask);
			eth.setGateway(expectGateWay);
			ethRepository.updateEth(eth);
			actual = ethRepository.findAllEths().get(0);
			assertEquals(expectIp, actual.getIp());
			assertEquals(expectMask, actual.getMask());
			assertEquals(expectGateWay, actual.getGateway());
			ethRepository.updateEth(oldEth);

			// case: DHCP
			eth = Eth.copy(oldEth);		
			eth.setIsDHCP(true);
			ethRepository.updateEth(eth);
			actual = ethRepository.findAllEths().get(0);
			assertEquals(true, actual.getIsDHCP());
			ethRepository.updateEth(oldEth);
		}
	}
	
	@Test
	public void testGetEthUsedRate() throws ShellException, IOException {
		if (RUN_ON_RHEL) {
			Eth eth = ethRepository.findAllEths().get(0);
			int usedRate = ethRepository.getEthUsedRate(eth.getId());
			assertTrue(usedRate >= 0);
			assertTrue(usedRate < 100);
		}
		
	}
	
	@Test
	public void testBond() throws IOException, ShellException{
		if(RUN_ON_RHEL){
			Eth master = new Eth("bond0");
			master.setIp("192.168.1.201");
			master.setMask("255.255.255.0");
			String[] slaves = new String[]{"eth2", "eth3"};			
			ethRepository.bond(master, slaves);
			
			List<Eth> eths = ethRepository.findAllEths();
			int bonds = 0;
			for(Eth eth:eths){
				if(eth.getIsbond()){
					bonds++;
				}				
			}			
			assertEquals(1, bonds);
			
			ethRepository.bond(master, null);
			eths = ethRepository.findAllEths();
			bonds = 0;
			for(Eth eth:eths){
				if(eth.getIsbond()){
					bonds++;
				}				
			}			
			assertEquals(0, bonds);
		}
		
	}

}
