package com.arcsoft.supervisor.cluster.action.settings.time;

import com.arcsoft.supervisor.cluster.action.BaseResponse;
import com.arcsoft.supervisor.model.domain.settings.NTPStatus;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * The response message for get ntp service status
 * 
 * @author xpeng
 */
@XmlRootElement
public class GetNTPResponse extends BaseResponse {
	private NTPStatus ntpStatus;

	public NTPStatus getNtpStatus() {
		return ntpStatus;
	}

	public void setNtpStatus(NTPStatus ntpStatus) {
		this.ntpStatus = ntpStatus;
	}


}
