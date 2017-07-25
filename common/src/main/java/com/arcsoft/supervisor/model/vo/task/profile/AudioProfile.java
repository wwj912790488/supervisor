package com.arcsoft.supervisor.model.vo.task.profile;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Value object for holds the audio profile.
 *
 * @author zw.
 */
public class AudioProfile {

    private Boolean audiopassthrough;
    private Boolean audiomix;

    private Codec audiocodec;

    public enum Codec {
        AAC,MP2
    }

    private CodecProfile audiocodecprofile;

    public enum CodecProfile {
        LC, MPEG2LC, HEV1, HEV2
    }

    private Channel audiochannel;

    public enum Channel {
        MONO(1), STEREO(2), C6(6);

        final int value;

        Channel(int value) {
            this.value = value;
        }

        @JsonValue
        public int getValue() {
            return value;
        }

        @JsonCreator
        public static Channel fromValue(int value) {
            for (Channel channel : values()) {
                if (channel.getValue() == value) {
                    return channel;
                }
            }
            return null;
        }
    }

    private SampleRate audiosamplerate;

    public enum SampleRate {
        S8K(8000), S22K(22050), S24K(24000), S32K(32000), S44K(44100), S48K(48000);

        final int value;

        SampleRate(int value) {
            this.value = value;
        }

        @JsonValue
        public int getValue() {
            return value;
        }

        @JsonCreator
        public static SampleRate fromValue(int value) {
            for (SampleRate sampleRate : values()) {
                if (sampleRate.getValue() == value) {
                    return sampleRate;
                }
            }
            return null;
        }
    }

    private Integer audiobitrate;

    private ModeOfVolume audiovolumemode;

    public enum ModeOfVolume {
        SOURCE(0), INCREASE(1), BALANCE(2);

        final int value;

        ModeOfVolume(int value) {
            this.value = value;
        }

        @JsonValue
        public int getValue() {
            return value;
        }

        @JsonCreator
        public static ModeOfVolume fromValue(int value) {
            for (ModeOfVolume modeOfVolume : values()) {
                if (modeOfVolume.getValue() == value) {
                    return modeOfVolume;
                }
            }
            return null;
        }
    }

    private BoostLevel audioboostlevel;

    public enum BoostLevel {
        BL0(0), BL1(1), BL2(2), BL3(3), BL4(4), BL5(5), BL6(6), BL7(7), BL8(8), BL9(9), BL10(10);

        final int value;

        BoostLevel(int value) {
            this.value = value;
        }

        @JsonValue
        public int getValue() {
            return value;
        }

        @JsonCreator
        public static BoostLevel fromValue(int value) {
            for (BoostLevel boostLevel : values()) {
                if (boostLevel.getValue() == value) {
                    return boostLevel;
                }
            }
            return null;
        }
    }

    private BalanceLevel audiobalancelevel;

    public enum BalanceLevel {
        LOW(0), MID(5), HIGH(10);

        final int value;

        BalanceLevel(int value) {
            this.value = value;
        }

        @JsonValue
        public int getValue() {
            return value;
        }

        @JsonCreator
        public static BalanceLevel fromValue(int value) {
            for (BalanceLevel balanceLevel : values()) {
                if (balanceLevel.getValue() == value) {
                    return balanceLevel;
                }
            }
            return null;
        }
    }

    private Integer audiobalancedb;

    private ChannelProcess audiochannelprocessing;

    public enum ChannelProcess {
        None, Left, Right, Mix
    }

    public Boolean isAudiopassthrough() {
        return audiopassthrough;
    }

    public void setAudiopassthrough(Boolean audiopassthrough) {
        this.audiopassthrough = audiopassthrough;
    }

    public Boolean getAudiomix() {
        return audiomix;
    }

    public void setAudiomix(Boolean audiomix) {
        this.audiomix = audiomix;
    }

    public Integer getAudiobitrate() {
        return audiobitrate;
    }

    public void setAudiobitrate(Integer audiobitrate) {
        this.audiobitrate = audiobitrate;
    }


    public Integer getAudiobalancedb() {
        return audiobalancedb;
    }

    public void setAudiobalancedb(Integer audiobalancedb) {
        this.audiobalancedb = audiobalancedb;
    }

    public Boolean getAudiopassthrough() {
        return audiopassthrough;
    }

    public Codec getAudiocodec() {
        return audiocodec;
    }

    public void setAudiocodec(Codec audiocodec) {
        this.audiocodec = audiocodec;
    }

    public CodecProfile getAudiocodecprofile() {
        return audiocodecprofile;
    }

    public void setAudiocodecprofile(CodecProfile audiocodecprofile) {
        this.audiocodecprofile = audiocodecprofile;
    }

    public Channel getAudiochannel() {
        return audiochannel;
    }

    public void setAudiochannel(Channel audiochannel) {
        this.audiochannel = audiochannel;
    }

    public SampleRate getAudiosamplerate() {
        return audiosamplerate;
    }

    public void setAudiosamplerate(SampleRate audiosamplerate) {
        this.audiosamplerate = audiosamplerate;
    }

    public ModeOfVolume getAudiovolumemode() {
        return audiovolumemode;
    }

    public void setAudiovolumemode(ModeOfVolume audiovolumemode) {
        this.audiovolumemode = audiovolumemode;
    }

    public BoostLevel getAudioboostlevel() {
        return audioboostlevel;
    }

    public void setAudioboostlevel(BoostLevel audioboostlevel) {
        this.audioboostlevel = audioboostlevel;
    }

    public BalanceLevel getAudiobalancelevel() {
        return audiobalancelevel;
    }

    public void setAudiobalancelevel(BalanceLevel audiobalancelevel) {
        this.audiobalancelevel = audiobalancelevel;
    }

    public ChannelProcess getAudiochannelprocessing() {
        return audiochannelprocessing;
    }

    public void setAudiochannelprocessing(ChannelProcess audiochannelprocessing) {
        this.audiochannelprocessing = audiochannelprocessing;
    }
}
