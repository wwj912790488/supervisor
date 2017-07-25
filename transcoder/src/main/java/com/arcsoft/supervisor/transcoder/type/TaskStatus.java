package com.arcsoft.supervisor.transcoder.type;


public enum TaskStatus {
    INCREATING(0),	/*before task can be used, no use now, only used to occupy enum value 0*/
    PENDING(1), 	/*ready to run*/
    WAITING(2), 	/*waiting to run*/
    RUNNING(3),
    COMPLETED(4),
    ERROR(5),
    CANCELLED(6),
    SUSPENDED(7),
    STOPPING(8),
    DOWNLOADING(9),
    UPLOADING(10),
    READY(11);

    /**
     * @return
     */
    public String getKey() {
        return this.name();
    }

    public byte getId() {
        return id;
    }

    private TaskStatus(int id) {
        this.id = (byte) id;
    }

    private byte id;
}
