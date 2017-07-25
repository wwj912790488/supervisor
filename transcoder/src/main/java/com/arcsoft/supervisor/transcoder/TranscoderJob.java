package com.arcsoft.supervisor.transcoder;


import com.arcsoft.supervisor.transcoder.type.TaskStatus;

/**
 * job before/after transcoding
 * subclass should implement Integer call();
 *
 * @author Bing
 */
public abstract class TranscoderJob implements Runnable {

    public static final int SHORT_LIFE_TYPE_JOB = 0;
    public static final int LONG_LIFE_TYPE_JOB = 1;

    protected ITranscodingTracker transcodingTracker = null;

    /**
     * progress of current transcoding task
     */
    protected int progress = 0;
    /**
     * name of current transcoding task
     */
    protected String name = null;

    private volatile int done = -1;
    private int result = 0;

    public int getProgress() {
        return progress;
    }

    public String getName() {
        return name;
    }

    public final boolean isDone() {
        return done == 1;
    }


    public final boolean isStarted() {
        return done >= 0;
    }

    public final int getResult() {
        return result;
    }

    public String getErrorDescription() {
        return result == 0 ? null : "error code=" + result;
    }

    @Override
    public void run() {
        done = 0;
        result = doJob();
        done = 1;
    }

//	public int undoJob(){return 0;}	
//	public void interuptJob(){}

    /**
     * @return error code
     */
    protected abstract int doJob();

    /**
     * NOTE: LONG_LIFE_TYPE_JOB can only be done with submitTask
     *
     * @return SHORT_LIFE_TYPE_JOB or LONG_LIFE_TYPE_JOB
     */
    public abstract int getJobLifeType();

    /**
     * eg. uploading
     *
     * @return
     */
    public TaskStatus getJobTaskStatus() {
        return null;
    }

    public void init(ITranscodingTracker t) {
        this.transcodingTracker = t;
    }

    public void uninit() {
        this.transcodingTracker = null;
    }
}
