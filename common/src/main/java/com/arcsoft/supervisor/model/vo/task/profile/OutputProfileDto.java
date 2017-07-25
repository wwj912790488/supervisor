package com.arcsoft.supervisor.model.vo.task.profile;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Value object for template of output profile.
 *
 * @author zw.
 */
public class OutputProfileDto {

    public static final String NODE_NAME_VIDEOPROFILES = "videoprofiles";

    public static final String NODE_NAME_AUDIOPROFILES = "audioprofiles";

    private Integer id;

    private String name;

    private String description;

    @JsonProperty(NODE_NAME_VIDEOPROFILES)
    private List<VideoProfile> videoprofiles;

    @JsonProperty(NODE_NAME_AUDIOPROFILES)
    private List<AudioProfile> audioprofiles;

    public OutputProfileDto() {
    }

    public OutputProfileDto(List<VideoProfile> videoprofiles, List<AudioProfile> audioprofiles) {
        this.videoprofiles = videoprofiles;
        this.audioprofiles = audioprofiles;
    }

    public OutputProfileDto(VideoProfile videoProfile, AudioProfile audioProfile) {
        this.videoprofiles = new ArrayList<>();
        this.audioprofiles = new ArrayList<>();
        this.videoprofiles.add(videoProfile);
        this.audioprofiles.add(audioProfile);
    }

    public List<VideoProfile> getVideoprofiles() {
        return videoprofiles;
    }

    public void setVideoprofiles(List<VideoProfile> videoprofiles) {
        this.videoprofiles = videoprofiles;
    }

    public List<AudioProfile> getAudioprofiles() {
        return audioprofiles;
    }

    public void setAudioprofiles(List<AudioProfile> audioprofiles) {
        this.audioprofiles = audioprofiles;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
