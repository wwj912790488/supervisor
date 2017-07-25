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
@Table(name = "alarm_pushed_log_info")
public class AlarmPushedLogInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alarm_device_id")
    private AlarmDevice alarmDevice;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alarm_push_log_id")
    private AlarmPushLog alarmPushLog;

    //return msg id
    private String msgId;

    //return msg send time
    private Long msgSendTime;

    private Boolean msgForAll;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public Long getMsgSendTime() {
		return msgSendTime;
	}

	public void setMsgSendTime(Long msgSendTime) {
		this.msgSendTime = msgSendTime;
	}

    public AlarmDevice getAlarmDevice() {
        return alarmDevice;
    }

    public void setAlarmDevice(AlarmDevice alarmDevice) {
        this.alarmDevice = alarmDevice!=null?alarmDevice:null;
    }

    public AlarmPushLog getAlarmPushLog() {
        return alarmPushLog;
    }

    public void setAlarmPushLog(AlarmPushLog alarmPushLog) {
        this.alarmPushLog = alarmPushLog;
    }

    public Boolean getMsgForAll() {
        return msgForAll;
    }

    public void setMsgForAll(Boolean msgForAll) {
        this.msgForAll = msgForAll;
    }
}
