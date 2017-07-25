package com.arcsoft.supervisor.model.vo.task;

/**
 * Defines types of task.
 *
 * @author zw.
 */
public enum TaskType {

    CONTENT_DETECT(1),
    IP_STREAM_COMPOSE(2),
    RTSP(3),
    SDI_STREAM_COMPOSE(4),
    USER_RELATED_COMPOSE(5);

    private final int type;

    TaskType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public static TaskType getTypeByValue(int type){
        for (TaskType taskType : values()){
            if (taskType.getType() == type){
                return taskType;
            }
        }
        return null;
    }

    public boolean isComposeType(){
        switch (this){
            case IP_STREAM_COMPOSE:
            case SDI_STREAM_COMPOSE:
            case USER_RELATED_COMPOSE:
                return true;
            default:
                return false;
        }
    }

}
