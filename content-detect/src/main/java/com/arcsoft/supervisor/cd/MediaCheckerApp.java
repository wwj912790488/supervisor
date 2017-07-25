package com.arcsoft.supervisor.cd;

import com.arcsoft.supervisor.cd.data.*;
import com.arcsoft.supervisor.utils.NamedThreadFactory;
import org.apache.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MediaCheckerApp implements DataProcessListener, TaskManagerListener {

	private Logger logger = Logger.getLogger(MediaCheckerApp.class);
	private ListenServer listenServer = null;
	//private TaskManager taskManager = new TaskManager();
	private ExecutorService pool = Executors.newSingleThreadExecutor(NamedThreadFactory.create("Content-Detect:MediaCheckerApp"));
	private Thread listenThread = null;
	private ContentServerInfo contentServerInfo = null;
	private TaskListInfo taskListInfo = new TaskListInfo();
	//private MediaCheckProcessManager mediaCheckProcessManager = new MediaCheckProcessManager();
	private volatile boolean exit = false;

	public void setContentServerInfo(ContentServerInfo contentServerInfo) {
		this.contentServerInfo = contentServerInfo;
	}

	public void Init() {

		logger.info("Init start");
		this.listenServer = new ListenServer();
		this.listenServer.setPort(contentServerInfo.getListenPort());
		this.listenServer.setDataProcessListener(this);
		listenThread = new Thread(this.listenServer);
		this.listenThread.start();

//		taskManager.setTaskManagerListener(this);
//		taskManager.setTaskListInfo(taskListInfo);
//		taskManager.setMaxProcess(contentServerInfo.getProcessNum());
//		taskManager.setIp(contentServerInfo.getIp());
//		if (Utils.isLocalIp(contentServerInfo.getIp()) && (this.contentServerInfo.getProcessNum() > 0)) {
//			mediaCheckProcessManager.setTaskListInfo(taskListInfo);
//			mediaCheckProcessManager.setContentServerInfo(contentServerInfo);
//			mediaCheckProcessManager.start();
//		}
		logger.info("Init end");
	}

	public void shutdown() {
		exit = true;
		logger.info("shutdown start");
		//mediaCheckProcessManager.uninit();
		//taskManager.uninit();
		listenServer.shutdown();
		pool.shutdown();
		logger.info("shutdown end");
	}

	public void startTask(StartTaskInfo data) {

		//executeTask(CommandDefine.COMMAND_TASK_START, data);
	}

	public void stopTask(int taskId) {
//		CommonInfo data = new CommonInfo();
//		data.setTaskid(taskId);
//		executeTask(CommandDefine.COMMAND_TASK_STOP, data);
	}

	public void getProgress(int taskId) {
//		CommonInfo data = new CommonInfo();
//		data.setTaskid(taskId);
//		executeTask(CommandDefine.COMMAND_GET_PROGRESS, data);
	}

//	private void executeTask(final int command, final AbstractInfo taskInfo) {
//		pool.execute(new Runnable() {
//			@Override
//			public void run() {
//				process(command, taskInfo);
//			}
//		});
//	}
//
//	private void process(int command, AbstractInfo taskInfo) {
//		if (command == CommandDefine.COMMAND_TASK_START) {
//			if (waitAppStart() < 0) {
//				return;
//			}
//		}
//
//		AbstractInfo ret = taskManager.SendTask(command, taskInfo);
//		if (ret != null) {
//			logger.info(ret.toString());
//		}
//		if (contentServerInfo != null && contentServerInfo.getCheckResultListener() != null) {
//
//			CheckResultListener listener = contentServerInfo.getCheckResultListener();
//			if (ret instanceof CommonResultInfo) {
//				CommonResultInfo info = (CommonResultInfo) ret;
//				if (info.getCommand() == CommandDefine.COMMAND_TASK_START) {
//					int state = info.getResult() >= 0 ? TaskState.STATE_TASK_RUN : TaskState.STATE_ERROR;
//					if (info.getResult() != ErrorDefine.MVCH_SAME_TASKID_RUNNING) {
//						if (info.getTaskid() != 0) {
//							taskListInfo.setTaskInfoByTaskId(info.getTaskid(), state);
//						}
//					}
//					listener.receivedTaskState(info.getTaskid(), state);
//
//				} else if (info.getCommand() == CommandDefine.COMMAND_TASK_STOP) {
//					if (info.getResult() >= 0) {
//						listener.receivedTaskState(info.getTaskid(), TaskState.STATE_TASK_STOPPING);
//					} else {
//						listener.receivedTaskState(info.getTaskid(), TaskState.STATE_TASK_STOP_ERROR);
//						logger.error("stop error. taskid=" + info.getTaskid() + ", " + info.getResult());
//					}
//
//				}
//			}
//			contentServerInfo.getCheckResultListener().receive(ret);
//		}
//
//	}

	@Override
	public synchronized void receive(int command) {

	}

	@Override
	public synchronized void receive(AbstractInfo data) {

//		if (data instanceof MediaCheckIpPort) {
//			MediaCheckIpPort info = (MediaCheckIpPort) data;
//			taskManager.Add(info.getPort());
//			return;
//		}

		if (data instanceof DetectResultInfo) {
			CheckResultInfo checkInfo = new CheckResultInfo();
			Utils.convertDetectTypeToCheckType((DetectResultInfo) data, checkInfo);
			if (contentServerInfo != null && contentServerInfo.getCheckResultListener() != null) {
				contentServerInfo.getCheckResultListener().receive(checkInfo);
			}
			return;
		}

		if (contentServerInfo != null && contentServerInfo.getCheckResultListener() != null) {
			contentServerInfo.getCheckResultListener().receive(data);
		}
	}

	@Override
	public synchronized void receivedTaskState(int taskId, int state) {

		if (taskId != 0) {
			taskListInfo.setTaskInfoByTaskId(taskId, state);
		}
		logger.info("taskId:" + taskId + ", state:" + state);
		if (contentServerInfo != null && contentServerInfo.getCheckResultListener() != null && taskId > 0) {
			contentServerInfo.getCheckResultListener().receivedTaskState(taskId, state);
		}
	}

	public boolean isTaskRun(int taskId) {
		return taskListInfo.isExistRunTask(taskId);
	}

//	public byte[] getThumbnail(int taskId, int width, int height) {
//		byte[] buffer = null;
//		CommandGetThumbnail taskInfo = new CommandGetThumbnail();
//		taskInfo.setTaskId(taskId);
//		taskInfo.setWidth(width);
//		taskInfo.setHeight(height);
//
//		AbstractInfo ret = taskManager.SendTask(CommandDefine.COMMAND_THUMBNAIL, taskInfo);
//		if (ret != null) {
//			logger.info(ret.toString());
//		}
//		if (ret instanceof CommandGetThumbnail) {
//			CommandGetThumbnail result = (CommandGetThumbnail) ret;
//			if (result.getResult() == 0) {
//				buffer = result.getBuffer();
//			}
//		}
//
//		return buffer;
//	}

	private int waitAppStart() {
		int runProcessNum = taskListInfo.getRunTaskCount();
		int maxProcess = contentServerInfo.getProcessNum();
		if (maxProcess == runProcessNum) {
			return -1;
		}
		int totalProcess = taskListInfo.getTaskInfoCount();

		if (totalProcess >= runProcessNum) {
			if (totalProcess > runProcessNum) {
				return 1;
			} else {
				do {
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
					}
					totalProcess = taskListInfo.getTaskInfoCount();
					runProcessNum = taskListInfo.getRunTaskCount();
					if (totalProcess > runProcessNum) {
						return 1;
					}
					if (exit) {
						return -1;
					}
				} while (true);
			}
		}

		return 1;
	}

}
