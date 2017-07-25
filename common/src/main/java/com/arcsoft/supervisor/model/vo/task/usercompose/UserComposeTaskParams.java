package com.arcsoft.supervisor.model.vo.task.usercompose;

import com.arcsoft.supervisor.model.vo.task.AbstractRtspParams;

import java.util.List;

public class UserComposeTaskParams extends AbstractRtspParams {
	
	private Integer totalWidth;
	private Integer totalHeight;
	
	private List<UserComposeCellConfig> cellConfigs;

	private String rtspOpsFileName;
	private String rtspMobileFileName;

	private Integer screenOutputPort;
	private Integer mobileOutputPort;
	
    private String rtspHostIp;

    /**
     * The transcoder template of task
     */
    private String transcoderTemplate;
	
	public String getTranscoderTemplate() {
		return transcoderTemplate;
	}
	public void setTranscoderTemplate(String transcoderTemplate) {
		this.transcoderTemplate = transcoderTemplate;
	}
	public Integer getTotalWidth() {
		return totalWidth;
	}
	public void setTotalWidth(Integer totalWidth) {
		this.totalWidth = totalWidth;
	}
	public Integer getTotalHeight() {
		return totalHeight;
	}
	public void setTotalHeight(Integer totalHeight) {
		this.totalHeight = totalHeight;
	}
	public List<UserComposeCellConfig> getCellConfigs() {
		return cellConfigs;
	}
	public void setCellConfigs(List<UserComposeCellConfig> cellConfigs) {
		this.cellConfigs = cellConfigs;
	}
	public String getRtspOpsFileName() {
		return rtspOpsFileName;
	}
	public void setRtspOpsFileName(String rtspOpsFileName) {
		this.rtspOpsFileName = rtspOpsFileName;
	}
	public String getRtspMobileFileName() {
		return rtspMobileFileName;
	}
	public void setRtspMobileFileName(String rtspMobileFileName) {
		this.rtspMobileFileName = rtspMobileFileName;
	}

	public Integer getScreenOutputPort() {
		return screenOutputPort;
	}

	public void setScreenOutputPort(Integer screenOutputPort) {
		this.screenOutputPort = screenOutputPort;
	}

	public Integer getMobileOutputPort() {
		return mobileOutputPort;
	}

	public void setMobileOutputPort(Integer mobileOutputPort) {
		this.mobileOutputPort = mobileOutputPort;
	}
}
