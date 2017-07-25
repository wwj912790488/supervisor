package com.arcsoft.supervisor.recorder;

import com.arcsoft.supervisor.recorder.data.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RecordApp implements RecordCommandHandler {

	private Logger logger = Logger.getLogger(RecordApp.class);
	private ListenServer listenServer = null;
	private List<RecordTask> taskList = new ArrayList<RecordTask>();
	private Thread listenThread = null;
	private RecordAgentInfo recordAgentInfo = null;

	public void setRecordAgentInfo(RecordAgentInfo recordAgentInfo) {
		this.recordAgentInfo = recordAgentInfo;
	}

	public void Init() {
		logger.debug("init");
		this.listenServer = new ListenServer();
		this.listenServer.setPort(recordAgentInfo.getListenPort());
		this.listenServer.setDataProcessListener(this);
		listenThread = new Thread(this.listenServer);
		this.listenThread.start();

	}

	public void shutdown() {
		for (RecordTask recordTask : taskList) {
			recordTask.stop();
		}
		taskList.clear();
		listenServer.shutdown();
	}

	public void startTask(RecordTaskParam param) throws StartRecordTaskException {
		RecordTask newTask = new RecordTask(param, this, recordAgentInfo);
		try {
			taskList.add(newTask);
			Future<?> future = newTask.start();
			try {
				future.get(5, TimeUnit.SECONDS);
			} catch (InterruptedException | ExecutionException | TimeoutException e) {
				taskList.remove(newTask);
				future.cancel(true);
				newTask.stop();
				throw new StartRecordTaskException();
			}
		} catch(IOException e) {
			taskList.remove(newTask);
			throw new StartRecordTaskException();
		}
	}

	public void stopTask(int taskId) {
		ArrayList<RecordTask> stopped = new ArrayList<RecordTask>();
		for (RecordTask recordTask : taskList) {
			if(recordTask.getTaskId() == taskId) {
				recordTask.stop();
				stopped.add(recordTask);
			}
		}
		for (RecordTask recordTask : stopped) {
			taskList.remove(recordTask);
		}
	}

	@Override
	public synchronized void receive(Command command) {

		for (RecordTask recordTask : taskList) {
			if(recordTask.getTaskId() == command.getTaskId()) {
				recordTask.process(command);
			}
		}
	}

}
