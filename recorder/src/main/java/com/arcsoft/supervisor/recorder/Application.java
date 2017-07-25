package com.arcsoft.supervisor.recorder;

import com.arcsoft.supervisor.recorder.data.RecordAgentInfo;
import com.arcsoft.supervisor.recorder.data.RecordTaskParam;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.net.URL;

public class Application {
	
	public static RecordApp app = new RecordApp();
	
	private static Logger log = Logger.getLogger(Application.class);
	
	private static final String DEFAULT_LOG4J = "config/log4j.properties";

	public static void main(String[] args) {
		configureLog4j();
		
		app.setRecordAgentInfo(new RecordAgentInfo());		
		app.Init();
		
		RecordTaskParam param = new RecordTaskParam();
		param.setUrl("udp://239.1.1.1:1234");
		param.setTask_id(1);
		
		try {
			log.debug("start task");
			app.startTask(param);
		} catch (StartRecordTaskException e) {
			log.debug("start task failed", e);
		}
		
		
	}
	
	private static void configureLog4j() {
		// setup log4j use the specified file.
		String log4jConfigFile = System.getProperty("recorder.log4j");
		if (log4jConfigFile != null) {
			File file = new File(log4jConfigFile);
			if (file.exists()) {
				PropertyConfigurator.configure(log4jConfigFile);
				return;
			}
		}

		// setup log4j using default configuration.
		URL url = Application.class.getClassLoader().getResource(DEFAULT_LOG4J);
		if (url != null) {
			PropertyConfigurator.configure(url);
		}
	}
}
