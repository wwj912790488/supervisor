package com.arcsoft.supervisor.service.task.impl;


import com.arcsoft.supervisor.cluster.action.ActionErrorCode;
import com.arcsoft.supervisor.cluster.action.ActionHandler;
import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.task.AlertRequest;
import com.arcsoft.supervisor.cluster.action.task.AlertResponse;
import com.arcsoft.supervisor.cluster.action.task.ContentDetectResultRequest;
import com.arcsoft.supervisor.cluster.action.task.ScreenWarningBorderRequest;
import com.arcsoft.supervisor.cluster.app.ActionException;
import com.arcsoft.supervisor.cluster.app.Request;
import com.arcsoft.supervisor.cluster.app.Response;
import com.arcsoft.supervisor.model.domain.channel.Channel;
import com.arcsoft.supervisor.model.domain.graphic.ScreenPosition;
import com.arcsoft.supervisor.model.domain.server.Server;
import com.arcsoft.supervisor.model.domain.task.Task;
import com.arcsoft.supervisor.model.vo.task.TaskAlertContent;
import com.arcsoft.supervisor.model.vo.task.TaskStatus;
import com.arcsoft.supervisor.repository.graphic.ScreenPositionJPARepo;
import com.arcsoft.supervisor.service.ServiceSupport;
import com.arcsoft.supervisor.service.audit.AuditLogger;
import com.arcsoft.supervisor.service.audit.TaskAlertAuditContent;
import com.arcsoft.supervisor.service.remote.RemoteExecutorService;
import com.arcsoft.supervisor.service.server.ServerService;
import com.arcsoft.supervisor.service.task.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * A implementation to handle the alert message of task.
 *
 * @author zw.
 */
@Service
public class TaskAlertHandler extends ServiceSupport implements ActionHandler {

    @Autowired
    private AuditLogger<TaskAlertAuditContent> taskAlertAuditLogger;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ScreenPositionJPARepo screenPositionJPARepo;

    @Autowired
    private ServerService serverService;

    @Autowired
    private RemoteExecutorService remoteExecutorService;

    private static final String[] IGNORE_ERRORCODE_HEX_PREFIX = {
            "21"
    };

    public boolean doFilter(int level, int code, String msg) {
        String hexCode = String.format("%x", code);
        for (String prefix : IGNORE_ERRORCODE_HEX_PREFIX) {
            if (hexCode.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int[] getActions() {
        return new int[]{
                Actions.TASK_ALERT
        };
    }

    @Transactional
    @Override
    public Response execute(Request request) throws ActionException {
        return handleRequest((AlertRequest) request);
    }

    private AlertResponse handleRequest(AlertRequest request) {
        AlertResponse response = new AlertResponse();
        response.setErrorCode(ActionErrorCode.SUCCESS);
        TaskAlertAuditContent alertContent = TaskAlertAuditContent.from(request.getAlertContent());
        try {
            if (isFilteredAlertMessage(alertContent.getLevel().getValue(), alertContent.getErrorCode(), alertContent.getDescription())) {
                return response;
            } else {
                taskAlertAuditLogger.log(TaskAlertAuditContent.from(request.getAlertContent()));
            }
        } catch (Exception e) {
            logger.error("Failed to handle task alert " + request.getAlertContent(), e);
        }

        return response;
    }


    private boolean isFilteredAlertMessage(int level, int code, String msg) {

        if (doFilter(level, code, msg)) {
            return true;
        }
        return false;
    }


    private void returnScreenWarning(ContentDetectResultRequest request, Channel channel) {
        Task task = taskService.getById(request.getResult().getTaskid());
        if (task != null && task.isStatusEqual(TaskStatus.RUNNING)) {
            Server server=serverService.getServer(task.getServerId());
            if (server != null) {
                List<ScreenPosition> positionList = screenPositionJPARepo.findByChannel(channel);
                for (ScreenPosition screenPosition : positionList) {
                    Integer columnCount = screenPosition.getScreenSchema().getColumnCount();
                    Integer column = screenPosition.getColumn();
                    Integer row = screenPosition.getRow();
                    Integer localtion = columnCount * row + column;

                    ScreenWarningBorderRequest screenWarningBorderRequest=new ScreenWarningBorderRequest();
                    screenWarningBorderRequest.setTaskId(request.getResult().getTaskid());
                    screenWarningBorderRequest.setShow(false);
                    screenWarningBorderRequest.setIndex(localtion);
                    remoteExecutorService.remoteExecute(screenWarningBorderRequest, server);
                }

            }
        }

    }

}
