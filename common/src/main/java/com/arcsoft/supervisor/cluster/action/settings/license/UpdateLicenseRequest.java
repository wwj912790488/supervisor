package com.arcsoft.supervisor.cluster.action.settings.license;

import com.arcsoft.supervisor.cluster.action.BaseRequest;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Update license request.
 * 
 * @author fjli
 */
@XmlRootElement
public class UpdateLicenseRequest extends BaseRequest {

	private byte[] data;

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

}
