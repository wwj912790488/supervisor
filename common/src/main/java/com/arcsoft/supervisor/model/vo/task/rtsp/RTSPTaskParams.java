package com.arcsoft.supervisor.model.vo.task.rtsp;

import com.arcsoft.supervisor.model.vo.task.AbstractRtspParams;

import java.util.List;

/**
 * @author zw.
 */
public class RTSPTaskParams extends AbstractRtspParams {

    private List<MobileConfig> configs;
    private String programId;
    private String audioId;
    private String videocodec;
    private String audiocodec;
    private String udpUrl;
    private Boolean enableRecord;
    private String recordFileName;
    private String recordPath;
    private Byte recordFormat;
    private byte[] sdpFile;
    private String protocol;
    private Integer port;

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public byte[] getSdpFile() {
		return sdpFile;
	}

	public void setSdpFile(byte[] sdpFile) {
		this.sdpFile = sdpFile;
	}

	public String getProgramId() {
        return programId;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
    }

    public String getAudioId() {
        return audioId;
    }

    public String getVideocodec(){return videocodec;}
    public void setVideocodec(String videocodec){this.videocodec=videocodec;}

    public String getAudiocodec(){return audiocodec;}
    public void setAudiocodec(String audiocodec){this.audiocodec=audiocodec;}

    public void setAudioId(String audioId) {
        this.audioId = audioId;
    }

    public List<MobileConfig> getConfigs() {
        return configs;
    }

    public void setConfigs(List<MobileConfig> configs) {
        this.configs = configs;
    }

    public String getUdpUrl() {
        return udpUrl;
    }

    public void setUdpUrl(String udpUrl) {
        this.udpUrl = udpUrl;
    }

    public Boolean getEnableRecord() {
        return enableRecord;
    }

    public void setEnableRecord(Boolean enableRecord) {
        this.enableRecord = enableRecord;
    }

    public String getRecordFileName() {
        return recordFileName;
    }

    public void setRecordFileName(String recordFileName) {
        this.recordFileName = recordFileName;
    }

    public String getRecordPath() {
        return recordPath;
    }

    public void setRecordPath(String recordPath) {
        this.recordPath = recordPath;
    }

    public Byte getRecordFormat() {
        return recordFormat;
    }

    public void setRecordFormat(Byte recordFormat) {
        this.recordFormat = recordFormat;
    }
}
