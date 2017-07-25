package com.arcsoft.supervisor.thirdparty.baidupush;

import com.arcsoft.supervisor.model.vo.task.MediaCheckType;
import com.arcsoft.supervisor.utils.DateHelper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * Defines data-structure for push content detect log.
 *
 * @author jt.
 */
public class ContentDetectLogData {

    private static final String FORMAT_PATTER = "yyyy,MM,dd-HH:mm:ss.SSS";

    private int id;

    private String name;

    private int type;

    private long startTime;

    private long endTime;

    private String startTimeStr;

    private String endTimeStr;

    public ContentDetectLogData() {
    }

    public ContentDetectLogData(int id, String name, int type, Date startTime, Date endTime) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.startTime = startTime.getTime();
        this.endTime = endTime.getTime();
        this.startTimeStr = fromDate(startTime);
        this.endTimeStr = fromDate(endTime);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getStartTimeStr() {
        return startTimeStr;
    }

    public void setStartTimeStr(String startTimeStr) {
        this.startTimeStr = startTimeStr;
    }

    public String getEndTimeStr() {
        return endTimeStr;
    }

    public void setEndTimeStr(String endTimeStr) {
        this.endTimeStr = endTimeStr;
    }

    public String fromDate(Date date) {
        return date == null ? StringUtils.EMPTY : DateHelper.formatDateTime(date, FORMAT_PATTER);
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
