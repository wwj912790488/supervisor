package com.arcsoft.supervisor.service.channel;

public class RecordChannelData {
    private String name;
    private String type;
    private String uri;
    private Integer programId;
    private Integer videoId;
    private Integer audioId;

    public RecordChannelData(String name, String uri, Integer programId, Integer audioId) {
        this.name = name;
        this.type = "UDP";
        this.uri = uri;
        this.programId = programId;
        this.videoId = -1;
        this.audioId = audioId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Integer getProgramId() {
        return programId;
    }

    public void setProgramId(Integer programId) {
        this.programId = programId;
    }

    public Integer getVideoId() {
        return videoId;
    }

    public void setVideoId(Integer videoId) {
        this.videoId = videoId;
    }

    public Integer getAudioId() {
        return audioId;
    }

    public void setAudioId(Integer audioId) {
        this.audioId = audioId;
    }
}
