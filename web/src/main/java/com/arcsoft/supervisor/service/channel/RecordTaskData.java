package com.arcsoft.supervisor.service.channel;

public class RecordTaskData {
    private String name;
    private Integer channelId;
    private Integer profile;
    private String outputPath;
    private String fileName;
    private boolean generateThumb;
    private Integer thumbWidth;
    private Integer keepTimes;
    private Integer segmentLength;

    public RecordTaskData(String name, Integer channelId, Integer profile, String outputPath, Integer keepTimes) {
        this.name = name;
        this.channelId = channelId;
        this.profile = profile;
        this.outputPath = outputPath;
        this.fileName = this.name + "-${yyyy}${MM}${dd}-${HH}${mm}${ss}";
        this.generateThumb = false;
        this.thumbWidth = 640;
        this.keepTimes = keepTimes;
        this.segmentLength = 600;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public Integer getProfile() {
        return profile;
    }

    public void setProfile(Integer profile) {
        this.profile = profile;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isGenerateThumb() {
        return generateThumb;
    }

    public void setGenerateThumb(boolean generateThumb) {
        this.generateThumb = generateThumb;
    }

    public Integer getThumbWidth() {
        return thumbWidth;
    }

    public void setThumbWidth(Integer thumbWidth) {
        this.thumbWidth = thumbWidth;
    }

    public Integer getKeepTimes() {
        return keepTimes;
    }

    public void setKeepTimes(Integer keepTimes) {
        this.keepTimes = keepTimes;
    }

    public Integer getSegmentLength() {
        return segmentLength;
    }

    public void setSegmentLength(Integer segmentLength) {
        this.segmentLength = segmentLength;
    }
}
