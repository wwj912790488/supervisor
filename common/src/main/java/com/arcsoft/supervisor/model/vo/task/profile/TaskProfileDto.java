package com.arcsoft.supervisor.model.vo.task.profile;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

/**
 * Dto class of <code>TaskProfileDto</code> to holds and converts data between {@code TaskProfile}.
 *
 * @author zw.
 */
public class TaskProfileDto {

    public static final String NODE_NAME_OUTPUTPROFILES = "taskoutputprofiles";

    public static final String NODE_NAME_TASKOUTPUTS = "taskoutputs";

    private Integer id;

    @JsonProperty("taskname")
    private String name;

    @JsonProperty("taskdescription")
    private String description;

    @JsonProperty("taskencodingoption")
    private EncodingOption encodingOption;

    public enum EncodingOption{
        BestQuality, GoodQuality, Balance, Fast, Fastest, Custom
    }

    @JsonProperty("taskpriority")
    private Priority priority;

    public enum Priority{
        P1(1), P2(2), P3(3), P4(4), P5(5), P6(6), P7(7), P8(8), P9(9), P10(10);

        final int value;

        Priority(int value) {
            this.value = value;
        }

        @JsonValue
        public int getValue() {
            return value;
        }

        @JsonCreator
        public static Priority fromValue(int value) {
            for (Priority priority : values()) {
                if (priority.getValue() == value) {
                    return priority;
                }
            }
            return null;
        }
    }

    /**
     * How many gpu cores will be used
     */
    @JsonProperty("taskgpucores")
    private Integer usedGpuCoreAmount;

    @JsonProperty("taskinputrow")
    @Deprecated
    private Integer screenRow;

    @JsonProperty("taskinputcolumn")
    @Deprecated
    private Integer screenColumn;

    private Boolean allowProgramIdChange;

    @JsonProperty(NODE_NAME_OUTPUTPROFILES)
    @JsonSerialize(contentUsing = TaskProfileDtoOutputProfilesSerializer.class)
    private List<OutputProfileDto> outputProfiles;

    @JsonProperty(NODE_NAME_TASKOUTPUTS)
    private List<TaskOutput> outputs;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EncodingOption getEncodingOption() {
        return encodingOption;
    }

    public void setEncodingOption(EncodingOption encodingOption) {
        this.encodingOption = encodingOption;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
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
        return allowProgramIdChange == null ? false : allowProgramIdChange;
    }

    public void setAllowProgramIdChange(Boolean allowProgramIdChange) {
        this.allowProgramIdChange = allowProgramIdChange;
    }

    public List<OutputProfileDto> getOutputProfiles() {
        return outputProfiles;
    }

    public void setOutputProfiles(List<OutputProfileDto> outputProfiles) {
        this.outputProfiles = outputProfiles;
    }

    public List<TaskOutput> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<TaskOutput> outputs) {
        this.outputs = outputs;
    }

}
