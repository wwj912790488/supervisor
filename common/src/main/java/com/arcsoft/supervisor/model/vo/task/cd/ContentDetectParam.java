package com.arcsoft.supervisor.model.vo.task.cd;

import java.util.HashMap;
import java.util.Map;

public class ContentDetectParam {
	private int index;
	private Map<String, String> params = new HashMap<String, String>();
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public Map<String, String> getParams() {
		return params;
	}
	public void setParams(Map<String, String> params) {
		this.params = params;
	}


}
