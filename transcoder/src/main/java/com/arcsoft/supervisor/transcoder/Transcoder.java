package com.arcsoft.supervisor.transcoder;

import com.arcsoft.supervisor.transcoder.Util.SerialTaskEventNotifier;
import com.arcsoft.supervisor.transcoder.type.TaskStatus;
import com.arcsoft.supervisor.transcoder.util.errorcode.ErrorCode;
import com.arcsoft.supervisor.utils.app.App;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author Bing
 * @version 2.0
 */
public abstract class Transcoder {

    /**
     * 0 is preserved, instead for no stop command
     */
    public static final int STOP_FLAG_NONE = 0;
    /**
     * flag, cannot be 0
     */
    public static final int STOP_FLAG_MANUAL = 1;
    /**
     * flag, cannot be 0
     */
    public static final int STOP_FLAG_SYS = 2;

    private static Logger logger = Logger.getLogger(Transcoder.class);

    /**
     * transcoder limit checker
     */
    private LimitChecker limitChecker = null;

    private final BlockingQueue<TranscodingKey> startTaskConcurrent = new ArrayBlockingQueue<TranscodingKey>(1);
    /**
     * notify executor for transcoder
     */
    private final SerialTaskEventNotifier serialTaskEventNotifier = new SerialTaskEventNotifier();

    /**
     * task trackers - {taskId, ITranscodingTracker}...
     */
    Map<TranscodingKey, ITranscodingTracker> trackers = new ConcurrentHashMap<TranscodingKey, ITranscodingTracker>();

    /**
     * internal used - end task count statistic
     */
    protected volatile int iEndTaskCount = 0;

    /**
     * task event NotifyListener
     */
    protected ITranscodingTracker.IEventNotifyListener taskStatusNotifyListener = new ITranscodingTracker.IEventNotifyListener() {
        @Override
        public void fireTaskStatusChanged(TranscodingKey taskId, TaskStatus status) {
            ITranscodingTracker tt = null;
            if (TaskStatus.COMPLETED.equals(status)
                    || TaskStatus.ERROR.equals(status)
                    || TaskStatus.CANCELLED.equals(status)) {
                tt = trackers.remove(taskId);
                if (tt != null) {
                    Transcoder.this.fireTranscodingStatusChanged(tt, status);
                    Transcoder.this.fireTrackerDestroy(tt);
                    iEndTaskCount++;
                    tt.destroy();
                    stopPreviousprocess(tt,tt.getPid(),taskId.toString());
                }
            } else {
                tt = trackers.get(taskId);
                Transcoder.this.fireTranscodingStatusChanged(tt, status);
            }
        }

        @Override
        public void fireTaskErrorMessage(TranscodingKey taskId, int level, int code, String msg) {
            ITranscodingTracker tt = trackers.get(taskId);
            if (tt != null) {
                Transcoder.this.fireTaskErrorMessage(tt, level, code, msg);
            }
        }
    };

    /**
     * transcoding status listeners
     */
    private List<ITranscodingStatusListener> statusListeners = new ArrayList<ITranscodingStatusListener>(4);
    /**
     * transcoding error message listeners
     */
    private List<ITranscodingMessageListener> messageListeners = new ArrayList<ITranscodingMessageListener>(4);

    public void addTranscodingStatusListener(ITranscodingStatusListener l) {
        if (l == null)
            return;
        if (!this.statusListeners.contains(l))
            this.statusListeners.add(l);
    }

    public void removeTranscodingStatusListener(ITranscodingStatusListener l) {
        if (l == null)
            return;
        this.statusListeners.remove(l);
    }

    public void addTranscodingMessageListener(ITranscodingMessageListener l) {
        if (l == null)
            return;
        if (!this.messageListeners.contains(l))
            this.messageListeners.add(l);
    }

    public void removeTranscodingMessageListener(ITranscodingMessageListener l) {
        if (l == null)
            return;
        this.messageListeners.remove(l);
    }

    private void fireTrackerDestroy(final ITranscodingTracker tracker) {
        Runnable[] notifyActions = new Runnable[1];
        notifyActions[0] = new Runnable() {
            @Override
            public void run() {
                tracker.destroy();
            }
        };
        serialTaskEventNotifier.execute(tracker.getTranscodingKey(), notifyActions);
    }

    /**
     * notify transcoding status listeners
     *
     * @param tracker
     * @param status
     */
    private void fireTranscodingStatusChanged(final ITranscodingTracker tracker, final TaskStatus status) {
        Runnable[] notifyActions = new Runnable[this.statusListeners.size()];
        for (int i = 0; i < this.statusListeners.size(); i++) {
            final ITranscodingStatusListener l = statusListeners.get(i);
            notifyActions[i] = new Runnable() {
                @Override
                public void run() {
                    long t0 = System.currentTimeMillis();
                    try {
                        l.handleTaskStatusChanged(tracker, status);
                    } catch (Exception e) {
                        logger.error("taskId=" + tracker.getTranscodingKey(), e);
                    }
                    long t1 = System.currentTimeMillis();
                    if (t1 - t0 > 500) {
                        String errmsg = "[PERFORMANCE WARNING]TOO MUCH TIME COST: statusListener=" + l + " , time cost=" + (t1 - t0);
                        logger.warn(errmsg);
                        //fireTaskErrorMessage(0,taskId,0,errmsg);
                    }
                }
            };
        }
        serialTaskEventNotifier.execute(tracker.getTranscodingKey(), notifyActions);
    }

    private void fireTaskErrorMessage(final ITranscodingTracker tracker, final int level, final int code, final String msg) {
        Runnable[] notifyActions = new Runnable[this.messageListeners.size()];
        for (int i = 0; i < this.messageListeners.size(); i++) {
            final ITranscodingMessageListener l = messageListeners.get(i);
            notifyActions[i] = new Runnable() {
                @Override
                public void run() {
                    long t0 = System.currentTimeMillis();
                    try {
                        l.fireTaskErrorMessage(tracker, level, code, msg);
                    } catch (Exception e) {
                        logger.error("taskId=" + tracker.getTranscodingKey(), e);
                    }
                    long t1 = System.currentTimeMillis();
                    if (t1 - t0 > 500) {
                        logger.warn("TOO MUCH TIME COST: messageListener=" + l + " , time cost=" + (t1 - t0));
                    }
                }
            };
            //SystemExecutor.asyncExecute(r, 0);
        }
        serialTaskEventNotifier.execute(tracker.getTranscodingKey(), notifyActions);
    }

    public void setLimitChecker(LimitChecker limitChecker) {
        this.limitChecker = limitChecker;
    }

    public LimitChecker getLimitChecker() {
        if (this.limitChecker == null) {
            this.limitChecker = new LimitChecker(this);
        }
        return this.limitChecker;
    }

    public List<ITranscodingTracker> getRunningTaskTracker() {
        ArrayList<ITranscodingTracker> tts = new ArrayList<ITranscodingTracker>();
        Object[] ts = this.trackers.keySet().toArray();
        for (int i = 0; i < ts.length; i++) {
            ITranscodingTracker t = this.trackers.get(ts[i]);
            if (t == null)
                continue;
            if (t.getTranscoderRunningStatus() >= ITranscodingTracker.TRANSCODING_STARTING) {
                tts.add(t);
            }
        }
        return tts;
    }

    public int getRunningTaskCount() {
        return getRunningTaskTracker().size();
    }

    /**
     * check whether excced the max transcoder count
     * <p/>
     * used by temobi to check whether server is busy, it's a rough estimation,
     * you should use {@link #checkLimit(ITranscodingTracker, java.io.Writer)} instead.
     *
     * @return
     */
    public boolean isReachMaxTranscoderLimit() {
        return getLimitChecker().checkLimit(null, null) != 0;
    }

    /**
     * is super task in running
     *
     * @return
     */
    public boolean isSupperTaskRunning() {
        Object[] ts = this.trackers.values().toArray();
        for (int i = 0; i < ts.length; i++) {
            ITranscodingTracker t = (ITranscodingTracker) ts[i];
            if (t.getPriority() == TaskRuntimePrioity.PRIORITY_HIGH && t.getTaskStartAt() != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get queue task in running
     *
     * @param taskId
     * @return ITranscodingTracker or null
     */
    public ITranscodingTracker getTranscodingTracker(TranscodingKey taskId) {
        return this.trackers.get(taskId);
    }

    public ITranscodingTracker getTranscodingTracker(int seqenceNumber) {
        return this.trackers.get(new TranscodingKey(seqenceNumber));
    }

    public ITranscodingTracker[] getAllTranscodingTrackers() {
        return this.trackers.values().toArray(new ITranscodingTracker[0]);
    }

    protected final boolean addTranscodingTracker(TranscodingKey taskId, ITranscodingTracker tt) {
        ITranscodingTracker old = trackers.put(taskId, tt);
        if (old != null && old != tt) {
            logger.error("TASKTRACKERS WITH THE SAME ID FOUND! taskId=" + taskId);
            stopPreviousprocess(old,tt.getPid(),taskId.toString());
        }
        return true;
    }

    /**
     * submit task to execute in queue in WAITING, then, to execute with {@link #startTask(ITranscodingTracker)}
     *
     * @param taskId
     * @param runtimePrioity
     * @param taskParam
     * @throws Exception
     */
    public abstract void submitTask(final TranscodingKey taskId, int runtimePrioity, TranscodingParams taskParam) throws Exception;

    /**
     * ez start start method
     *
     * @param taskId
     * @param priority
     * @param taskParam
     * @return
     */
    public int startTask(TranscodingKey taskId, int priority, TranscodingParams taskParam) {
        return startTask(createTranscodingTracker(taskId, priority, taskParam));
    }

    /**
     * start task directly
     *
     * @param tt ITranscodingTracker
     * @return 0:success; Transcoder.ERR_NOCPU, or other error code
     */
    public int startTask(ITranscodingTracker tt) {
        int ret = 0;
        long t0 = System.currentTimeMillis();

        synchronized (this) {
            //license check
            if (tt.getPriority() != TaskRuntimePrioity.PRIORITY_HIGH && this.isSupperTaskRunning()) {
                return ErrorCode.ERR_NOCPU;
            }
            StringBuilder errbuf = new StringBuilder();
            ret = getLimitChecker().checkLimit(tt, errbuf);
            if (ret != ErrorCode.ERR_NONE) {
                logger.info(errbuf.toString());
                taskStatusNotifyListener.fireTaskErrorMessage(tt.getTranscodingKey(), 0, ret, errbuf.toString());
                return ret;
            }
            //put to trackers
            addTranscodingTracker(tt.getTranscodingKey(), tt);
        }

        long t1 = System.currentTimeMillis();
        if (AppConfig.getDebugMask() != 0)
            logger.info("taskid:" + tt.getTranscodingKey() + ",check transcoder max limit time cost=" + (t1 - t0));

        boolean added = false;
        try {
            try {
                added = startTaskConcurrent.offer(TranscodingKey.NULL_KEY, 60, TimeUnit.SECONDS);
            } catch (Exception e) {
                logger.error(null, e);
            }
            if (added) {
                long t2 = System.currentTimeMillis();
                ret = doStartTask(tt);
                long t3 = System.currentTimeMillis();
                if (AppConfig.getDebugMask() != 0)
                    logger.info("taskid:" + tt.getTranscodingKey() + ",wait time pre-doStartTask:" + (t2 - t1) + ",doStartTask time cost=" + (t3 - t2));
                if (ret != 0 && ret != ErrorCode.ERR_NOCPU) {
                    trackers.remove(tt.getTranscodingKey());
                    stopPreviousprocess(tt,tt.getPid(),tt.getTranscodingKey().toString());
                }
            } else {
                ret = ErrorCode.ERR_NOCPU;
            }
        } catch (Exception e) {
            logger.error(null, e);
        } finally {
            if (added)
                startTaskConcurrent.poll();
        }
        return ret;
    }

    public int cancelTask(int seqenceNumber, int stopFlag) {
        return cancelTask(new TranscodingKey(seqenceNumber), stopFlag);
    }

    public int cancelTask(TranscodingKey taskId, int stopFlag) {
        return doCancelTask(taskId, stopFlag);
    }

    public void cancelAllTasks() {
        TranscodingKey[] ts = this.trackers.keySet().toArray(new TranscodingKey[0]);
        for (int i = 0; i < ts.length; i++) {
            cancelTask(ts[i], STOP_FLAG_MANUAL);
        }
    }

    protected void cancelAllNoneQuickTasks() {
        ITranscodingTracker[] ts = getAllTranscodingTrackers();
        for (int i = 0; i < ts.length; i++) {
            if (ts[i].getPriority() != TaskRuntimePrioity.PRIORITY_HIGH) {
                cancelTask(ts[i].getTranscodingKey(), STOP_FLAG_MANUAL);
            }
        }
    }

    /**
     * @param taskId
     * @param priority
     * @param taskParam
     * @return
     */
    public abstract ITranscodingTracker createTranscodingTracker(
            TranscodingKey taskId, int priority, TranscodingParams taskParam);

    /**
     * @param transcodingTracker
     * @return 0:success; Transcoder.ERR_NOCPU, or other error code
     * @throws Exception
     */
    protected abstract int doStartTask(ITranscodingTracker transcodingTracker) throws Exception;

    /**
     * @param taskId
     */
    protected abstract int doCancelTask(TranscodingKey taskId, int stopFlag);

    public void destroy() {
        ITranscodingTracker[] ts = this.getAllTranscodingTrackers();
        for (int i = 0; i < ts.length; i++) {
            ts[i].destroy();
        }
        trackers.clear();
    }

    /**
     * @param duration
     * @param percent
     * @param timeConsumed
     * @return power*100
     */
    static int uGetTranscodingPower(int duration, int percent, int timeConsumed) {
        int ret = 0;
        if (duration > 0 && timeConsumed > 0) {
            ret = (int) (percent * duration * 1.0f / 1000 / timeConsumed);
        }
        return ret;
    }

    void stopPreviousprocess(ITranscodingTracker tracker,int pid,String taskid)
    {
        try
        {
            if(tracker!=null)
                tracker.destroy();

            boolean exited = true;
            List<String> results = App.runShell("pgrep -f transcoder.exe");
            for (String existpid:results) {
                if(existpid!=null&&!existpid.isEmpty())
                {
                    if(existpid.compareTo(String.valueOf(pid))==0)
                    {
                        exited = false;
                        break;
                    }
                }
            }

            if (!exited)
            {
                String msg = String.format("start to kill process(pid=%d), task id = %s",pid,taskid);
                logger.info(msg);

                App.runShell("kill"+" -9 "+String.valueOf(pid));
            }
            else
            {
                logger.info(String.format("taskid=%s, pid=%d, process exit success.",taskid,pid));
            }

        }catch (Exception e)
        {
            logger.info(String.format("taskid=%s, pid=%d, failed to check or kill process.",taskid,pid));
        }
    }

}
