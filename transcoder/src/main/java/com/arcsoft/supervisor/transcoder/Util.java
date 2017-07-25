package com.arcsoft.supervisor.transcoder;

import com.arcsoft.supervisor.transcoder.type.TaskStatus;
import com.arcsoft.supervisor.transcoder.util.SystemExecutor;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Util {

    public static TaskStatus toTaskStatus(int internalStatus) {
        if (internalStatus == ITranscodingTracker.TRANSCODING_NOT_START) {
            return TaskStatus.WAITING;
        } else if (internalStatus == ITranscodingTracker.TRANSCODING_SUSPEND) {
            return TaskStatus.SUSPENDED;
        } else if (internalStatus == ITranscodingTracker.TRANSCODING_PREJOB) {
            return TaskStatus.DOWNLOADING;
        } else if (internalStatus == ITranscodingTracker.TRANSCODING_STOPPING) {
            return TaskStatus.STOPPING;
        } else if (internalStatus > ITranscodingTracker.TRANSCODING_PREJOB) {
            return TaskStatus.RUNNING;
        }
        return null;
    }

    public static class TrackerEventNotifyAdapter implements ITranscodingTracker.IEventNotifyListener {
        @Override
        public void fireTaskErrorMessage(TranscodingKey taskId, int level, int code, String msg) {
        }

        @Override
        public void fireTaskStatusChanged(TranscodingKey taskId, TaskStatus status) {
        }
    }

    public static class TLogger {

        public static int DEBUG_MASK_CMD = 0x40000000;
        public static int DEBUG_MASK_NOUT = 0x80000000;
        public static int DEBUG_MASK_QUEUE = 0x0000001;
    }

    /**
     * serial the event notify on one task id
     *
     * @author Bing
     */
    static class SerialTaskEventNotifier {

        private ConcurrentLinkedQueue<TaskEventNotifyAction> evtQueue = new ConcurrentLinkedQueue<TaskEventNotifyAction>();
        private List<Object> onRunnings = Collections.synchronizedList(new LinkedList<Object>());
        private TaskEventNotifyAction active = null;

        private synchronized void executeNext() {
            active = null;
            for (Iterator<TaskEventNotifyAction> iter = evtQueue.iterator(); iter.hasNext(); ) {
                TaskEventNotifyAction a = iter.next();
                if (onRunnings.contains(a.taskId))
                    continue;
                active = a;
                iter.remove();
                break;
            }

            if (active != null) {
                onRunnings.add(active.taskId);
                SystemExecutor.getThreadPoolExecutor().execute(active);
            }
        }

        /**
         * @param taskId
         * @param actions
         */
        public synchronized void execute(Object taskId, Runnable[] actions) {
            TaskEventNotifyAction a = new TaskEventNotifyAction(taskId, actions);
            evtQueue.add(a);
            if (active == null) {
                executeNext();
            }
        }

        @SuppressWarnings("rawtypes")
        private class TaskEventNotifyAction implements Runnable {
            Object taskId;
            Runnable[] fRuns = null;

            public TaskEventNotifyAction(Object taskId, Runnable[] fRuns) {
                this.taskId = taskId;
                this.fRuns = fRuns;
            }

            @Override
            public void run() {
                try {
                    Future[] fs = new Future[fRuns.length];
                    for (int i = 0; i < fRuns.length; i++) {
                        fs[i] = SystemExecutor.getThreadPoolExecutor().submit(fRuns[i]);
                    }
                    for (int i = 0; i < fs.length; i++) {
                        try {
                            if (fs[i] != null)
                                fs[i].get(9000, TimeUnit.MILLISECONDS);
                        } catch (TimeoutException e) {
                            fs[i].cancel(true);
                        } catch (Exception e) {
                        }
                    }
                } finally {
                    try {
                        onRunnings.remove(this.taskId);
                    } catch (Exception e) {
                    }
                    executeNext();
                }
            }
        }
    }

    /**
     * TrackerInfoCache
     *
     * @author Bing
     */
    public static class TrackerInfoCache<T> {
        private T obj = null;
        private long lastTime = 0;

        public boolean isExpired(int expiredTime) {
            return System.currentTimeMillis() - lastTime > expiredTime;
        }

        public T getCache() {
            return obj;
        }

        public void setCache(T o) {
            this.obj = o;
            lastTime = System.currentTimeMillis();
        }

        public void setActionSubmitTime(long lastTime) {
            this.lastTime = lastTime;
        }

    }

}
