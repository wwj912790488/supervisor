package com.arcsoft.supervisor.cluster.action.settings.license;

import com.arcsoft.supervisor.cluster.action.BaseResponse;
import com.arcsoft.supervisor.model.domain.settings.LicenseInfo;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * The response message for list license request.
 * 
 * @author fjli
 */
@XmlRootElement
public class ListLicenseResponse extends BaseResponse {

	private LicenseInfo licenseInfo;

	public LicenseInfo getLicenseInfo() {
		return licenseInfo;
	}

	public void setLicenseInfo(LicenseInfo licenseInfo) {
		this.licenseInfo = licenseInfo;
	}

}
