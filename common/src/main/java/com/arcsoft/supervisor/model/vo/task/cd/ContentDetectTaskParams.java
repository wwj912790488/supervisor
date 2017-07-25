package com.arcsoft.supervisor.model.vo.task.cd;

import com.arcsoft.supervisor.model.vo.task.AbstractTaskParams;

import java.util.ArrayList;

/**
 * The task of content detect.
 *
 * @author zw.
 */
public class ContentDetectTaskParams extends AbstractTaskParams {

	private ArrayList<Integer> checkType = new ArrayList<Integer>();
	private ArrayList<Integer> checkTypeParam = new ArrayList<Integer>();
	private String url;
	// for ts ...
	private int index = -1;
	private int program_id;// Reserved
	private int video_pid = -1; // -1 means selected by default, -2 means disable.
	private int audio_pid = -1; // -1 means selected by default, -2 means disable.
	private int subtitle_pid = -3; // -1 means selected by default, -2 means disable.

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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ContentDetectTaskParams{");
        sb.append("id=").append(getId());
        sb.append(", checkType=").append(checkType);
        sb.append(", checkTypeParam=").append(checkTypeParam);
        sb.append(", url='").append(url).append('\'');
        sb.append(", program_id=").append(program_id);
        sb.append(", audio_pid=").append(audio_pid);
        sb.append('}');
        return sb.toString();
    }
}
