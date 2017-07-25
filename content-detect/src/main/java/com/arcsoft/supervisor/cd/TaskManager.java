package com.arcsoft.supervisor.cd;

import com.arcsoft.supervisor.cd.data.*;
import org.apache.log4j.Logger;

public class TaskManager implements ConnectCheckListener {

	private Logger logger = Logger.getLogger(TaskManager.class);

	private String ip = "127.0.0.1";
	private TaskManagerListener taskManagerListener = null;
	private ConnectCheck connectcheck = new ConnectCheck();
	// private ExecutorService poolCheckRunState =
	// Executors.newCachedThreadPool();
	private TaskListInfo taskListInfo = null;
	private int maxProcess = 10;

	public TaskManager() {
		connectcheck.setConnectCheckListener(this);
	}

	public void uninit() {
		logger.info("uninit start");
		connectcheck.uninit();
		exitAllProcess();
		// poolCheckRunState.shutdown();
		logger.info("uninit end");
	}

	public void setTaskManagerListener(TaskManagerListener taskManagerListener) {
		this.taskManagerListener = taskManagerListener;
	}

	public void setTaskListInfo(TaskListInfo taskListInfo) {
		this.taskListInfo = taskListInfo;
	}

	public void setMaxProcess(int maxProcess) {
		this.maxProcess = maxProcess;
	}

	public void setIp(String ip) {
		this.ip = ip;
		connectcheck.setIp(ip);
	}

	public synchronized boolean Add(int port) {
		logger.info("add port:" + port);
		final TaskInfo info = new TaskInfo();
		info.setPort(port);
		info.setIp(ip);
		taskListInfo.add(info);
		// poolCheckRunState.execute(new Runnable() {
		// @Override
		// public void run() {
		// checkRunTaskId(info);
		// }
		// });

		connectcheck.addPort(port);
		logger.info("add end port:" + port);
		return true;
	}

	public synchronized AbstractInfo SendTask(int command, AbstractInfo taskInfo) {
		AbstractInfo ret = null;
		do {
			if (command == CommandDefine.COMMAND_TASK_START) {
				ret = startTask(taskInfo);
			} else if (command == CommandDefine.COMMAND_TASK_STOP || command == CommandDefine.COMMAND_GET_PROGRESS) {
				CommonInfo task = (CommonInfo) taskInfo;
				TaskInfo info = taskListInfo.getTaskInfoByTaskId(task.getTaskid());
				if (info != null) {
					ret = sendCommand(info, command, taskInfo);
					break;
				} else {
					ret = generalResultInfo(command, task.getTaskid(), ErrorDefine.MVCH_ID_ERROR);
					break;
				}
			} else if (command == CommandDefine.COMMAND_THUMBNAIL) {
				CommandGetThumbnail task = (CommandGetThumbnail) taskInfo;
				TaskInfo info = taskListInfo.getTaskInfoByTaskId(task.getTaskId());
				if (info != null) {
					ret = sendCommand(info, command, taskInfo);
					break;
				}
			}

		} while (false);

		return ret;
	}

	private AbstractInfo startTask(AbstractInfo taskInfo) {
		AbstractInfo ret = null;
		StartTaskInfo task = (StartTaskInfo) taskInfo;
		if (taskListInfo.isExistRunTask(task.getTaskid())) {
			ret = generalResultInfo(CommandDefine.COMMAND_TASK_START, task.getTaskid(),
					ErrorDefine.MVCH_SAME_TASKID_RUNNING);
		} else {
			TaskInfo info = null;
			for (int i = 0; i < 10; i++) {
				info = taskListInfo.getIdleTaskInfo();
				if (info != null) {
					break;
				}
				int runNum = taskListInfo.getRunTaskCount();
				if (runNum >= maxProcess) {
					break;
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {

				}
			}

			if (info != null) {
				info.setTaskId(task.getTaskid());
				info.setState(TaskState.STATE_TASK_RUN);
				ret = sendCommand(info, CommandDefine.COMMAND_TASK_START, taskInfo);
			} else {
				ret = generalResultInfo(CommandDefine.COMMAND_TASK_START, task.getTaskid(),
						ErrorDefine.MVCH_NO_IDLE_PROCESSER);
			}
		}
		return ret;
	}

	private AbstractInfo sendCommand(TaskInfo info, int command, AbstractInfo taskInfo) {
		AbstractInfo ret = null;
		MessageSender sender = new MessageSender();
		sender.setIp(info.getIp());
		sender.setPort(info.getPort());
		sender.create();
		if (sender.isConnected()) {
			if (taskInfo != null) {
				ret = sender.send(command, taskInfo);
			} else {
				ret = sender.sendCommand(command);
			}
			sender.close();
		} else {
			ret = generalResultInfo(command, info.getTaskId(), ErrorDefine.MVCH_CONNECT_ERROR);
		}

		return ret;
	}

	private CommonResultInfo generalResultInfo(int command, int taskId, int error) {
		CommonResultInfo ret = new CommonResultInfo();
		ret.setCommand(command);
		ret.setTaskid(taskId);
		ret.setResult(error);
		return ret;
	}

	/*
	 * private boolean checkRunTaskId(TaskInfo info) {
	 * 
	 * AbstractInfo ret = sendCommand(info, CommandDefine.COMMAND_GET_TASKINFO,
	 * null); if (ret instanceof CommandTaskInfo) { CommandTaskInfo taskInfo =
	 * (CommandTaskInfo) ret; taskListInfo.setTaskInfoByPort(info.getPort(),
	 * taskInfo.getState(), taskInfo.getTaskid()); return true; } return false;
	 * }
	 */
	@Override
	public synchronized void connectError(int port) {
		TaskInfo info = taskListInfo.getTaskInfoByPort(port);
		taskListInfo.setTaskInfoByPort(port, TaskState.STATE_DISCONNECT, info.getTaskId());
		taskListInfo.removeByPort(port);
		if (taskManagerListener != null) {
			taskManagerListener.receivedTaskState(info.getTaskId(), TaskState.STATE_DISCONNECT);
		}
	}

	public synchronized void exitAllProcess() {

		int count = taskListInfo.getTaskInfoCount();
		for (int i = count - 1; i >= 0; i--) {
			TaskInfo info = taskListInfo.getTaskInfoByIndex(i);
			if (info == null) {
				continue;
			}
			sendCommand(info, CommandDefine.COMMAND_APP_QUIT, null);
		}

	}
}
