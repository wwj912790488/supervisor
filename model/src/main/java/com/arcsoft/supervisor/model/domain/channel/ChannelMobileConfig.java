package com.arcsoft.supervisor.model.domain.channel;

import javax.persistence.*;

/**
 * Mobile terminal config options for channel.
 *
 * @author zw.
 */
@Entity
@Table(name = "channel_mobile_config")
public class ChannelMobileConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /**
     * The type of the config. 0: sd 1: hd
     */
    private Byte type;
    @Column(name = "video_bitrate")
    private Integer videoBitrate;
    @Column(name = "audio_bitrate")
    private Integer audioBitrate;
    private Integer width;
    private Integer height;
    private String address;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel;
    private boolean deinterlace=false;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
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

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDeinterlace(boolean deinterlace){this.deinterlace=deinterlace;}
    public boolean getDeinterlace(){return deinterlace;}

    public boolean isDeinterlace() {
        return deinterlace;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChannelMobileConfig)) return false;

        ChannelMobileConfig that = (ChannelMobileConfig) o;
        if (isDeinterlace() != that.isDeinterlace()) return false;
        if (getType() != null ? !getType().equals(that.getType()) : that.getType() != null) return false;
        if (getVideoBitrate() != null ? !getVideoBitrate().equals(that.getVideoBitrate()) : that.getVideoBitrate() != null)
            return false;
        if (getAudioBitrate() != null ? !getAudioBitrate().equals(that.getAudioBitrate()) : that.getAudioBitrate() != null)
            return false;
        if (getWidth() != null ? !getWidth().equals(that.getWidth()) : that.getWidth() != null) return false;
        return getHeight() != null ? getHeight().equals(that.getHeight()) : that.getHeight() == null;

    }

    @Override
    public int hashCode() {
        int result = getType() != null ? getType().hashCode() : 0;
        result = 31 * result + (getVideoBitrate() != null ? getVideoBitrate().hashCode() : 0);
        result = 31 * result + (getAudioBitrate() != null ? getAudioBitrate().hashCode() : 0);
        result = 31 * result + (getWidth() != null ? getWidth().hashCode() : 0);
        result = 31 * result + (getHeight() != null ? getHeight().hashCode() : 0);
        result = 31 * result + (isDeinterlace() ? 1 : 0);
        return result;
    }
}
