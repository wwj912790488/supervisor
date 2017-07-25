package com.arcsoft.supervisor.recorder.data;

public class RecordTaskParam {
	private int task_id;
	private String programId;
    private String audioId;
    private String url;
    private String recordFileName;
    private String recordPath;
    
	public int getTask_id() {
		return task_id;
	}
	public void setTask_id(int task_id) {
		this.task_id = task_id;
	}
	public String getProgramId() {
		return programId;
	}
	public void setProgramId(String programId) {
		this.programId = programId;
	}
	public String getAudioId() {
		return audioId;
	}
	public void setAudioId(String audioId) {
		this.audioId = audioId;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getRecordFileName() {
		return recordFileName;
	}
	public void setRecordFileName(String recordFileName) {
		this.recordFileName = recordFileName;
	}
	public String getRecordPath() {
		return recordPath;
	}
	public void setRecordPath(String recordPath) {
		this.recordPath = recordPath;
	}
    
    
}
