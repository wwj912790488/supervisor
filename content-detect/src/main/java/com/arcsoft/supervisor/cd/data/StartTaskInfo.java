package com.arcsoft.supervisor.cd.data;

import org.msgpack.annotation.Message;

import java.util.ArrayList;

@Message
// Annotation
public class StartTaskInfo extends AbstractInfo {
	private int taskid;
	private ArrayList<Integer> checkType = new ArrayList<Integer>();
	private ArrayList<Integer> checkTypeParam = new ArrayList<Integer>();
	private String url;
	// for ts,udp
	private int index = -1;
	// Reserved
	private int program_id;
	// -1 means selected by default, -2 means disable.
	private int video_pid = -1;
	// -1 means selected by default, -2 means disable.
	private int audio_pid = -1;
	// -1 means selected by default, -2 means disable.
	private int subtitle_pid = -3;

	public int getTaskid() {
		return taskid;
	}

	public void setTaskid(int taskid) {
		this.taskid = taskid;
	}

	public ArrayList<Integer> getCheckType() {
		return checkType;
	}

	public void setCheckType(ArrayList<Integer> checkType) {
		this.checkType = checkType;
	}

	public ArrayList<Integer> getCheckTypeParam() {
		return checkTypeParam;
	}

	public void setCheckTypeParam(ArrayList<Integer> checkTypeParam) {
		this.checkTypeParam = checkTypeParam;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getProgram_id() {
		return program_id;
	}

	public void setProgram_id(int program_id) {
		this.program_id = program_id;
	}

	public int getVideo_pid() {
		return video_pid;
	}

	public void setVideo_pid(int video_pid) {
		this.video_pid = video_pid;
	}

	public int getAudio_pid() {
		return audio_pid;
	}

	public void setAudio_pid(int audio_pid) {
		this.audio_pid = audio_pid;
	}

	public int getSubtitle_pid() {
		return subtitle_pid;
	}

	public void setSubtitle_pid(int subtitle_pid) {
		this.subtitle_pid = subtitle_pid;
	}

}
