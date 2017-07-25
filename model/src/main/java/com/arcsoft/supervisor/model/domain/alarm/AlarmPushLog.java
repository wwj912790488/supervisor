package com.arcsoft.supervisor.model.domain.alarm;

import com.arcsoft.supervisor.model.domain.log.ContentDetectLog;
import com.arcsoft.supervisor.model.domain.user.User;
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
@Table(name = "alarm_push_log")
public class AlarmPushLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
   
//    //return msg id
//    private String msgId;
//
//	//return msg send time
//    private Long msgSendTime;
    
    private int type;
    
    @Column(name = "start_time")
    private Long startTime;

    @Column(name = "end_time")
    private Long endTime;

    @Column(name = "channel_id")
    private Integer channelId;

    @Column(name = "channel_name")
    private String channelName;

//    @JsonIgnore
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "content_detect_log_id")
//    private ContentDetectLog contentDetectLog;
//
//    @JsonIgnore
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
//    private User user;
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alarm_config_id")
    private AlarmConfig alarmConfig;



    @Transient
    private Date startTimeAsDate;
    @Transient
    private Date endTimeAsDate;

    public Date getStartTimeAsDate() {
        return this.startTime > 0 ? new Date(this.startTime) : null;
    }

    public Date getEndTimeAsDate() {
        return this.endTime > 0 ? new Date(this.endTime) : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

//    public String getMsgId() {
//		return msgId;
//	}
//
//	public void setMsgId(String msgId) {
//		this.msgId = msgId;
//	}
//
//	public Long getMsgSendTime() {
//		return msgSendTime;
//	}
//
//	public void setMsgSendTime(Long msgSendTime) {
//		this.msgSendTime = msgSendTime;
//	}

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

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public AlarmConfig getAlarmConfig() {
        return alarmConfig;
    }

    public void setAlarmConfig(AlarmConfig alarmConfig) {
        this.alarmConfig = alarmConfig;
    }

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
