package com.arcsoft.supervisor.cd;

import com.arcsoft.supervisor.cd.data.TaskInfo;
import com.arcsoft.supervisor.cd.data.TaskState;

import java.util.ArrayList;

public class TaskListInfo {

	private ArrayList<TaskInfo> listTaskInfo = new ArrayList<TaskInfo>();

	public void add(TaskInfo info) {
		synchronized (listTaskInfo) {
			listTaskInfo.add(info);
		}
	}

	public void removeByTaskId(int taskId) {
		synchronized (listTaskInfo) {
			for (TaskInfo t : listTaskInfo) {
				if (t.getTaskId() == taskId) {
					listTaskInfo.remove(t);
					break;
				}
			}
		}
	}

	public void removeByPort(int port) {
		synchronized (listTaskInfo) {
			for (TaskInfo t : listTaskInfo) {
				if (t.getPort() == port) {
					listTaskInfo.remove(t);
					break;
				}
			}
		}
	}

	public int getTaskInfoCount() {
		synchronized (listTaskInfo) {
			return listTaskInfo.size();
		}
	}

	public int getRunTaskCount() {
		int count = 0;
		synchronized (listTaskInfo) {
			for (TaskInfo t : listTaskInfo) {
				if (t.getState() == TaskState.STATE_TASK_RUN) {
					count++;
				}
			}
		}
		return count;
	}

	public int getIdleTaskCount() {
		int count = 0;
		synchronized (listTaskInfo) {
			for (TaskInfo t : listTaskInfo) {
				if (t.getState() != TaskState.STATE_TASK_RUN && t.getState() != TaskState.STATE_DISCONNECT) {
					count++;
				}
			}
		}
		return count;
	}

	public TaskInfo getIdleTaskInfo() {
		TaskInfo ret = null;
		synchronized (listTaskInfo) {
			for (TaskInfo t : listTaskInfo) {
				if (t.getState() != TaskState.STATE_TASK_RUN && t.getState() != TaskState.STATE_DISCONNECT) {
					ret = t;
					break;
				}
			}
		}

		return ret;
	}

	public TaskInfo getTaskInfoByPort(int port) {
		TaskInfo ret = null;
		synchronized (listTaskInfo) {
			for (TaskInfo t : listTaskInfo) {
				if (t.getPort() == port) {
					ret = t;
					break;
				}
			}
		}

		return ret;
	}

	public TaskInfo getTaskInfoByTaskId(int taskId) {
		TaskInfo ret = null;
		synchronized (listTaskInfo) {
			for (TaskInfo t : listTaskInfo) {
				if (t.getTaskId() == taskId) {
					ret = t;
					break;
				}
			}
		}

		return ret;
	}

	public TaskInfo getTaskInfoByIndex(int index) {
		TaskInfo ret = null;
		synchronized (listTaskInfo) {
			ret = listTaskInfo.get(index);
		}

		return ret;
	}

	public void setTaskInfoByTaskId(int taskId, int state) {
		synchronized (listTaskInfo) {
			for (TaskInfo t : listTaskInfo) {
				if (t.getTaskId() == taskId) {
					t.setState(state);
					break;
				}
			}
		}
	}

	public void setTaskInfoByPort(int port, int state, int taskId) {
		synchronized (listTaskInfo) {
			for (TaskInfo t : listTaskInfo) {
				if (t.getPort() == port) {
					t.setState(state);
					t.setTaskId(taskId);
					break;
				}
			}
		}
	}

	public boolean isExistRunTask(int taskId) {
		boolean ret = false;
		synchronized (listTaskInfo) {
			for (TaskInfo t : listTaskInfo) {
				if (t.getTaskId() == taskId) {
					ret = (t.getState() == TaskState.STATE_TASK_RUN);
					break;
				}
			}
		}

		return ret;
	}

}
