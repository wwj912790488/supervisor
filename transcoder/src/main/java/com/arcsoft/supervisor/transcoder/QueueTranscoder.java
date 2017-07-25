package com.arcsoft.supervisor.transcoder;

import com.arcsoft.supervisor.transcoder.type.TaskStatus;
import com.arcsoft.supervisor.transcoder.util.SystemExecutor;
import com.arcsoft.supervisor.transcoder.util.errorcode.ErrorCode;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * TranscodingManager
 *
 * @author Bing
 */
public abstract class QueueTranscoder extends Transcoder {

    private static Logger logger = Logger.getLogger(QueueTranscoder.class);

    private static int TAKE_TYPE_ANY = 0;
    private static int TAKE_TYPE_NOT_HD = 1;
    private static int TAKE_TYPE_NOT_SD = 2;
    private static int TAKE_TYPE_NOT_SDI = 3;
    private static int TAKE_TYPE_NOT_ASI = 4;

    /**
     * waiting queue
     */
    private final WaitingQueue waitingTasks = new WaitingQueue();

    /**
     * proc submit waiting tasks
     */
    private final ReSubmitWaitingProc procReSubmitWaiting = new ReSubmitWaitingProc();
    private volatile boolean stopped = false;

    @Override
    public void destroy() {
        try {
            stopped = true;
            waitingTasks.put(TranscodingKey.NULL_KEY);
        } catch (InterruptedException e1) {
            logger.error(null, e1);
        }
        super.destroy();
    }

    public int getWaitingTaskCount() {
        return this.waitingTasks.size();
    }

    /**
     * submit task to svr and try to start it
     * if need waiting, then TaskStatus.WAITING is notified.
     */
    @Override
    public void submitTask(final TranscodingKey taskId, int runtimePrioity, TranscodingParams taskParam) throws Exception {
        if (!procReSubmitWaiting.isStarted()) {
            procReSubmitWaiting.start();
        }
        if (taskId == null || taskParam == null || this.getTranscodingTracker(taskId) != null)
            return;

        if (runtimePrioity == TaskRuntimePrioity.PRIORITY_HIGH) {
            cancelAllNoneQuickTasks();
        }

        final ITranscodingTracker tt = createTranscodingTracker(taskId, runtimePrioity, taskParam);

        synchronized (this) {
            if (this.getTranscodingTracker(taskId) != null) {
                return;
            }
            addTranscodingTracker(taskId, tt);
        }

        //prejob
        final List<TranscoderJob> jobs = tt.getTranscodingParams().getBeforeTranscodingJobs(TranscoderJob.LONG_LIFE_TYPE_JOB);

        if (jobs == null || jobs.isEmpty()) {
            waitingTasks.put(taskId);
        } else {
            Runnable tryStart = new Runnable() {
                public void run() {
                    try {
                        for (TranscoderJob job : jobs) {

                            for (int i = 0; i < 3; i++) {
                                job.run();
                                if (job.getResult() == 0) {
                                    break;
                                }
                                logger.error("BeforeTranscodingJob failed - " + job.getName());
                                try {
                                    Thread.sleep(5000);
                                } catch (InterruptedException e) {
                                    logger.error(null, e);
                                }
                            }

                            if (job.getResult() != 0) {
                                taskStatusNotifyListener.fireTaskStatusChanged(tt.getTranscodingKey(), TaskStatus.ERROR); //notify as error
                                return;
                            }

                        }

                        waitingTasks.put(taskId);

                    } catch (Exception e) {
                        logger.info(null, e);
                    }

                }
            };
            SystemExecutor.getThreadPoolExecutor().execute(tryStart);
        }

        try {
            taskStatusNotifyListener.fireTaskStatusChanged(taskId, TaskStatus.WAITING);
        } catch (Exception e) {
            logger.info(null, e);
        }
    }

    /**
     * class for starting task from waiting queue
     */
    private final class ReSubmitWaitingProc extends Thread {
        private AtomicInteger started = new AtomicInteger(0);
        private int lastEndTaskCount = -1;
        private Object lastTaskIdTryStart = null;

        public final boolean isStarted() {
            return started.get() != 0;
        }


        @Override
        public synchronized void start() {
            if (started.compareAndSet(0, 1)) {
                super.start();
            }
        }


        private void doWaitUtilHaveTaskEnd() {
            while (this.lastEndTaskCount == iEndTaskCount) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    logger.error(null, e);
                }
            }
            this.lastEndTaskCount = iEndTaskCount;
        }

        /**
         * @param maxTime
         */
        private void doWait(long maxTime) {
            if (this.lastEndTaskCount != iEndTaskCount) { //there is new task ended
                this.lastEndTaskCount = iEndTaskCount;
                return;
            }
            try {
                Thread.sleep(maxTime);
            } catch (Exception e) {
                logger.error(null, e);
            }
        }

        @Override
        public void run() {
            logger.info("----resubmit task is started!-----");

            int takeType = TAKE_TYPE_ANY;

            while (!stopped) {
                try {
                    TranscodingKey taskId = null;
                    if (takeType == TAKE_TYPE_ANY) {
                        try {
                            taskId = waitingTasks.take();
                        } catch (InterruptedException e) {
                            logger.error(null, e);
                            break;
                        }
                    } else {
                        taskId = waitingTasks.takeByType(takeType);
                    }

                    int lastTakeType = takeType;
                    takeType = TAKE_TYPE_ANY;

                    if (AppConfig.getDebugMask() != 0) logger.info("-----take:" + taskId);

                    //the while exit control
                    if (taskId == null || taskId.equals(TranscodingKey.NULL_KEY)) {
                        if (lastTakeType == TAKE_TYPE_ANY) {
                            break;
                        } else {
                            continue;
                        }
                    }

                    ITranscodingTracker tt = getTranscodingTracker(taskId);
                    if (tt == null || tt.getStopFlag() != 0)
                        continue;

                    int starterr = -1;
                    try {
                        if (taskId.equals(lastTaskIdTryStart)) {
                            doWaitUtilHaveTaskEnd();
                        }
                        if (tt.getStopFlag() != 0)
                            continue;
                        if (stopped) {
                            break;
                        }
                        starterr = startTask(tt);
                        if (stopped && starterr == 0) {
                            tt.destroy();
                            break;
                        }
                        lastTaskIdTryStart = taskId;
                    } catch (Exception e) {
                        logger.error("taskId=" + taskId + " start task exception:", e);
                        starterr = -1;
                    }

                    if (ErrorCode.inCatNoCPU(starterr)) {
                        waitingTasks.untake(taskId);
                        if (AppConfig.getDebugMask() != 0) {
                            logger.info("-----untake taskId=:" + taskId);
                        }

                        if (lastTakeType == TAKE_TYPE_ANY) {
                            switch (starterr) {
                                case ErrorCode.ERR_NOCPU_HD_COUNT:
                                    takeType = TAKE_TYPE_NOT_HD;
                                    break;
                                case ErrorCode.ERR_NOCPU_SD_COUNT:
                                    takeType = TAKE_TYPE_NOT_SD;
                                    break;
                                case ErrorCode.ERR_NOCPU_SDI_COUNT:
                                    takeType = TAKE_TYPE_NOT_SDI;
                                    break;
                                case ErrorCode.ERR_NOCPU_ASI_COUNT:
                                    takeType = TAKE_TYPE_NOT_ASI;
                                    break;
                                default:
                                    doWait(3000);
                                    break;
                            }
                        } else {
                            doWait(3000);
                        }
                    } else if (ErrorCode.ERR_NONE != starterr) {
                        try {
                            taskStatusNotifyListener.fireTaskStatusChanged(taskId, TaskStatus.ERROR);
                        } catch (Exception e) {
                        }
                    }
                } catch (Exception ex) {
                    logger.error(null, ex);
                }

            }//~while
            logger.info("----resubmit task is ended!----");
        }
    }

    /**
     * class WaitingQueue
     */
    private final class WaitingQueue {
        /**
         * waiting tasks queue
         */
        private BlockingQueue<TranscodingKey> waitingTasks;


        public WaitingQueue() {
            waitingTasks = new PriorityBlockingQueue<TranscodingKey>(32, new WaitingQueueComparator());
        }

        public TranscodingKey take() throws InterruptedException {
            return waitingTasks.take();
        }

        public void untake(TranscodingKey t) {
            try {
                this.waitingTasks.put(t);
            } catch (InterruptedException e) {
            }
        }

        public TranscodingKey takeByType(int type) {
            TranscodingKey ret = null;
            ArrayList<TranscodingKey> tmp = new ArrayList<TranscodingKey>();
            try {
                while (!waitingTasks.isEmpty()) {
                    TranscodingKey t = waitingTasks.take();
                    if (t == null) continue;
                    ITranscodingTracker tt = getTranscodingTracker(t);
                    if (tt == null) continue;
                    if (tt.getStopFlag() == STOP_FLAG_NONE) {
                        if (TAKE_TYPE_NOT_HD == type && tt.getTranscodingParams().getHDOuputCount() == 0 ||
                                TAKE_TYPE_NOT_SD == type && tt.getTranscodingParams().getSDOuputCount() == 0 ||
                                TAKE_TYPE_NOT_SDI == type && !"SDI".equalsIgnoreCase(tt.getTranscodingParams().getInputType()) ||
                                TAKE_TYPE_NOT_ASI == type && !"ASI".equalsIgnoreCase(tt.getTranscodingParams().getInputType())) {
                            ret = tt.getTranscodingKey();
                            break;
                        }
                    }
                    tmp.add(t);
                }
            } catch (Exception e) {
            } finally {
                waitingTasks.addAll(tmp);
            }
            return ret;
        }

        public synchronized void put(TranscodingKey t) throws InterruptedException {
            this.waitingTasks.put(t);
        }

        public int size() {
            return this.waitingTasks.size();
        }

    }

    private final class WaitingQueueComparator implements Comparator<TranscodingKey> {
        @Override
        public int compare(TranscodingKey t1, TranscodingKey t2) {
            ITranscodingTracker tt1 = getTranscodingTracker(t1);
            if (tt1 == null)
                return 1;
            ITranscodingTracker tt2 = getTranscodingTracker(t2);
            if (tt2 == null)
                return -1;
            int r = tt2.getTranscodingParams().getTaskRetrievePriority() - tt1.getTranscodingParams().getTaskRetrievePriority();
            if (r == 0) {
                r = (int) (tt1.getTranscodingParams().getTaskSubmitTime() - tt2.getTranscodingParams().getTaskSubmitTime());
            }
            return r;
        }
    }
}
