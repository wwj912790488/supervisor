package com.arcsoft.supervisor.model.domain.server;

/**
 * Defines the type of server.
 *
 * @author zw.
 */
public enum ServerType {

    COMMANDER(1),
    AGENT(2),
    OPS(3),
    RTSP(4);

    private final int value;

    private ServerType(int type) {
        this.value = type;
    }

    public int getValue() {
        return value;
    }

    public static ServerType getTypeEnum(int value){
        for (ServerType serverType : values()){
            if (value == serverType.getValue()){
                return serverType;
            }
        }
        return null;
    }
}
