package com.arcsoft.supervisor.model.vo.task.profile;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * H264 of video profile
 *
 * @author zw.
 */
@JsonTypeName("H264")
public class VideoProfileWithH264 extends VideoProfile {

    @JsonProperty("videoframerate")
    private String framerate;

    @JsonProperty("videosourceframerate")
    private Boolean followSourceFramerate;

    @JsonProperty("videoframerateX")
    private Integer framerateX;

    @JsonProperty("videoframerateY")
    private Integer framerateY;

    @JsonProperty("videoframerateconversionmode")
    private Boolean modeOfFramerateConversion;

    @JsonProperty("videointerlace")
    private ModeOfFrameAndField modeOfFrameAndFiled;

    public enum ModeOfFrameAndField {
        SOURCE("-1"), FRAME("0"), AUTOFIELD("2"), MBAFF("3"), PAFF("4");

        final String value;

        ModeOfFrameAndField(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }
    }

    @JsonProperty("videotopfieldfirst")
    private PriorityOfField priorityOfField;

    public enum PriorityOfField {
        SOURCE("-1"), BOTTOM("0"), TOP("1");

        final String value;

        PriorityOfField(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }
    }


    @Override
    public Codec getCodec() {
        return Codec.H264;
    }

    @Override
    public void setCodec(Codec codec) {
        super.setCodec(Codec.H264);
    }

    public String getFramerate() {
        return framerate;
    }

    public void setFramerate(String framerate) {
        this.framerate = framerate;
    }

    public Boolean getFollowSourceFramerate() {
        return followSourceFramerate;
    }

    public void setFollowSourceFramerate(Boolean followSourceFramerate) {
        this.followSourceFramerate = followSourceFramerate;
    }

    public Integer getFramerateX() {
        return framerateX;
    }

    public void setFramerateX(Integer framerateX) {
        this.framerateX = framerateX;
    }

    public Integer getFramerateY() {
        return framerateY;
    }

    public void setFramerateY(Integer framerateY) {
        this.framerateY = framerateY;
    }

    public Boolean getModeOfFramerateConversion() {
        return modeOfFramerateConversion;
    }

    public void setModeOfFramerateConversion(Boolean modeOfFramerateConversion) {
        this.modeOfFramerateConversion = modeOfFramerateConversion;
    }

    public ModeOfFrameAndField getModeOfFrameAndFiled() {
        return modeOfFrameAndFiled;
    }

    public void setModeOfFrameAndFiled(ModeOfFrameAndField modeOfFrameAndFiled) {
        this.modeOfFrameAndFiled = modeOfFrameAndFiled;
    }

    public PriorityOfField getPriorityOfField() {
        return priorityOfField;
    }

    public void setPriorityOfField(PriorityOfField priorityOfField) {
        this.priorityOfField = priorityOfField;
    }
}
