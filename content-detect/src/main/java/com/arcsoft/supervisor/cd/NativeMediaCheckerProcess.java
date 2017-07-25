package com.arcsoft.supervisor.cd;

import org.apache.log4j.Logger;

import java.io.File;

public class NativeMediaCheckerProcess {

	private Logger logger = Logger.getLogger(NativeMediaCheckerProcess.class);

	public Process createProcess(String app, int listenPort, int serverPort) {
		logger.info("listen, server port(" + listenPort + ", " + serverPort + ")");
		Process proc = null;
		String slistenPort = Integer.toString(listenPort);
		String startP = Integer.toString(serverPort);
		String[] command = { app, "127.0.0.1", slistenPort, startP };
		try {
			ProcessBuilder pb = new ProcessBuilder(command);
			pb.directory(new File(app).getParentFile());
			proc = pb.start();

		} catch (Exception e) {
			// logger.error(null, e);
		}

		return proc;
	}

}
