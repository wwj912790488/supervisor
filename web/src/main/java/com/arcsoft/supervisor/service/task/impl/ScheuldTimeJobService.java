package com.arcsoft.supervisor.service.task.impl;

import com.arcsoft.supervisor.cluster.action.task.ContentDetectResultRequest;
import com.arcsoft.supervisor.cluster.action.task.ScreenWarningBorderRequest;
import com.arcsoft.supervisor.model.domain.channel.Channel;
import com.arcsoft.supervisor.model.domain.graphic.ScreenPosition;
import com.arcsoft.supervisor.model.domain.log.ContentDetectLog;
import com.arcsoft.supervisor.model.domain.server.Server;
import com.arcsoft.supervisor.model.domain.task.Task;
import com.arcsoft.supervisor.model.vo.task.TaskStatus;
import com.arcsoft.supervisor.repository.log.ContentDetectLogRepository;
import com.arcsoft.supervisor.service.ServiceSupport;
import com.arcsoft.supervisor.service.log.event.ContentDetectLogEvent;
import com.arcsoft.supervisor.service.remote.RemoteExecutorService;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Created by wwj on 2017/5/10.
 */
@Service
public class ScheuldTimeJobService extends ServiceSupport implements Job {


    @Override
    @Transactional
    public void execute(JobExecutionContext context) throws JobExecutionException {
        ContentDetectLog detectLog = (ContentDetectLog) context.getMergedJobDataMap().get("detectLog");
        ScheuldTimeJobBean scheuldTimeJobBean = (ScheuldTimeJobBean) context.getMergedJobDataMap().get("scheuldTimeJobBean");
        ContentDetectLog persistDetectLog = scheuldTimeJobBean.getContentDetectLogRepository().findByGuid(detectLog.getGuid());
        if (persistDetectLog == null) {
            List<ContentDetectLog> duplicatedLogs = scheuldTimeJobBean.getContentDetectLogRepository().
                    findByChannelIdAndTypeAndStartTimeLessThanAndStartTimeGreaterThanAndGuidNot(detectLog.getChannelId(), detectLog.getType(), detectLog.getStartTime() + 10, detectLog.getStartTime() - 10, detectLog.getGuid());
            if (duplicatedLogs.isEmpty()) {
                scheuldTimeJobBean.getContentDetectLogRepository().save(detectLog);
            }
            if(detectLog.getEndTimeAsDate()==null){
                //告警恢复
                returnScreenStartWarning(scheuldTimeJobBean);
            }else {
                returnScreenEndWarning(scheuldTimeJobBean);
            }
        }


        try {

            context.getScheduler().shutdown();

        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public void addScheuldTimeJob(ScheuldTimeJobBean scheuldTimeJobBean) {
        SchedulerFactory schedulerfactory = new StdSchedulerFactory();
        Scheduler scheduler = null;
        String[] str = scheuldTimeJobBean.getEndTime().split("\\:");
        String cronExpression = str[2] + " " + str[1] + " " + str[0] + " * * ?";
        try {
            scheduler = schedulerfactory.getScheduler();
            ContentDetectLog detectLog = scheuldTimeJobBean.getDetectLog();
            JobDetail jobDetail = newJob(ScheuldTimeJobService.class).withIdentity(detectLog.getGuid(), scheuldTimeJobBean.getChannelName()).build();
            detectLog.setChannelName(scheuldTimeJobBean.getChannelName());
            jobDetail.getJobDataMap().put("detectLog", detectLog);
            jobDetail.getJobDataMap().put("scheuldTimeJobBean", scheuldTimeJobBean);

            CronTrigger trigger = newTrigger().withIdentity(detectLog.getGuid(), scheuldTimeJobBean.getChannelName()).withSchedule(cronSchedule(cronExpression)).build();
            scheduler.scheduleJob(jobDetail, trigger);
            logger.debug(jobDetail.getKey() + " 已被安排执行于: " + "，并且以如下重复规则重复执行: " + trigger.getCronExpression());
            scheduler.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void returnScreenStartWarning(ScheuldTimeJobBean scheuldTimeJobBean) {
        Task task = scheuldTimeJobBean.getTaskService().getById(scheuldTimeJobBean.getDetectLog().getTaskId());
        if (task != null && task.isStatusEqual(TaskStatus.RUNNING)) {
            Server server = scheuldTimeJobBean.getServerService().getServer(task.getServerId());
            if (server != null) {
                List<ScreenWarningBorderRequest> requestList = scheuldTimeJobBean.getRequestList();
                for (ScreenWarningBorderRequest request : requestList) {
                    request.setShow(true);
                    scheuldTimeJobBean.getRemoteExecutorService().remoteExecute(request, server);
                }

            }
        }
    }

    private void returnScreenEndWarning(ScheuldTimeJobBean scheuldTimeJobBean) {
        Task task = scheuldTimeJobBean.getTaskService().getById(scheuldTimeJobBean.getDetectLog().getTaskId());
        if (task != null && task.isStatusEqual(TaskStatus.RUNNING)) {
            Server server = scheuldTimeJobBean.getServerService().getServer(task.getServerId());
            if (server != null) {
                List<ScreenWarningBorderRequest> requestList = scheuldTimeJobBean.getRequestList();
                for (ScreenWarningBorderRequest request : requestList) {
                    request.setShow(false);
                    scheuldTimeJobBean.getRemoteExecutorService().remoteExecute(request, server);
                }

            }
        }
    }





    /*public static void main(String[] args) {
        ContentDetectLogRepository contentDetectLogRepository = null;
        ContentDetectLog detectLog = new ContentDetectLog();
        detectLog.setTaskId(18);
        detectLog.setStartTime(1494386129219L);
        detectLog.setEndTime(1494386299016L);
        detectLog.setChannelName("JLTV");
        detectLog.setGuid("E2C4C77B-B9AD-40B7-ACF9-BC9642902E00-315478200");

        ScheuldTimeJobService service = new ScheuldTimeJobService();
        //service.addScheuldTimeJob("15:24:15", detectLog, "JLTV", contentDetectLogRepository);
    }*/

}
