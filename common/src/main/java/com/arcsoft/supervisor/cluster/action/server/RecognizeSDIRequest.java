package com.arcsoft.supervisor.cluster.action.server;

import com.arcsoft.supervisor.cluster.action.BaseRequest;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RecognizeSDIRequest extends BaseRequest {
	private String sdiName;
	private int number;
	
	public String getSdiName() {
		return sdiName;
	}
	public void setSdiName(String sdiName) {
		this.sdiName = sdiName;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	
	
}
