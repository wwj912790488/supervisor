package com.arcsoft.supervisor.model.vo.task.profile;

import com.fasterxml.jackson.annotation.*;

/**
 * Value object for holds video profile.
 *
 * @author zw.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "videocodec"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = VideoProfileWithH264.class, name = "H264")
})
public abstract class VideoProfile {

    //Defines generic field of video profile

    private Boolean videopassthrough;

    public Boolean getVideopassthrough() {
        return videopassthrough;
    }

    public void setVideopassthrough(Boolean videopassthrough) {
        this.videopassthrough = videopassthrough;
    }

    @JsonProperty("videocodec")
    private Codec codec;

    public enum Codec {
        H264
    }

    @JsonProperty("videocodecprofile")
    private CodecProfile codecProfile;


    public enum CodecProfile {
        High, Main, Baseline
    }

    @JsonProperty("videocodeclevel")
    private CodecLevel codecLevel;

    public enum CodecLevel {
        AUTO("-1"), L10("10"), L11("11"), L12("12"), L13("13"),
        L20("20"), L21("21"), L22("22"), L30("30"), L31("31"),
        L32("32"), L40("40"), L41("41"), L42("42"), L50("50"), L51("51");

        final String level;

        CodecLevel(String level) {
            this.level = level;
        }

        @JsonValue
        public String getLevel() {
            return level;
        }
    }

    @JsonProperty("videoheight")
    private Integer height;

    @JsonProperty("videowidth")
    private Integer width;

    @JsonProperty("videoPAR")
    private Par par;

    public enum Par {
        SOURCE("source"), CUSTOM("custom"), P16x9("16:9"), P4x3("4:3"), P40x33("40:33"), P16x11("16:11");

        final String par;

        Par(String par) {
            this.par = par;
        }

        @JsonValue
        public String getPar() {
            return par;
        }
    }

    @JsonProperty("videosourcePAR")
    private Boolean followSource;

    @JsonProperty("videoPARX")
    private Integer parX;

    @JsonProperty("videoPARY")
    private Integer parY;

    @JsonProperty("videosmartborder")
    private ModeOfWidthAndHeightSwitch modeOfWidthAndHeightSwitch;

    public enum ModeOfWidthAndHeightSwitch {
        SMART_BLACK_BORDER(1), AUTO_CUT(2), LINEAR_STRETCH(0);

        final int mode;

        ModeOfWidthAndHeightSwitch(int mode) {
            this.mode = mode;
        }

        @JsonValue
        public int getMode() {
            return mode;
        }

        @JsonCreator
        public static ModeOfWidthAndHeightSwitch fromMode(int mode) {
            for (ModeOfWidthAndHeightSwitch modeOfWidthAndHeightSwitch : values()) {
                if (modeOfWidthAndHeightSwitch.getMode() == mode) {
                    return modeOfWidthAndHeightSwitch;
                }
            }
            return null;
        }
    }

    @JsonProperty("videoratecontrol")
    private ModeOfBitrateControl bitrateControl;

    public enum ModeOfBitrateControl {
        VBR, CBR, ABR, CQ, CRF
    }

    @JsonProperty("videobitrate")
    private Integer bitrate;

    @JsonView(VideoProfile.class)
    @JsonProperty("videomaxbitrate")
    private Integer maxBitrate;

    @JsonProperty("videoqualityleveldisp")
    private QualityLevel qualityLevel;

    public enum QualityLevel {
        L0(-1), L1(0), L2(1), L3(2), L4(3), L5(4);

        final int level;

        QualityLevel(int level) {
            this.level = level;
        }

        @JsonValue
        public int getLevel() {
            return level;
        }

        @JsonCreator
        public static QualityLevel fromLevel(int level) {
            for (QualityLevel qualityLevel : values()) {
                if (qualityLevel.getLevel() == level) {
                    return qualityLevel;
                }
            }
            return null;
        }
    }


    @JsonProperty("videobufferfill")
    private Integer initializedBufferFill;

    @JsonProperty("videobuffersize")
    private Integer bufferSize;

    @JsonProperty("videoquantizer")
    private Integer quantizer;

    @JsonProperty("videogopsize")
    private Integer gopsize;

    @JsonProperty("videobframe")
    private Integer bframe;

    @JsonProperty("videoreferenceframe")
    private Integer referenceFrame;

    @JsonProperty("videoCABAC")
    private Boolean cabac;

    @JsonProperty("videointraprediction")
    private Boolean interFramePrediction;

    @JsonProperty("videotransform")
    private Boolean transform;

    @JsonProperty("videoSCD")
    private Boolean scd;

    //Define graphics processing

    @JsonProperty("videodeinterlace")
    private Deinterlace deinterlace;

    public enum Deinterlace {
        CLOSE(0), OPEN(1), AUTO(2);

        final int value;

        Deinterlace(int value) {
            this.value = value;
        }

        @JsonValue
        public int getValue() {
            return value;
        }

        @JsonCreator
        public static Deinterlace fromValue(int value) {
            for (Deinterlace deinterlace : values()) {
                if (deinterlace.getValue() == value) {
                    return deinterlace;
                }
            }
            return null;
        }
    }

    @JsonProperty("videodeinterlacealg")
    private DeinterlaceAlgorithm deinterlaceAlgorithm;

    public enum DeinterlaceAlgorithm {
        BLOB(1),QUALITY(2), SPEED(3);

        final int value;

        DeinterlaceAlgorithm(int value) {
            this.value = value;
        }

        @JsonValue
        public int getValue() {
            return value;
        }

        @JsonCreator
        public static DeinterlaceAlgorithm fromValue(int value) {
            for (DeinterlaceAlgorithm deinterlaceAlgorithm : values()) {
                if (deinterlaceAlgorithm.getValue() == value) {
                    return deinterlaceAlgorithm;
                }
            }
            return null;
        }
    }

    @JsonProperty("videoresizealg")
    private ResizeAlgorithm resizeAlgorithm;

    public enum ResizeAlgorithm {
        QUALITY(3), SPEED(1);

        final int value;

        ResizeAlgorithm(int value) {
            this.value = value;
        }

        @JsonValue
        public int getValue() {
            return value;
        }

        @JsonCreator
        public static ResizeAlgorithm fromValue(int value) {
            for (ResizeAlgorithm resizeAlgorithm : values()) {
                if (resizeAlgorithm.getValue() == value) {
                    return resizeAlgorithm;
                }
            }
            return null;
        }
    }

    @JsonProperty("videodenoise")
    private Denoise denoise;

    public enum Denoise {
        D0(0), D1(1), D2(2), D3(3), D4(4), D5(5), D6(6), D7(7), D8(8), D9(9), D10(10);

        final int value;

        Denoise(int value) {
            this.value = value;
        }

        @JsonValue
        public int getValue() {
            return value;
        }

        @JsonCreator
        public static Denoise fromValue(int value) {
            for (Denoise denoise : values()) {
                if (denoise.getValue() == value) {
                    return denoise;
                }
            }
            return null;
        }
    }

    @JsonProperty("videodeblock")
    private Boolean deblock;

    @JsonProperty("videosharpen")
    private Sharpen sharpen;

    public enum Sharpen {
        S0(0), S1(1), S2(2), S3(3), S4(4), S5(5);

        final int value;

        Sharpen(int value) {
            this.value = value;
        }

        @JsonValue
        public int getValue() {
            return value;
        }

        @JsonCreator
        public static Sharpen fromValue(int value) {
            for (Sharpen sharpen : values()) {
                if (sharpen.getValue() == value) {
                    return sharpen;
                }
            }
            return null;
        }
    }

    @JsonProperty("videoantialias")
    private Boolean antiAlias;

    @JsonProperty("videobright")
    private Integer bright;

    @JsonProperty("videocontrast")
    private Integer contrast;

    @JsonProperty("videosaturation")
    private Integer saturation;

    @JsonProperty("videohue")
    private Integer hue;

    @JsonProperty("videodelight")
    private Delight delight;

    public enum Delight {
        DL0(0), DL1(1), DL2(2), DL3(3), DL4(4), DL5(5), DL6(6);

        final int value;

        Delight(int value) {
            this.value = value;
        }

        @JsonValue
        public int getValue() {
            return value;
        }

        @JsonCreator
        public static Delight fromValue(int value) {
            for (Delight delight : values()) {
                if (delight.getValue() == value) {
                    return delight;
                }
            }
            return null;
        }
    }


    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Boolean getFollowSource() {
        return followSource;
    }

    public void setFollowSource(Boolean followSource) {
        this.followSource = followSource;
    }

    public Integer getParX() {
        return parX;
    }

    public void setParX(Integer parX) {
        this.parX = parX;
    }

    public Integer getParY() {
        return parY;
    }

    public void setParY(Integer parY) {
        this.parY = parY;
    }



    public Integer getBitrate() {
        return bitrate;
    }

    public void setBitrate(Integer bitrate) {
        this.bitrate = bitrate;
    }

    public Integer getMaxBitrate() {
        return maxBitrate;
    }

    public void setMaxBitrate(Integer maxBitrate) {
        this.maxBitrate = maxBitrate;
    }


    public Integer getInitializedBufferFill() {
        return initializedBufferFill;
    }

    public void setInitializedBufferFill(Integer initializedBufferFill) {
        this.initializedBufferFill = initializedBufferFill;
    }

    public Integer getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(Integer bufferSize) {
        this.bufferSize = bufferSize;
    }

    public Integer getQuantizer() {
        return quantizer;
    }

    public void setQuantizer(Integer quantizer) {
        this.quantizer = quantizer;
    }

    public Integer getGopsize() {
        return gopsize;
    }

    public void setGopsize(Integer gopsize) {
        this.gopsize = gopsize;
    }

    public Integer getBframe() {
        return bframe;
    }

    public void setBframe(Integer bframe) {
        this.bframe = bframe;
    }

    public Integer getReferenceFrame() {
        return referenceFrame;
    }

    public void setReferenceFrame(Integer referenceFrame) {
        this.referenceFrame = referenceFrame;
    }

    public Boolean getCabac() {
        return cabac;
    }

    public void setCabac(Boolean cabac) {
        this.cabac = cabac;
    }

    public Boolean getInterFramePrediction() {
        return interFramePrediction;
    }

    public void setInterFramePrediction(Boolean interFramePrediction) {
        this.interFramePrediction = interFramePrediction;
    }

    public Boolean getTransform() {
        return transform;
    }

    public void setTransform(Boolean transform) {
        this.transform = transform;
    }

    public Boolean getScd() {
        return scd;
    }

    public void setScd(Boolean scd) {
        this.scd = scd;
    }


    public Boolean getDeblock() {
        return deblock;
    }

    public void setDeblock(Boolean deblock) {
        this.deblock = deblock;
    }


    public Boolean getAntiAlias() {
        return antiAlias;
    }

    public void setAntiAlias(Boolean antiAlias) {
        this.antiAlias = antiAlias;
    }

    public Integer getBright() {
        return bright;
    }

    public void setBright(Integer bright) {
        this.bright = bright;
    }

    public Integer getContrast() {
        return contrast;
    }

    public void setContrast(Integer contrast) {
        this.contrast = contrast;
    }

    public Integer getSaturation() {
        return saturation;
    }

    public void setSaturation(Integer saturation) {
        this.saturation = saturation;
    }

    public Integer getHue() {
        return hue;
    }

    public void setHue(Integer hue) {
        this.hue = hue;
    }

    public Codec getCodec() {
        return codec;
    }

    public void setCodec(Codec codec) {
        this.codec = codec;
    }

    public CodecProfile getCodecProfile() {
        return codecProfile;
    }

    public void setCodecProfile(CodecProfile codecProfile) {
        this.codecProfile = codecProfile;
    }

    public CodecLevel getCodecLevel() {
        return codecLevel;
    }

    public void setCodecLevel(CodecLevel codecLevel) {
        this.codecLevel = codecLevel;
    }

    public Par getPar() {
        return par;
    }

    public void setPar(Par par) {
        this.par = par;
    }

    public ModeOfWidthAndHeightSwitch getModeOfWidthAndHeightSwitch() {
        return modeOfWidthAndHeightSwitch;
    }

    public void setModeOfWidthAndHeightSwitch(ModeOfWidthAndHeightSwitch modeOfWidthAndHeightSwitch) {
        this.modeOfWidthAndHeightSwitch = modeOfWidthAndHeightSwitch;
    }

    public ModeOfBitrateControl getBitrateControl() {
        return bitrateControl;
    }

    public void setBitrateControl(ModeOfBitrateControl bitrateControl) {
        this.bitrateControl = bitrateControl;
    }

    public QualityLevel getQualityLevel() {
        return qualityLevel;
    }

    public void setQualityLevel(QualityLevel qualityLevel) {
        this.qualityLevel = qualityLevel;
    }

    public Deinterlace getDeinterlace() {
        return deinterlace;
    }

    public void setDeinterlace(Deinterlace deinterlace) {
        this.deinterlace = deinterlace;
    }

    public DeinterlaceAlgorithm getDeinterlaceAlgorithm() {
        return deinterlaceAlgorithm;
    }

    public void setDeinterlaceAlgorithm(DeinterlaceAlgorithm deinterlaceAlgorithm) {
        this.deinterlaceAlgorithm = deinterlaceAlgorithm;
    }

    public ResizeAlgorithm getResizeAlgorithm() {
        return resizeAlgorithm;
    }

    public void setResizeAlgorithm(ResizeAlgorithm resizeAlgorithm) {
        this.resizeAlgorithm = resizeAlgorithm;
    }

    public Denoise getDenoise() {
        return denoise;
    }

    public void setDenoise(Denoise denoise) {
        this.denoise = denoise;
    }

    public Sharpen getSharpen() {
        return sharpen;
    }

    public void setSharpen(Sharpen sharpen) {
        this.sharpen = sharpen;
    }

    public Delight getDelight() {
        return delight;
    }

    public void setDelight(Delight delight) {
        this.delight = delight;
    }
}
