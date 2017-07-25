package com.arcsoft.supervisor.cluster.action.settings.time;

import com.arcsoft.supervisor.cluster.action.BaseRequest;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Request for set the server's timezone
 * 
 * @author xpeng
 */
@XmlRootElement
public class SetTimeZoneRequest extends BaseRequest {
	private String timezone;
	
	public SetTimeZoneRequest(){		
	}
	
	public SetTimeZoneRequest(String timezone){
		this.timezone = timezone;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}


	


}
