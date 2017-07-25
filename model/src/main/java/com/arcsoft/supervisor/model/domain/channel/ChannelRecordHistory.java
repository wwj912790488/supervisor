package com.arcsoft.supervisor.model.domain.channel;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

/**
 * A entity class for channel record to holds the history info.
 *
 * @author zw.
 */
@Entity
@DynamicUpdate
@Table(name = "channel_record_history")
public class ChannelRecordHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @Column(name = "start_time")
    private Date startTime;

    @Column(name = "end_time")
    private Date endTime;

    @Column(name = "path")
    private String recordBasePath;

    public ChannelRecordHistory() {}

    public ChannelRecordHistory(Channel channel, String recordBasePath) {
        this.channel = channel;
        this.recordBasePath = recordBasePath;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Date getStartTime() {
        return startTime;
    }

    @PrePersist
    public void prePersist(){
        this.startTime = new Date();
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getRecordBasePath() {
        return recordBasePath;
    }

    public void setRecordBasePath(String recordBasePath) {
        this.recordBasePath = recordBasePath;
    }

    public void end(){
        this.endTime = new Date();
    }
}
