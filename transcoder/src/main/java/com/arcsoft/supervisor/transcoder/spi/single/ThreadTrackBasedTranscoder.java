package com.arcsoft.supervisor.transcoder.spi.single;

import com.arcsoft.supervisor.transcoder.*;
import com.arcsoft.supervisor.transcoder.util.SystemExecutor;
import com.arcsoft.supervisor.transcoder.util.errorcode.ErrorCode;


/**
 * @author Bing
 */
public class ThreadTrackBasedTranscoder extends QueueTranscoder {

    static {
        if (AppConfig.getPropertyAsint("transcoder.startupMode", 0) != 0) {
            int preloadCount = AppConfig.getPropertyAsint("transcoder.preload.initCount", 4);
            ProcessPool.getInstance().prepareProcesses(preloadCount);
        }
    }

    public ThreadTrackBasedTranscoder() {
    }

    @Override
    public ITranscodingTracker createTranscodingTracker(TranscodingKey taskId,
                                                        int priority, TranscodingParams taskParam) {
        TaskTracker tt = new TaskTracker(this, taskId,
                priority, taskParam);
        return tt;
    }

    @Override
    protected int doStartTask(ITranscodingTracker tracker) throws Exception {
        TaskTracker tt = (TaskTracker) tracker;
        return tt.startTask();
    }

    @Override
    public int doCancelTask(TranscodingKey taskid, int stopFlag) {
        TaskTracker tt = (TaskTracker) getTranscodingTracker(taskid);
        if (tt != null) {
            tt.stop(stopFlag);
        } else {
            return ErrorCode.ERR_TASK_NOT_EXIST;
        }
        return 0;
    }

    @Override
    public void destroy() {
        super.destroy();
        ProcessPool.getInstance().freeProcesses();
        SystemExecutor.destroy();
    }

    /**
     * A extend method for support inject a {@code ITranscodingStatusListener}.
     *
     */
    public void setITranscodingStatusListener(ITranscodingStatusListener listener){
        addTranscodingStatusListener(listener);
    }

    /**
     * Proxy method to add the given {@code messageListener}.
     * @param messageListener the listener of alert message
     */
    public void setITranscodingMessageListener(ITranscodingMessageListener messageListener){
        addTranscodingMessageListener(messageListener);
    }

}
