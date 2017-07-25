package com.arcsoft.supervisor.model.vo.task.cd;

import com.arcsoft.supervisor.model.vo.task.MediaCheckType;

/**
 * A dto object to transmission the content detect result.
 *
 * @author zw.
 */
public class ContentDetectResult {

    private int taskid;
    private int checkType;
    private long startTime;
    private long endTime;
    /**
     *  The value is base on checkType. if the checkType is audio then this value indicate audio channel.
     * */
    private int value2;

    /**
     * THe unique id.
     */
    private String guid;

    /**
     * The identify value of channel.
     */
    private int channelId;

    public int getTaskid() {
        return taskid;
    }

    public void setTaskid(int taskid) {
        this.taskid = taskid;
    }

    public int getCheckType() {
        return checkType;
    }

    public void setCheckType(int checkType) {
        this.checkType = checkType;
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

    public int getValue2() {
        return value2;
    }

    public void setValue2(int value2) {
        this.value2 = value2;
    }

    public boolean isAudio(){
        return checkType == MediaCheckType.CHECK_TYPE_BREAK_INDEX
                || checkType == MediaCheckType.CHECK_TYPE_MUTE_THRESHOLD_INDEX;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ContentDetectResult{");
        sb.append("taskid=").append(taskid);
        sb.append(", checkType=").append(checkType);
        sb.append('}');
        return sb.toString();
    }
}
