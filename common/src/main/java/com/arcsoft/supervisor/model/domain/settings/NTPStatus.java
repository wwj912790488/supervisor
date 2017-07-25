package com.arcsoft.supervisor.model.domain.settings;

import java.util.List;

/**
 * the informaiton of NTP service
 * 
 * @author xpeng
 * 
 */
public class NTPStatus {
	private Boolean isServiceOn;
	private List<String> ntpServers;

	public NTPStatus(){		
	}
	public NTPStatus(Boolean isServceOn, List<String> servers) {
		this.isServiceOn = isServceOn;
		this.ntpServers =servers;
	}

	public Boolean getIsServiceOn() {
		return isServiceOn;
	}

	public void setIsServiceOn(Boolean isServiceOn) {
		this.isServiceOn = isServiceOn;
	}

	public List<String> getNtpServers() {
		return ntpServers;
	}

	public void setNtpServers(List<String> ntpServers) {
		this.ntpServers = ntpServers;
	}

}
