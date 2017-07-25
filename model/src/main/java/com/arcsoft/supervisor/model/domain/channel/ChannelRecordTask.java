package com.arcsoft.supervisor.model.domain.channel;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name = "channel_record_task")
@DynamicUpdate
public class ChannelRecordTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer supervisorChannelId;

    private Integer recordChannelId;

    private Integer recordTaskId;

    public ChannelRecordTask() {}

    public ChannelRecordTask(Integer supervisorChannelId, Integer recordChannelId, Integer recordTaskId) {
        this.supervisorChannelId = supervisorChannelId;
        this.recordChannelId = recordChannelId;
        this.recordTaskId = recordTaskId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSupervisorChannelId() {
        return supervisorChannelId;
    }

    public void setSupervisorChannelId(Integer supervisorChannelId) {
        this.supervisorChannelId = supervisorChannelId;
    }

    public Integer getRecordChannelId() {
        return recordChannelId;
    }

    public void setRecordChannelId(Integer recordChannelId) {
        this.recordChannelId = recordChannelId;
    }

    public Integer getRecordTaskId() {
        return recordTaskId;
    }

    public void setRecordTaskId(Integer recordTaskId) {
        this.recordTaskId = recordTaskId;
    }
}
