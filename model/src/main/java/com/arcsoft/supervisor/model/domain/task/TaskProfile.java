package com.arcsoft.supervisor.model.domain.task;

import com.arcsoft.supervisor.model.vo.task.profile.TaskProfileDto;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity class for task profile.
 *
 * @author zw.
 */
@Entity
@Table(name = "profile_task")
@DiscriminatorValue("3")
public class TaskProfile extends Profile {

    @Enumerated(EnumType.STRING)
    private TaskProfileDto.EncodingOption encodingOption;

    @Enumerated(EnumType.STRING)
    private TaskProfileDto.Priority priority;

    /**
     * How many gpu cores will be used
     */
    @Column(name = "used_gpu_core_amount")
    private Integer usedGpuCoreAmount;

    @Deprecated
    private Integer screenRow;

    @Deprecated
    private Integer screenColumn;

    private Boolean allowProgramIdChange;

    @Column(name = "amount_output")
    private Integer amountOfOutput;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "profile")
    private List<Task> tasks = new ArrayList<>();

    @PreRemove
    private void onBeforeRemove() {
        for (Task task : tasks) {
            task.setProfile(null);
        }
    }

    public TaskProfileDto.EncodingOption getEncodingOption() {
        return encodingOption;
    }

    public void setEncodingOption(TaskProfileDto.EncodingOption encodingOption) {
        this.encodingOption = encodingOption;
    }

    public TaskProfileDto.Priority getPriority() {
        return priority;
    }

    public void setPriority(TaskProfileDto.Priority priority) {
        this.priority = priority;
    }

    public Integer getUsedGpuCoreAmount() {
        return usedGpuCoreAmount;
    }

    public void setUsedGpuCoreAmount(Integer usedGpuCoreAmount) {
        this.usedGpuCoreAmount = usedGpuCoreAmount;
    }

    public Integer getScreenRow() {
        return screenRow;
    }

    public void setScreenRow(Integer screenRow) {
        this.screenRow = screenRow;
    }

    public Integer getScreenColumn() {
        return screenColumn;
    }

    public void setScreenColumn(Integer screenColumn) {
        this.screenColumn = screenColumn;
    }

    public Boolean getAllowProgramIdChange() {
        return allowProgramIdChange;
    }

    public void setAllowProgramIdChange(Boolean allowProgramIdChange) {
        this.allowProgramIdChange = allowProgramIdChange;
    }

    public Integer getAmountOfOutput() {
        return amountOfOutput;
    }

    public void setAmountOfOutput(Integer amountOfOutput) {
        this.amountOfOutput = amountOfOutput;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public static class Builder extends ProfileBuilder<TaskProfile, Builder> {

        public Builder() {
            super(new TaskProfile());
        }

        public Builder encodingOption(TaskProfileDto.EncodingOption encodingOption) {
            profile.setEncodingOption(encodingOption);
            return getBuilder();
        }

        public Builder priority(TaskProfileDto.Priority priority) {
            profile.setPriority(priority);
            return getBuilder();
        }

        public Builder usedGpuCoreAmount(int usedGpuCoreAmount) {
            profile.setUsedGpuCoreAmount(usedGpuCoreAmount);
            return getBuilder();
        }

        public Builder screenRow(int screenRow) {
            profile.setScreenRow(screenRow);
            return getBuilder();
        }

        public Builder screenColumn(int screenColumn) {
            profile.setScreenColumn(screenColumn);
            return getBuilder();
        }

        public Builder amountOfOutput(int amountOfOutput) {
            profile.setAmountOfOutput(amountOfOutput);
            return getBuilder();
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }
    }

}
