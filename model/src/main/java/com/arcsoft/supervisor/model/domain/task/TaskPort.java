package com.arcsoft.supervisor.model.domain.task;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 * A port class for {@link Task} which holds the port number.
 *
 * @author zw.
 */
@Entity
@Table(name = "task_port")
public class TaskPort {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Enum for port type.
     */
    public enum PortType{
        // Below types is for compose task
        MOBILE, SCREEN,
        // Below types is for channel task
        SD, HD
    }

    @Enumerated(EnumType.STRING)
    private PortType type;

    @Column(name = "port_number", nullable = false)
    private Integer portNumber;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;


    public TaskPort() {
    }

    public TaskPort(PortType type, Integer portNumber) {
        this.type = type;
        this.portNumber = portNumber;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public PortType getType() {
        return type;
    }

    public void setType(PortType type) {
        this.type = type;
    }

    public Integer getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(Integer portNumber) {
        this.portNumber = portNumber;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

}
