package com.arcsoft.supervisor.model.domain.log;

import com.arcsoft.supervisor.model.vo.task.MediaCheckType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;

/**
 * Entity class for content detect result.
 *
 * @author zw.
 */
@Entity
@Table(name = "content_detect_log")
public class ContentDetectLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * The value definition to see {@link com.arcsoft.supervisor.model.vo.task.MediaCheckType}
     */
    private int type;

    @Column(name = "start_time")
    private Long startTime;

    @Column(name = "end_time")
    private Long endTime;

    @Column(name = "audio_sound_track")
    private Integer soundTrack;

    @Column(name = "task_id")
    private Integer taskId;

    @Column(name = "channel_id")
    private Integer channelId;

    @Column(name = "channel_name")
    private String channelName;

    @Column(name = "video_file_path")
    private String videoFilePath;

    @Column(name = "start_offset")
    private Long startOffset;

    @Column(name = "guid")
    private String guid;

    @Column(name = "confirm_datetime")
    private Date confirmdate;

//    @Column(name = "confirm_User_id")
//    private Integer confiruser;

    @Transient
    private Date startTimeAsDate;

    @Transient
    private Date endTimeAsDate;

    public Date getStartTimeAsDate() {
        return this.startTime > 0 ? new Date(this.startTime) : null;
    }

    public void setStartTimeAsDate(Date startdate) {
        this.startTimeAsDate = startdate;
    }

    public Date getEndTimeAsDate() {
        return this.endTime > 0 ? new Date(this.endTime) : null;
    }

    public void setEndTimeAsDate(Date enddate) {
       this.endTimeAsDate =  enddate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Integer getSoundTrack() {
        return soundTrack;
    }

    public void setSoundTrack(Integer soundTrack) {
        this.soundTrack = soundTrack;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getVideoFilePath() {
        return videoFilePath;
    }

    public void setVideoFilePath(String videoFilePath) {
        this.videoFilePath = videoFilePath;
    }

    public Long getStartOffset() {
        return startOffset;
    }

    public void setStartOffset(Long startOffset) {
        this.startOffset = startOffset;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public Date getConfirmdate() {
        return confirmdate;
    }

    public void setConfirmdate(Date confirmdate) {
        this.confirmdate= confirmdate;
    }

 //   public Integer getConfiruser() {
//        return confiruser;
 //   }

 //   public void setConfiruser(Integer confiruser) {
//        this.confiruser= confiruser;
//    }

    @JsonIgnore
    public String getTypeTranslate() {
        switch(type) {
            case MediaCheckType.CHECK_TYPE_BLACK_FIELD_INDEX:
                return "黑帧";
            case MediaCheckType.CHECK_TYPE_GREEN_FIELD_INDEX:
                return "绿帧";
            case MediaCheckType.CHECK_TYPE_STATIC_FRAME_INDEX:
                return "静帧";
            case MediaCheckType.CHECK_TYPE_COLOR_BAR_INDEX:
                return "";
            case MediaCheckType.CHECK_TYPE_MOSAIC_INDEX:
                return "马赛克";
            case MediaCheckType.CHECK_TYPE_STREAM_INTERRUPT_INDEX:
                return "流中断";
            case MediaCheckType.CHECK_TYPE_MUTE_THRESHOLD_INDEX:
                return "静音";
            case MediaCheckType.CHECK_TYPE_VOLUME_LOW_INDEX:
                return "低音";
            case MediaCheckType.CHECK_TYPE_VOLUME_LOUD_INDEX:
                return "高音";
            case MediaCheckType.CHECK_TYPE_VOLUME_LOUDN_INDEX:
                return "高音";
            case MediaCheckType.CHECK_TYPE_TONE_INDEX:
                return "";
            case MediaCheckType.CHECK_TYPE_BREAK_INDEX:
                return "爆音";
            case MediaCheckType.SIGNAL_STREAM_CCERROR:
                return "连续计数器错误";
            case MediaCheckType.SIGNAL_STREAM_NOAUDIO:
                return "音频丢失";
            case MediaCheckType.SIGNAL_STREAM_NOVIDEO:
                return "视频丢失";
            case MediaCheckType.SIGNAL_STREAM_INTERRUPT:
                return "信源中断";
            default:
                return "未知错误";
        }
    }

}
