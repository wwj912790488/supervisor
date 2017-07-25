package com.arcsoft.supervisor.service.audit;


import com.arcsoft.supervisor.commons.SupervisorDefs;
import com.arcsoft.supervisor.model.domain.log.ServiceLog;
import com.arcsoft.supervisor.model.vo.task.TaskAlertContent;

import java.util.Date;

/**
 * A task alert content object to holds the alert information of task.
 *
 * @author zw.
 */
public class TaskAlertAuditContent extends BaseAuditContent {

    /**
     * The integer value of error or warn code.
     */
    private final int errorCode;

    /**
     * The hexadecimal of errorCode.
     */
    private final String hexErrorCode;

    private final int taskId;

    private String channelName;

    private final String ip;

    public TaskAlertAuditContent(String description,
                                 AuditLevel level,
                                 int errorCode,
                                 int taskId,
                                 String ip) {
        this(null, description, level, errorCode, taskId, null, ip);
    }

    public TaskAlertAuditContent(SupervisorDefs.Modules module,
                                 String description,
                                 AuditLevel level,
                                 int errorCode,
                                 int taskId,
                                 String channelName,
                                 String ip) {
        super(new Date(), module, description, level);
        this.errorCode = errorCode;
        this.hexErrorCode = String.format("0x%08x", errorCode);
        this.taskId = taskId;
        this.channelName = channelName;
        this.ip = ip;
    }

    /**
     * Retrieves the code.
     *
     * @return the code integer value
     */
    public int getErrorCode() {
        return errorCode;
    }

    public int getTaskId() {
        return taskId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getHexErrorCode() {
        return hexErrorCode;
    }

    public String getIp() {
        return ip;
    }

    public static TaskAlertAuditContent from(TaskAlertContent alertContent) {
        return new TaskAlertAuditContent(
                alertContent.getMessage(),
                alertContent.getLevel() == 0 ? AuditLevel.WARN : AuditLevel.ERROR,
                alertContent.getCode(),
                alertContent.getTaskId(),
                alertContent.getIp()
        );
    }

    public ServiceLog toServiceLog(String description) {
        return new ServiceLog((byte) getLevel().getValue(), getModule().getModule(), description, getIp());
    }


    @Override
    public String toString() {
        return toStringHelper().add("errorCode", hexErrorCode)
                .add("taskId", taskId)
                .add("ip", ip).toString();
    }


}
