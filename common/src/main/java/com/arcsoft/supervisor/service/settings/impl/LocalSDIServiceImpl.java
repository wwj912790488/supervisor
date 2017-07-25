package com.arcsoft.supervisor.service.settings.impl;

import com.arcsoft.supervisor.service.settings.LocalSDIService;
import com.arcsoft.supervisor.transcoder.AppConfig;
import com.arcsoft.supervisor.utils.app.App;
import com.arcsoft.supervisor.utils.app.ShellException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalSDIServiceImpl implements LocalSDIService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LocalSDIServiceImpl.class);
	
	private Map<String, Process> processMap = new HashMap<String, Process>();
	
	private String recognizeApp = "/usr/local/arcsoft/arcvideo/transcoder-supervisor/SDIRecognize";
	
	@Override
	public List<String> list() {
		try {
			System.out.println(AppConfig.getProperty("appdir"));
			recognizeApp=AppConfig.getProperty("appdir")!=null?AppConfig.getProperty("appdir")+"/transcoder-supervisor/SDIRecognize":recognizeApp;
			List<String> sdiNames = App.runShell(recognizeApp);
			return sdiNames;
		} catch (ShellException e) {
			LOGGER.error("Failed to get sdi.", e);
			return Collections.emptyList();
		}
	}

	@Override
	public void recognize(String name, int number) {
		Process proc = processMap.get(name);
		if(proc != null) {
			if(isProcessExit(proc)) {		
				processMap.remove(name);
			} else {
				proc.destroy();
				processMap.remove(name);
			}
		}
		processMap.put(name, createProcess(name, number));	
	}
	
	private boolean isProcessExit(Process proc) {
		boolean ret = true;
		try {
			proc.exitValue();
		} catch (IllegalThreadStateException ex) {
			ret = false;
		}
		return ret;
	}
	
	private Process createProcess(String name, int number) {
		Process proc = null;
		LOGGER.info("create recognize with name "  + name);
		String[] command = { recognizeApp, "-sditx", name, "unused", Integer.toString(number) };
		try {
			ProcessBuilder pb = new ProcessBuilder(command);
			String dir="/usr/local/arcsoft/arcvideo/transcoder-supervisor/";
			dir=AppConfig.getProperty("appdir")!=null?AppConfig.getProperty("appdir")+"/transcoder-supervisor/":dir;
			pb.directory(new File(dir));
			proc = pb.start();

		} catch (Exception e) {
			 LOGGER.error("start process failed", e);
		}

		return proc;
	}

}
