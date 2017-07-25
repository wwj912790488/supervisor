package com.arcsoft.supervisor.service.settings.impl;

import com.arcsoft.supervisor.model.domain.settings.Component;
import com.arcsoft.supervisor.model.domain.settings.Eth;
import com.arcsoft.supervisor.repository.settings.EthRepository;
import com.arcsoft.supervisor.service.settings.LocalComponentService;
import com.arcsoft.supervisor.utils.app.App;
import com.arcsoft.supervisor.utils.app.ShellException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LocalComponentServiceImpl implements LocalComponentService {
	
    private EthRepository ethRepository;
	
	private Logger log = Logger.getLogger(getClass());

	@Override
	public List<Component> list() {
		List<Component> list = new ArrayList<>();
		log.debug("get memory");
		list.addAll(listMemory());
		log.debug("get cpu");
		list.addAll(listCPU());
		log.debug("get gpu");
		list.addAll(listGPU());
		log.debug("get eth");
		list.addAll(listEth());
		return list;
	}
	
	private List<Component> listMemory() {
		ArrayList<Component> ret = new ArrayList<>();
        List<String> memoryList;
		try {
			memoryList = App
			        .runShell("free -g | awk ' NR == 2 { print $2,$3 } '");
		
	        for (String memoryStr : memoryList) {
	        	String[] mem = memoryStr.split(" ");
	        	if(mem.length == 2) {
	        		ret.add(Component.CreateMemoryComponent(Integer.parseInt(mem[0]), Integer.parseInt(mem[1])));
	        	}
	        }
		} catch (ShellException | NumberFormatException e) {
			log.info(e);
		}
		return ret;
	}
	
	private List<Component> listCPU() {
		ArrayList<Component> ret = new ArrayList<>();
		List<String> cpuList;
		try {
			cpuList = App.runShell("mpstat -P ALL 1 1 | awk ' NR > 4 { if(NF > 0) print $3,$4+$5+$6; else exit; } '");
			log.debug(cpuList);
			for(String cpuStr : cpuList) {
				String[] cpu = cpuStr.split(" ");
				if(cpu.length == 2) {
					ret.add(Component.CreateCPUComponent(cpu[0], (int)Float.parseFloat(cpu[1])));
				}
			}
		} catch(ShellException | NumberFormatException e) {
			log.error(e);
		}
		return ret;
	}
	
	private List<Component> listGPU() {
		ArrayList<Component> ret = new ArrayList<>();
		List<String> gpuList;
		try {
			gpuList = App
					.runShell("nvidia-smi --query-gpu=uuid,utilization.gpu --format=csv,noheader | awk -F, ' { gsub(/[^0-9]/,\"\",$2); print } ' ");
			log.debug(gpuList);
			for(String gpuStr : gpuList) {
				String[] gpu = gpuStr.split(" ");
				if(gpu.length == 2) {
					ret.add(Component.CreateGPUComponent(gpu[0], Integer.parseInt(gpu[1])));
				}
			}
		} catch(ShellException | NumberFormatException e) {
			log.error(e);
		}
		return ret;
	}
	
	private List<Component> listEth() {
		ArrayList<Component> ret = new ArrayList<>();
		try {
			List<Eth> ethList = ethRepository.findAllEths();
			for(Eth eth : ethList) {
				ret.add(Component.CreateEthComponent(eth.getId(), eth.getRate()));
			}
		} catch (ShellException | IOException e) {
			
		}
		return ret;
	}

	public EthRepository getEthRepository() {
		return ethRepository;
	}

	public void setEthRepository(EthRepository ethRepository) {
		this.ethRepository = ethRepository;
	}

}
