package com.arcsoft.supervisor.service.task;

import com.arcsoft.supervisor.model.vo.task.TaskStatus;
import org.joda.time.LocalDateTime;

import java.util.Date;

public class TaskKeeper {
    Date lastRunning;

    public boolean updateStatus(TaskStatus status) {
        switch (status) {
            case STOP:
                break;
            case RUNNING:
                lastRunning = new Date();
                break;
            case ERROR:
                LocalDateTime now = new LocalDateTime();
                if(now.isAfter(new LocalDateTime(this.lastRunning).plusSeconds(30))) {
                    return true;
                }
                break;
        }
        return false;
    }
}
