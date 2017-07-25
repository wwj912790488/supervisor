package com.arcsoft.supervisor.thirdparty.push.voice;

import com.arcsoft.supervisor.utils.DateHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * Defines data-structure for push content detect log.
 *
 * @author zw.
 */
public class ContentDetectData {

    private static final String FORMAT_PATTER = "yyyy,MM,dd-HH:mm:ss.SSS";

    private int id;

    private String name;

    private int type;

    private String startTime;

    private String endTime;

    private String guid;

    private Long logid;

    public ContentDetectData() {
    }

    public ContentDetectData(int id, String name, int type, Date startTime, Date endTime,Long logid) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.startTime = fromDate(startTime);
        this.endTime = fromDate(endTime);
        this.logid = logid;
    }

    public ContentDetectData(int id, String name, int type, Date startTime, Date endTime,String guid,Long logid) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.startTime = fromDate(startTime);
        this.endTime = fromDate(endTime);
        this.guid =guid;
        this.logid = logid;
    }

    public ContentDetectData(int id, String name, int type, Date startTime, Date endTime,String guid,Long logid,String fmat) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.startTime = fromDateTime(startTime,fmat);
        this.endTime = fromDateTime(endTime,fmat);
        this.guid =guid;
        this.logid = logid;
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String fromDate(Date date) {
        return date == null ? StringUtils.EMPTY : DateHelper.formatDateTime(date, FORMAT_PATTER);
    }

    public String fromDateTime(Date date,String patten) {
        return date == null ? StringUtils.EMPTY : DateHelper.formatDateTime(date, patten==null?FORMAT_PATTER:patten);
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Long getLogid() {
        return logid;
    }

    public void setLogid(Long logid) {
        this.logid = logid;
    }
}
