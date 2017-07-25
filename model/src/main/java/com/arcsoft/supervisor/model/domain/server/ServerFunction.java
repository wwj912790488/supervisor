package com.arcsoft.supervisor.model.domain.server;

/**
 * Defines function value of server.
 *
 * @author zw.
 */
public enum ServerFunction {

    SIGNAL_DETECT("sd"),
    CONTENT_DETECT("cd"),
    IP_STREAM_COMPOSE("ip"),
    SDI_STREAM_COMPOSE("sdi"),
    ENCODER("encoder"),
    STREAM_SERVER("stream"),
    COMMANDER("comm");

    private final String type;

    private ServerFunction(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static ServerFunction getServerFunctionWithType(String type){
        for (ServerFunction function : values()){
            if (function.getType().equals(type)){
                return function;
            }
        }
        return null;
    }
}
