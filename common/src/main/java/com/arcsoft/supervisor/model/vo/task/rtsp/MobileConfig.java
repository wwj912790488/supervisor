package com.arcsoft.supervisor.model.vo.task.rtsp;

/**
 * @author zw.
 */
public class MobileConfig {

    private Integer width;
    private Integer height;
    private Integer videoBitrate;
    private Integer audioBitrate;
    private boolean deinterlace;
    /**
     * The type of the config. 0: sd 1: hd
     */
    private Byte type;
    private String fileName;
    private Integer portNumber;

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getVideoBitrate() {
        return videoBitrate;
    }

    public void setVideoBitrate(Integer videoBitrate) {
        this.videoBitrate = videoBitrate;
    }

    public Integer getAudioBitrate() {
        return audioBitrate;
    }

    public void setAudioBitrate(Integer audioBitrate) {
        this.audioBitrate = audioBitrate;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(Integer portNumber) {
        this.portNumber = portNumber;
    }

    public boolean isHd() {
        return type == 1;
    }

    public boolean isSd() {
        return type == 0;
    }

    public void setDeinterlace(boolean deinterlace){this.deinterlace=deinterlace;}
    public boolean getDeinterlace(){return deinterlace;}
}
