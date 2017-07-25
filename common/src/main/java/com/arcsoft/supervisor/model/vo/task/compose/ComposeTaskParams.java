package com.arcsoft.supervisor.model.vo.task.compose;

import com.arcsoft.supervisor.model.vo.task.AbstractRtspParams;
import com.arcsoft.supervisor.model.vo.task.cd.ScreenContentDetectConfig;
import com.arcsoft.supervisor.utils.StringHelper;

import java.util.List;
import java.util.Map;

/**
 * Defines properties of compose task.
 *
 * @author zw.
 */
public class ComposeTaskParams extends AbstractRtspParams {

    /** The ip address of compose output stream  */
    private String targetIp;

    private String targetRtmpUrl;

    /**
     * Config of channel content detect on the screen
     */
    private List<ScreenContentDetectConfig> contentDetectConfigs;
    
    private Map<String, byte[]> sdpFiles;

    /**
     * The count of screen rows
     */
    private Integer rowCount;

    public Map<String, byte[]> getSdpFiles() {
		return sdpFiles;
	}

	public void setSdpFiles(Map<String, byte[]> sdpFiles) {
		this.sdpFiles = sdpFiles;
	}

	/**
     * The count of screen columns
     */
    private Integer columnCount;

    private Integer groupCount;

    private Integer switchTime;
    /**
     * Indicate currently task need rtsp output or not.
     * */
    private boolean enableRtsp = false;
    
    private boolean screenwithrtmp = false;

    /**
     * The sdp file name of rtsp. The property only used if <code>enableRtsp</code> is true.
     */
    private String rtspFileName;

    /**
     * The port number for ops output of compose task.
     */
    private Integer screenOutputPort;

    /**
     * The port number for rtsp output.
     */
    private Integer mobileOutputPort;

    /**
     * The items of resolution and output index of task.
     */
    private List<TaskOutputResolutionAndIndexMapper> resolutionAndIndexMappers;

    /**
     * The transcoder template of task
     */
    private String transcoderTemplate;
    
    private String rtmpOPSFileName;

    private List<String> seiMessages;

    private String  background;

    private Integer amountOfDecodedInputs;
    


    public String getRtmpOPSFileName() {
		return rtmpOPSFileName;
	}

	public void setRtmpOPSFileName(String rtmpOPSFileName) {
		this.rtmpOPSFileName = rtmpOPSFileName;
	}

	public List<ScreenContentDetectConfig> getContentDetectConfigs() {
		return contentDetectConfigs;
	}

	public void setContentDetectConfigs(
			List<ScreenContentDetectConfig> contentDetectConfigs) {
		this.contentDetectConfigs = contentDetectConfigs;
	}

	public String getTargetIp() {
        return targetIp;
    }

    public void setTargetIp(String targetIp) {
        this.targetIp = targetIp;
    }

    public boolean isEnableRtsp() {
        return enableRtsp;
    }

    public void setEnableRtsp(boolean enableRtsp) {
        this.enableRtsp = enableRtsp;
    }
    
    public boolean isScreenWithRTMP() {
        return screenwithrtmp;
    }

    public void setScreenWithRTMP(boolean screenwithrtmp) {
        this.screenwithrtmp = screenwithrtmp;
    }

    public String getRtspFileName() {
        return rtspFileName;
    }

    public void setRtspFileName(String rtspFileName) {
        this.rtspFileName = rtspFileName;
    }

    public Integer getRowCount() {
        return rowCount;
    }

    public void setRowCount(Integer rowCount) {
        this.rowCount = rowCount;
    }

    public Integer getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(Integer columnCount) {
        this.columnCount = columnCount;
    }

    public List<TaskOutputResolutionAndIndexMapper> getResolutionAndIndexMappers() {
        return resolutionAndIndexMappers;
    }

    public void setResolutionAndIndexMappers(List<TaskOutputResolutionAndIndexMapper> resolutionAndIndexMappers) {
        this.resolutionAndIndexMappers = resolutionAndIndexMappers;
    }

    public String getTranscoderTemplate() {
        return transcoderTemplate;
    }

    public void setTranscoderTemplate(String transcoderTemplate) {
        this.transcoderTemplate = transcoderTemplate;
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

    public Integer getGroupCount() {
        return groupCount;
    }

    public void setGroupCount(Integer groupCount) {
        this.groupCount = groupCount;
    }

    public Integer getSwitchTime() {
        return switchTime;
    }

    public void setSwitchTime(Integer switchTime) {
        this.switchTime = switchTime;
    }

    public List<String> getSeiMessages(){return seiMessages;}
    public void setSeiMessages(List<String>  seiMessages){this.seiMessages = seiMessages;}

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public Integer getAmountOfDecodedInputs() {
        return amountOfDecodedInputs;
    }

    public void setAmountOfDecodedInputs(Integer amountOfDecodedInputs) {
        this.amountOfDecodedInputs = amountOfDecodedInputs;
    }

    public String getTargetRtmpUrl() {
        return targetRtmpUrl;
    }

    public void setTargetRtmpUrl(String targetRtmpUrl) {
        this.targetRtmpUrl = targetRtmpUrl;
    }
}
