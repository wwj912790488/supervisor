package com.arcsoft.supervisor.model.domain.task;

import com.arcsoft.supervisor.model.domain.task.TaskPort.PortType;
import com.arcsoft.supervisor.model.vo.task.TaskStatus;
import com.arcsoft.supervisor.model.vo.task.TaskType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Entity class for Task.
 *
 * @author zw.
 */
@Entity
@Table(name = "task")
@DynamicUpdate
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String status;

    private Integer type;

    private Integer pid;

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    @Column(name = "ref_id")
    private Integer referenceId;

    @Column(name = "server_id")
    private String serverId;

    @Column(name = "gpu_index",nullable = false,columnDefinition = "int default -1")
    private Integer gpudIndex;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private TaskProfile profile;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "task" )
    private List<TaskPort> taskPorts;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "task")
    private List<TaskInputGpuUsage> taskInputGpuUsages;

    public Task() {
    }

    public Task(Integer type, Integer referenceId) {
        this.type = type;
        this.referenceId = referenceId;
    }

    public Task(Integer type, Integer referenceId, TaskProfile profile) {
        this.referenceId = referenceId;
        this.type = type;
        this.profile = profile;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Integer referenceId) {
        this.referenceId = referenceId;
    }

    public void setTypeWithEnum(TaskType taskType){
        this.type = taskType.getType();
    }

    public void setStatusWithEnum(TaskStatus taskStatus){
        this.status = taskStatus.toString();
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public Integer getGpudIndex() {
        return gpudIndex;
    }

    public void setGpudIndex(Integer gpudIndex) {
        this.gpudIndex = gpudIndex;
    }

    public TaskType getTypeAsEnum(){
        return TaskType.getTypeByValue(this.type);
    }

    public boolean isStatusEqual(TaskStatus taskStatus){
        return StringUtils.isNotBlank(this.status) && TaskStatus.valueOf(this.status) == taskStatus;
    }

    public boolean isStopped(){
        return StringUtils.isBlank(this.status) || isStatusEqual(TaskStatus.STOP) || isStatusEqual(TaskStatus.ERROR);
    }

    public List<TaskPort> getTaskPorts() {
        return taskPorts;
    }

    public void addTaskPort(List<TaskPort> taskPorts) {
        if (this.taskPorts == null) {
            this.taskPorts = new ArrayList<>();
        }

        for (TaskPort port : taskPorts) {
            port.setTask(this);
            this.taskPorts.add(port);
        }
    }

    public void addTaskPort(TaskPort taskPort) {
        addTaskPort(Arrays.asList(taskPort));
    }

    public void setTaskPorts(List<TaskPort> taskPorts) {
        this.taskPorts = taskPorts;
    }

    @JsonIgnore
    public TaskPort getTaskPortByType(PortType portType) {
        for (TaskPort port : this.taskPorts) {
            if (port.getType() == portType) {
                return port;
            }
        }
        return null;
    }

    public TaskProfile getProfile() {
        return profile;
    }

    public void setProfile(TaskProfile profile) {
        this.profile = profile;
    }

    public List<TaskInputGpuUsage> getTaskInputGpuUsages() {
        return taskInputGpuUsages;
    }

    public void setTaskInputGpuUsages(List<TaskInputGpuUsage> taskInputGpuUsages) {
        this.taskInputGpuUsages = taskInputGpuUsages;
    }
}
