package com.arcsoft.supervisor.cluster.action.settings.time;

import com.arcsoft.supervisor.cluster.action.BaseRequest;
import com.arcsoft.supervisor.model.domain.settings.NTPStatus;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * Request for set the server's date and time and sync with ntp server or not
 * 
 * @author xpeng
 */
@XmlRootElement
public class SetDateTimeRequest extends BaseRequest {
	private Date date;
	private NTPStatus ntpStatus;

	public SetDateTimeRequest(){		
	}
	
	public SetDateTimeRequest(Date date, NTPStatus ntp){
		this.date = date;
		this.ntpStatus = ntp;
	}
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}	
	
	public NTPStatus getNtpStatus() {
		return ntpStatus;
	}

	public void setNtpStatus(NTPStatus ntpStatus) {
		this.ntpStatus = ntpStatus;
	}

}
