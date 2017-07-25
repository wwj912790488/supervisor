package com.arcsoft.supervisor.model.vo.task.profile;

import com.fasterxml.jackson.annotation.*;

/**
 * Base class of output of task for defines generic information.
 *
 * @author zw.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "uniqueType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TsOverUdpOutput.class, name = "UdpStreaming-UDPOverTS"),
        @JsonSubTypes.Type(value = RtmpOutput.class, name = "FlashStreaming-RTMP")
})
public abstract class TaskOutput {

    @JsonProperty("linkedprofile")
    private Integer linkedProfile;

    @JsonProperty("outputtype")
    private Type type;

    public enum Type {
        UdpStreaming,
        FlashStreaming
    }

    @JsonProperty("outputcontainer")
    private Container container;

    public enum Container {
        UDPOverTS,
        RTMP
    }

    @JsonProperty("outputDest")
    private TargetType targetType;

    public enum TargetType {
        SCREEN(0), MOBILE(1);

        final int type;

        TargetType(int type) {
            this.type = type;
        }

        @JsonValue
        public int getType() {
            return type;
        }

        @JsonCreator
        public static TargetType fromType(int type) {
            for (TargetType targetType : values()) {
                if (targetType.getType() == type) {
                    return targetType;
                }
            }
            return null;
        }
    }

    public TargetType getTargetType() {
        return targetType;
    }

    @JsonIgnore
    public boolean isMobileType() {
        return TargetType.MOBILE == targetType;
    }

    public void setTargetType(TargetType targetType) {
        this.targetType = targetType;
    }

    public Integer getLinkedProfile() {
        return linkedProfile;
    }

    public void setLinkedProfile(Integer linkedProfile) {
        this.linkedProfile = linkedProfile;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }
    
    @JsonIgnore
    public boolean isScreenWithRTMP() {
        return (TargetType.SCREEN == targetType &&  Container.RTMP == container);
    }
}
