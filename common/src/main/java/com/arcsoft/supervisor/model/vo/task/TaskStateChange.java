package com.arcsoft.supervisor.model.vo.task;

import com.arcsoft.supervisor.cluster.action.task.StateChangeRequest;

import java.util.Date;

/**
 * 
 * This class used to holds task status for {@link StateChangeRequest}
 * 
 * @author zw
 */
public class TaskStateChange {

	private Integer id;
	private String state;
	private Date date;
	private Integer pid;

	public Integer getPid() {
		return pid;
	}

	public void setPid(Integer pid) {
		this.pid = pid;
	}

	/**
	 * The port number of ip-stream compose task.if currently task is not
	 * ip-stream compose task then it will be <code>null</code>.
	 */
	private Integer composeTaskUdpPort;
	
	private String rtmpOpsFileName;
	

	public String getRtmpOpsFileName() {
		return rtmpOpsFileName;
	}

	public void setRtmpOpsFileName(String rtmpOpsFileName) {
		this.rtmpOpsFileName = rtmpOpsFileName;
	}

	/**
	 * Returns the task id.
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * Set the task id.
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Returns the task state.
	 */
	public String getState() {
		return state;
	}

	/**
	 * Set the task state.
	 * 
	 * @param state - the task state
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * Returns the begin or end time. For RUNNING state, it is begin time. For COMPLETED, ERROR, CANCELLED state, it is
	 * end time.
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Set the begin or end time.
	 * 
	 * @param date - the time to be set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	public Integer getComposeTaskUdpPort() {
		return composeTaskUdpPort;
	}

	public void setComposeTaskUdpPort(Integer composeTaskUdpPort) {
		this.composeTaskUdpPort = composeTaskUdpPort;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("TaskStateChange{");
		sb.append("state='").append(state).append('\'');
		sb.append(", id=").append(id);
		sb.append(", composeTaskUdpPort=").append(composeTaskUdpPort);
		sb.append(", pid=").append(pid);
		sb.append('}');
		return sb.toString();
	}
	
	public static TaskStateChange from(int id, TaskStatus status, Integer composeTaskUdpPort, String rtspOpsFileName, Integer pid) {
		return from(id, status.name(), composeTaskUdpPort, rtspOpsFileName, pid);
	}
	
	public static TaskStateChange from(int id, TaskStatus status, Integer composeTaskUdpPort, Integer pid) {
		return from(id, status.name(), composeTaskUdpPort, pid);
	}

	public static TaskStateChange from(int id, String state, Integer composeTaskUdpPort, Integer pid){
		return from(id, state, composeTaskUdpPort, null, pid);
	}
	
	public static TaskStateChange from(int id, String state, Integer composeTaskUdpPort, String rtspOpsFileName, Integer pid){
		TaskStateChange stateChange = new TaskStateChange();
		stateChange.setId(id);
		stateChange.setState(state);
		stateChange.setComposeTaskUdpPort(composeTaskUdpPort);
		stateChange.setRtmpOpsFileName(rtspOpsFileName);
		stateChange.setPid(pid);
		return stateChange;
	}
	

	public static TaskStateChange from(int id, String state, Integer pid){
		return from(id, state, null, pid);
	}

	public static TaskStateChange from(int id, TaskStatus status, Integer pid) {
		return from(id, status.name(), pid);
	}
	
}
