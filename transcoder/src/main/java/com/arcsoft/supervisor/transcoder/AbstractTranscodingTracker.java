package com.arcsoft.supervisor.transcoder;


import com.arcsoft.supervisor.transcoder.type.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * AbstractTranscodingTracker
 *
 * @author Bing
 */
public abstract class AbstractTranscodingTracker implements ITranscodingTracker {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractTranscodingTracker.class);

    /**
     * task event listener
     */
    protected IEventNotifyListener taskEventListener;
    /**
     * task id
     */
    protected TranscodingKey taskid;
    /**
     * task priority
     */
    protected int priority = 0;
    protected Object userData = null;
    /**
     * trans task exit code
     */
    protected int transtaskExitCode = 0;
    protected String exitErrorDesc = null;
    protected TranscodeStatistic transcodeStat = null;

    /**
     * task start time - cache as string, so as to higher performance output
     */
    protected String startAt = null;
    /**
     * task endAt
     */
    protected Date endAt = null;
    /**
     * is need restart task
     */
    protected boolean restartTask = false;
    /**
     * do output - default true
     */
    protected boolean isDoOutput = true;
    /**
     * transcoder started status: TRANSCODING_EXIT, TRANSCODING_STOPPING, 0, TRANSCODING_STARTING, TRANSCODING_STARTED
     */
    protected volatile int procTranscoderStatus = 0;


    protected AbstractTranscodingTracker(Transcoder transcoder) {
        this.taskEventListener = transcoder.taskStatusNotifyListener;
    }

    @Override
    public Object getUserData() {
        return userData;
    }

    @Override
    public void setUserData(Object userData) {
        this.userData = userData;
    }

    public TaskStatus getTranscodeEndState() {
        return getStopFlag() == Transcoder.STOP_FLAG_MANUAL ? TaskStatus.CANCELLED
                : (getLastError() == 0 ? TaskStatus.COMPLETED : TaskStatus.ERROR);
    }

    @Override
    public int getLastError() {
        return this.transtaskExitCode;
    }

    @Override
    public String getLastErrorDesc() {
        return this.exitErrorDesc;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }


    @Override
    public Date getTaskStartAt() {
        Date t = null;
        if (this.startAt != null) {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                t = fmt.parse(this.startAt);
            } catch (ParseException e) {
                LOG.error("", e);
            }
        }
        return t;
    }

    @Override
    public Date getTaskEndAt() {
        return this.endAt;
    }

    @Override
    public boolean needRestartTask() {
        return this.restartTask;
    }

    @Override
    public int getTranscoderRunningStatus() {
        return this.procTranscoderStatus;
    }

    @Override
    public boolean isOnOutput() {
        return this.isDoOutput;
    }

    @Override
    public TranscodeStatistic getStatistic() {
        return this.transcodeStat;
    }
}
