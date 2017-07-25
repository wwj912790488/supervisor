package com.arcsoft.supervisor.service.task.impl;

import com.arcsoft.supervisor.cluster.action.ActionErrorCode;
import com.arcsoft.supervisor.cluster.action.ActionHandler;
import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.task.ContentDetectResultRequest;
import com.arcsoft.supervisor.cluster.action.task.ContentDetectResultResponse;
import com.arcsoft.supervisor.cluster.action.task.ScreenWarningBorderRequest;
import com.arcsoft.supervisor.cluster.app.ActionException;
import com.arcsoft.supervisor.cluster.app.Request;
import com.arcsoft.supervisor.cluster.app.Response;
import com.arcsoft.supervisor.model.domain.channel.Channel;
import com.arcsoft.supervisor.model.domain.channel.ChannelAlarmTime;
import com.arcsoft.supervisor.model.domain.channel.ChannelInfo;
import com.arcsoft.supervisor.model.domain.graphic.Screen;
import com.arcsoft.supervisor.model.domain.graphic.ScreenPosition;
import com.arcsoft.supervisor.model.domain.log.ContentDetectLog;
import com.arcsoft.supervisor.model.domain.server.OpsServer;
import com.arcsoft.supervisor.model.domain.server.Server;
import com.arcsoft.supervisor.model.domain.task.Task;
import com.arcsoft.supervisor.model.vo.task.TaskStatus;
import com.arcsoft.supervisor.model.vo.task.cd.ContentDetectResult;
import com.arcsoft.supervisor.repository.channel.ChannelRepository;
import com.arcsoft.supervisor.repository.graphic.ScreenPositionJPARepo;
import com.arcsoft.supervisor.repository.log.ContentDetectLogRepository;
import com.arcsoft.supervisor.service.ServiceSupport;
import com.arcsoft.supervisor.service.channel.ChannelService;
import com.arcsoft.supervisor.service.log.event.ContentDetectLogEvent;
import com.arcsoft.supervisor.service.remote.RemoteExecutorService;
import com.arcsoft.supervisor.service.server.ServerService;
import com.arcsoft.supervisor.service.task.TaskService;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.calendar.CronCalendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Handling the reported content detect result.
 * <p>The handler will receive the result by remote server report.</p>
 *
 * @author zw.
 */
@Service
public class ContentDetectResultHandler extends ServiceSupport implements ActionHandler {

    @Autowired
    private ContentDetectLogRepository contentDetectLogRepository;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private ServerService serverService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RemoteExecutorService remoteExecutorService;

    @Autowired
    private ScreenPositionJPARepo screenPositionJPARepo;

    @Autowired
    private  ScheuldTimeJobService scheuldTimeJobService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Override
    public int[] getActions() {
        return new int[]{
                Actions.TASK_REPORT_CONTENT_DETECT_RESULT
        };
    }

    @Transactional
    @Override
    public Response execute(Request request) throws ActionException {
        return handleResult((ContentDetectResultRequest) request);
    }

    private Response handleResult(ContentDetectResultRequest request) {
        ContentDetectResultResponse resultResponse = new ContentDetectResultResponse();
        resultResponse.setErrorCode(ActionErrorCode.SUCCESS);

        ContentDetectResult result = request.getResult();
        try {
            final  ContentDetectLog detectLog = new ContentDetectLog();
            copy(result, detectLog);
            List<ContentDetectLog> duplicatedLogs = contentDetectLogRepository.
                    findByChannelIdAndTypeAndStartTimeLessThanAndStartTimeGreaterThanAndGuidNot(detectLog.getChannelId(), detectLog.getType(), detectLog.getStartTime() + 1000, detectLog.getStartTime() - 1000, detectLog.getGuid());
            if (!duplicatedLogs.isEmpty()) {
                //ignore duplicated logs
                logger.debug("duplicated log:" + detectLog.getChannelId() + "," + detectLog.getType() + "," + detectLog.getStartTimeAsDate() + "," + detectLog.getEndTimeAsDate());
                return resultResponse;
            }
            Channel channel= (Channel) transactionTemplate.execute(new TransactionCallback<Object>() {
                @Override
                public Object doInTransaction(TransactionStatus status) {
                    return   channelService.getById(detectLog.getChannelId());
                }
            });
            ChannelAlarmTime channelAlarmTime= (ChannelAlarmTime) transactionTemplate.execute(new TransactionCallback<Object>() {
                @Override
                public Object doInTransaction(TransactionStatus status) {
                    Channel channel = channelService.getById(detectLog.getChannelId());
                    return  channel.getChannelAlarmTime();
                }
            });

            try {
                SimpleDateFormat sp = new SimpleDateFormat("HH:mm:ss");
                long detectLongStartTime = sp.parse(sp.format(detectLog.getStartTimeAsDate())).getTime();
                if(detectLog.getEndTimeAsDate()==null){
                    //开始告警，
                    //告警开始时间在不告警时间段内
                    if(channelAlarmTime.isEnableTime1()){
                        if (channelAlarmTime != null  && detectLongStartTime >= sp.parse(channelAlarmTime.getAlarmStartTime1()).getTime() && detectLongStartTime < sp.parse(channelAlarmTime.getAlarmEndTime1()).getTime()) {
                            List<ScreenWarningBorderRequest> screenWarningBorderRequest=returnScreenWarning(request,channel);
                            ScheuldTimeJobBean scheuldTimeJobBean=new ScheuldTimeJobBean(true,channelAlarmTime.getAlarmEndTime1(),detectLog,channel.getName(),contentDetectLogRepository,remoteExecutorService,taskService,serverService,screenPositionJPARepo,channel,screenWarningBorderRequest);
                            scheuldTimeJobService.addScheuldTimeJob(scheuldTimeJobBean);
                            return resultResponse;
                        }
                    }
                    if( channelAlarmTime.isEnableTime2()){
                        if (channelAlarmTime != null   && detectLongStartTime >= sp.parse(channelAlarmTime.getAlarmStartTime2()).getTime() && detectLongStartTime < sp.parse(channelAlarmTime.getAlarmEndTime2()).getTime()) {
                            List<ScreenWarningBorderRequest> screenWarningBorderRequest=returnScreenWarning(request,channel);
                            ScheuldTimeJobBean scheuldTimeJobBean=new ScheuldTimeJobBean(true,channelAlarmTime.getAlarmEndTime2(),detectLog,channel.getName(),contentDetectLogRepository,remoteExecutorService,taskService,serverService,screenPositionJPARepo,channel,screenWarningBorderRequest);
                            scheuldTimeJobService.addScheuldTimeJob(scheuldTimeJobBean);
                            return resultResponse;
                        }
                    }
                    if(channelAlarmTime.isEnableTime3()){
                        if (channelAlarmTime != null   && detectLongStartTime >= sp.parse(channelAlarmTime.getAlarmStartTime3()).getTime() && detectLongStartTime < sp.parse(channelAlarmTime.getAlarmEndTime3()).getTime()) {
                            List<ScreenWarningBorderRequest> screenWarningBorderRequest=returnScreenWarning(request,channel);
                            ScheuldTimeJobBean scheuldTimeJobBean=new ScheuldTimeJobBean(true,channelAlarmTime.getAlarmEndTime3(),detectLog,channel.getName(),contentDetectLogRepository,remoteExecutorService,taskService,serverService,screenPositionJPARepo,channel,screenWarningBorderRequest);
                            scheuldTimeJobService.addScheuldTimeJob(scheuldTimeJobBean);
                            return resultResponse;
                        }
                    }
                    if(channelAlarmTime.isEnableTime4()){
                        if (channelAlarmTime != null  && detectLongStartTime >= sp.parse(channelAlarmTime.getAlarmStartTime4()).getTime() && detectLongStartTime < sp.parse(channelAlarmTime.getAlarmEndTime4()).getTime()) {
                            List<ScreenWarningBorderRequest> screenWarningBorderRequest=returnScreenWarning(request,channel);
                            ScheuldTimeJobBean scheuldTimeJobBean=new ScheuldTimeJobBean(true,channelAlarmTime.getAlarmEndTime4(),detectLog,channel.getName(),contentDetectLogRepository,remoteExecutorService,taskService,serverService,screenPositionJPARepo,channel,screenWarningBorderRequest);
                            scheuldTimeJobService.addScheuldTimeJob(scheuldTimeJobBean);
                            return resultResponse;
                        }
                    }
                    if(channelAlarmTime.isEnableTime5()){
                        if (channelAlarmTime != null  && detectLongStartTime >= sp.parse(channelAlarmTime.getAlarmStartTime5()).getTime() && detectLongStartTime < sp.parse(channelAlarmTime.getAlarmEndTime5()).getTime()) {
                            List<ScreenWarningBorderRequest> screenWarningBorderRequest=returnScreenWarning(request,channel);
                            ScheuldTimeJobBean scheuldTimeJobBean=new ScheuldTimeJobBean(true,channelAlarmTime.getAlarmEndTime5(),detectLog,channel.getName(),contentDetectLogRepository,remoteExecutorService,taskService,serverService,screenPositionJPARepo,channel,screenWarningBorderRequest);
                            scheuldTimeJobService.addScheuldTimeJob(scheuldTimeJobBean);
                            return resultResponse;
                        }
                    }


                   /* //告警开始时间早于告警时间段内,没有结束时间（等到最近的时间不告警）
                    if (channelAlarmTime != null ){

                    }*/

                }/*else{
                    //结束告警
                    //告警开始时间在不告警时间段内,
                    if (channelAlarmTime != null  && channelAlarmTime.isEnableTime1() && detectLongEndTime >= sp.parse(channelAlarmTime.getAlarmStartTime1()).getTime() && detectLongEndTime < sp.parse(channelAlarmTime.getAlarmEndTime1()).getTime()) {
                        List<ScreenWarningBorderRequest> screenWarningBorderRequest=returnScreenWarning(request,channel);
                        ScheuldTimeJobBean scheuldTimeJobBean=new ScheuldTimeJobBean(false,channelAlarmTime.getAlarmEndTime1(),detectLog,channel.getName(),contentDetectLogRepository,remoteExecutorService,taskService,serverService,screenPositionJPARepo,channel,screenWarningBorderRequest);
                        //scheuldTimeJobService.addScheuldTimeJob(scheuldTimeJobBean);
                        return resultResponse;
                    }

                    //告警开始时间早于告警时间段内（如果有结束时间【1.在不告警时间段内结束 2.在不告警时间段外结束】）



                }*/

            } catch (Exception e) {
                e.printStackTrace();
            }

            ContentDetectLog persistDetectLog = contentDetectLogRepository.findByGuid(detectLog.getGuid());
            if (persistDetectLog == null) {
                if (channel != null) {
                    detectLog.setChannelName(channel.getName());
                    contentDetectLogRepository.save(detectLog);
                    getEventManager().submit(new ContentDetectLogEvent(detectLog, channel.getEnableTriggerRecord()));
                }

            } else {
                persistDetectLog.setEndTime(detectLog.getEndTime());
                //Below code in here is to support legacy content detect code.
                //Maybe delete in future.
                getEventManager().submit(new ContentDetectLogEvent(persistDetectLog, channel.getEnableTriggerRecord()));
            }
        } catch (Exception e) {
            logger.error("Failed to handle content detect result request.", e);
        }
        return resultResponse;
    }

        private List<ScreenWarningBorderRequest> returnScreenWarning(ContentDetectResultRequest request,Channel channel) {
            Task task = taskService.getById(request.getResult().getTaskid());
            if (task != null && task.isStatusEqual(TaskStatus.RUNNING)) {
                Server server=serverService.getServer(task.getServerId());
                if (server != null) {
                    List<ScreenWarningBorderRequest> list=new ArrayList<>();
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
                        list.add(screenWarningBorderRequest);

                    }
                    return list;
                }
            }
            return  null;

    }

    /**
     * Copys the <code>result</code> object to <code>detectLog</code> object.
     *
     * @param result    the <code>ContentDetectResult</code> object
     * @param detectLog the <code>ContentDetectLog</code> entity object
     */
    private void copy(ContentDetectResult result, ContentDetectLog detectLog) {
        detectLog.setTaskId(result.getTaskid());
        detectLog.setEndTime(result.getEndTime());
        detectLog.setStartTime(result.getStartTime());
        detectLog.setSoundTrack(result.isAudio() ? result.getValue2() : null);
        detectLog.setType(result.getCheckType());
        detectLog.setGuid(result.getGuid());
        detectLog.setChannelId(result.getChannelId());
    }

}
