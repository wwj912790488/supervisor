package com.arcsoft.supervisor.recorder;

import org.apache.log4j.Logger;

import java.io.IOException;

public class NativeRecordProcess {

	private Logger logger = Logger.getLogger(NativeRecordProcess.class);
	
	private Process proc;
	
	private NativeRecordProcess(String[] argv) throws IOException {
		ProcessBuilder pb = new ProcessBuilder(argv);
		proc = pb.start();
	}
	
	public void kill() {
		if(proc != null) {
			proc.destroy();
		}
	}

	public static NativeRecordProcess create(String app, int listenPort, int task_id) throws IOException {
		String slistenPort = Integer.toString(listenPort);
		String sTaskid = Integer.toString(task_id);
		String[] command = { app, "-i", "udp://239.1.1.1:1234", "-p", slistenPort, "-c", "20", "-d", "record/", "-I", sTaskid};
		
		return new NativeRecordProcess(command);
	}

}
