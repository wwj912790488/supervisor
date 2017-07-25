package com.arcsoft.supervisor.recorder;

import com.arcsoft.supervisor.recorder.data.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RecordTask implements ICommandClientStateChanged {
	
	private static Logger log = Logger.getLogger(RecordTask.class);
	
	private static class WaitRecordStart implements Callable<Integer> {
		private RecordTask waitingTask;
			
		public WaitRecordStart(RecordTask t) {
			waitingTask = t;
		}
		
		@Override
		public Integer call() throws Exception {
			while(true) {
				if(!waitingTask.taskStarted()) {
					Thread.sleep(100);
				} else {
					break;
				}
			}
			return waitingTask.getPort();
		}
		
	}
	
	private final ExecutorService executor = Executors.newCachedThreadPool();
	private boolean taskStarted;
	private RecordCommandHandler handler;
	private RecordAgentInfo info;
	private CommandClient client;
	private NativeRecordProcess process;
	
	public RecordTask(RecordTaskParam param, RecordCommandHandler handler, RecordAgentInfo info) {
		this.taskStarted = false;
		this.task_id = param.getTask_id();
		this.programId = param.getProgramId();
		this.audioId = param.getAudioId();
		this.recordFileName = param.getRecordFileName();
		this.recordPath = param.getRecordPath();
		this.url = param.getUrl();
		this.handler = handler;
		this.info = info;
	}
	
	public Future<Integer> start() throws IOException {
		log.debug("start native record process");
		process = NativeRecordProcess.create(info.getAppName(), info.getListenPort(), this.task_id);
		return executor.submit(new WaitRecordStart(this));
	}
	
	public void stop() {
		boolean success = false;
		if(client != null && client.isConnected()) {
			DirectCommand command = new DirectCommand(task_id, 1);
			success = client.send(command);		
		}
		
		if(!success) {
			process.kill();
		}
		if(client != null) {
			client.close();
		}
		executor.shutdown();
	}
	
	@Override
	public void onClientDisconnected() {
		this.client = new CommandClient(port, handler, this);
		try { 
			this.client.connect();
		} catch(IOException e) {
			RecorderListener listener = this.info.getRecorderListener();
			if(listener != null) {
				listener.receivedTaskState(task_id, TaskState.STATE_DISCONNECT);
			}
		}
	}
	
	public void process(Command command) {
		if(command instanceof ReportPortCommand) {
			ReportPortCommand reportCmd = (ReportPortCommand)command;
			this.client = new CommandClient(reportCmd.port, handler, this);
			try {
				this.client.connect();
				setTaskStarted(reportCmd.port);
			} catch (IOException e) {
				//failed to connect to native record app, we should kill the process and report start exception.
				//here we just don't set task started state and let stop method to do the cleanup.
			}			
		}
	}
	
	private synchronized void setTaskStarted(int port) {
		taskStarted = true;
		this.port = port;
	}
	
	private synchronized boolean taskStarted() {
		return taskStarted;
	}
	
	private int getPort() {
		return port;
	}
	
	public int getTaskId() {
		return task_id;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + task_id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RecordTask other = (RecordTask) obj;
		if (task_id != other.task_id)
			return false;
		return true;
	}
	
	private int task_id;
	private int port;
	private String programId;
    private String audioId;
    private String url;
    private String recordFileName;
    private String recordPath;

}
