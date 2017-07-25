package com.arcsoft.supervisor.cd;

import com.arcsoft.supervisor.cd.data.ContentServerInfo;
import com.arcsoft.supervisor.utils.NamedThreadFactory;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MediaCheckProcessManager {

	private Logger logger = Logger.getLogger(MediaCheckProcessManager.class);
	private static final int minIdleProcess = 2;
	private int curPort = 0;
	private volatile boolean running = true;
	private ExecutorService poolProcess = Executors.newSingleThreadExecutor(NamedThreadFactory.create("Content-Detect:" +
			"MediaCheckProcessManager"));
	private ContentServerInfo contentServerInfo = null;
	private ArrayList<Process> procList = new ArrayList<Process>();
	private TaskListInfo taskListInfo = null;

	public void setContentServerInfo(ContentServerInfo contentServerInfo) {
		this.contentServerInfo = contentServerInfo;
	}

	public void setTaskListInfo(TaskListInfo taskListInfo) {
		this.taskListInfo = taskListInfo;
	}

	public void uninit() {
		running = false;
		poolProcess.shutdown();
		procList.clear();
		logger.info("exit");
	}

	public void start() {

		poolProcess.execute(new Runnable() {
			@Override
			public void run() {
				process();
			}
		});

	}

	public void process() {
		curPort = contentServerInfo.getStartPort();
		int maxNum = contentServerInfo.getProcessNum();
		do {
			int runProcessNum = taskListInfo.getRunTaskCount();
			int num = procList.size();
			if (((num - runProcessNum) < minIdleProcess) && (num < maxNum)) {
				do {
					if (Utils.portIsFree(curPort)) {
						NativeMediaCheckerProcess process = new NativeMediaCheckerProcess();
						Process proc = process.createProcess(contentServerInfo.getAppName(),
								contentServerInfo.getListenPort(), curPort);
						if (proc != null) {
							procList.add(proc);
						}
						curPort++;
						if (curPort > 65535) {
							curPort = contentServerInfo.getStartPort();
						}
						try {
							Thread.sleep(1500);
						} catch (InterruptedException e) {
						}
						break;
					}
					curPort++;
					if (curPort > 65535) {
						curPort = contentServerInfo.getStartPort();
					}
				} while (running);
			}
			if (!running) {
				break;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}

			for (int i = procList.size() - 1; i >= 0; i--) {
				if (isProcessExit(procList.get(i))) {
					procList.remove(i);
				}
			}

		} while (running);
		logger.info("exit");
	}

	private boolean isProcessExit(Process proc) {
		boolean ret = true;
		try {
			proc.exitValue();
			// logger.info("isProcessExit checker");
		} catch (IllegalThreadStateException ex) {
			ret = false;
			// logger.info("isProcessExit checker 1");
		}
		return ret;
	}
}
