package com.arcsoft.supervisor.transcoder;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Bing
 */
public abstract class TranscodingParams {
    /**
     * OUTPUT_OPTION: no output
     */
    public static final int OUTPUT_OPTION_OUPUT_CLOSE = 0;
    /**
     * OUTPUT_OPTION: output
     */
    public static final int OUTPUT_OPTION_OUPUT_NORMAL = 1;
    /**
     * OUTPUT_OPTION: output backup m:n
     */
    public static final int OUTPUT_OPTION_OUPUT_BACKUP_N = 2;

    //predefined user data
    public static final String USER_DATA_START_TASK_EXTRA_NOTE = "start_task_extra_note";
    public static final String USER_DATA_START_TASK_USERDATA = "start_task_userdata";

    /**
     * native transcoder xml
     */
    protected String xmlparam = null;
    /**
     * output group count cache
     */
    protected int outputGroupCount = -1;
    /**
     * hd count
     */
    protected int hdOuputCount = -1;
    /**
     * sd count
     */
    protected int sdOuputCount = -1;
    /**
     * input type
     */
    protected String inputType = null;
    /**
     * task name
     */
    protected String taskName = null;
    /**
     * task retrive priority
     */
    protected int taskRetrievePriority = 0;
    /**
     * task submit task
     */
    protected long taskSubmitTime = System.currentTimeMillis();
    /**
     * task output option
     */
    protected int outputOption = OUTPUT_OPTION_OUPUT_NORMAL;
    /**
     * task guid
     */
    protected String taskGuid = null;
    /**
     * userdata
     */
    private Map<String, Object> userDatas = null;
    /**
     * {index,duration;...}
     */
    protected Map<Integer, Integer> durations = new Hashtable<Integer, Integer>();

    private List<TranscoderJob> beforeTranscodingJobs = new ArrayList<TranscoderJob>(2);
    private List<TranscoderJob> afterTranscodingJobs = new ArrayList<TranscoderJob>(2);
    private AtomicInteger uninitTranscodingJobs = new AtomicInteger(0);


    protected TranscodingParams() {
    }

    public final Object getUserData(String name) {
        return this.userDatas == null ? null : this.userDatas.get(name);
    }

    public synchronized final void setUserData(String name, Object userData) {
        if (this.userDatas == null) {
            this.userDatas = new HashMap<String, Object>();
        }
        this.userDatas.put(name, userData);
    }

    public String getTaskName() {
        return this.taskName;
    }

    public long getTaskSubmitTime() {
        return taskSubmitTime;
    }

    public int getTaskRetrievePriority() {
        return taskRetrievePriority;
    }

    /**
     * @param index
     * @return ms
     */
    public int getInputSrcDuration(int index) {
        int duration = 0;
        if (this.durations.containsKey(new Integer(index))) {
            duration = this.durations.get(new Integer(index));
        }
        return duration;
    }

    public void setInputSrcDuration(int index, int duration) {
        this.durations.put(index, duration);
    }

    public String getTaskGuid() {
        return taskGuid;
    }

    public void setTaskGuid(String taskGuid) {
        this.taskGuid = taskGuid;
    }

    public void setNoOutputOnInit(boolean noOutputOnInit) {
        this.outputOption = noOutputOnInit ? OUTPUT_OPTION_OUPUT_CLOSE : OUTPUT_OPTION_OUPUT_NORMAL;
    }

    public boolean isNoOutputOnInit() {
        return outputOption != OUTPUT_OPTION_OUPUT_CLOSE;
    }

    public int getOutputOption() {
        return outputOption;
    }

    public void setOutputOption(int outputOption) {
        this.outputOption = outputOption;
    }

    /**
     * to be called on starting task
     */
    public abstract void checkTranscoderXmlReady() throws Exception;

    /**
     * get param as xml string
     *
     * @return
     */
    public String getParamAsXml() {
        return this.xmlparam;
    }

    /**
     * @return InputType.ASI, InputType.SDI, ...
     */
    public String getInputType() {
        return this.inputType;
    }

    public int getHDOuputCount() {
        return this.hdOuputCount;
    }

    public int getSDOuputCount() {
        return this.sdOuputCount;
    }

    public int getOutputGroupCount() {
        return this.outputGroupCount;
    }

    public void initTranscodingJobs(ITranscodingTracker t) {
        uninitTranscodingJobs.set(0);
        for (TranscoderJob beforeTranscodingJob : beforeTranscodingJobs) {
            if (beforeTranscodingJob != null) {
                beforeTranscodingJob.init(t);
            }
        }

        for (TranscoderJob afterTranscodingJob : afterTranscodingJobs) {
            if (afterTranscodingJob != null) {
                afterTranscodingJob.init(t);
            }
        }
    }

    public void uninitTranscodingJobs() {
        if (uninitTranscodingJobs.compareAndSet(0, 1)) {
            for (TranscoderJob beforeTranscodingJob : beforeTranscodingJobs) {
                if (beforeTranscodingJob != null) {
                    beforeTranscodingJob.uninit();
                }
            }
            for (TranscoderJob afterTranscodingJob : afterTranscodingJobs) {
                if (afterTranscodingJob != null) {
                    afterTranscodingJob.uninit();
                }
            }
        }
    }

    /**
     * @param exJobs
     * @param jobLifeType
     * @return
     */
    private static List<TranscoderJob> getTranscodingJobs(List<TranscoderJob> exJobs, int jobLifeType) {
        ArrayList<TranscoderJob> jobs = new ArrayList<TranscoderJob>(exJobs.size());
        for (int i = 0; i < exJobs.size(); i++) {
            if (exJobs.get(i).getJobLifeType() == jobLifeType) {
                jobs.add(exJobs.get(i));
            }
        }
        return jobs;
    }

    /**
     * @param jobLifeType {@link TranscoderJob#LONG_LIFE_TYPE_JOB}
     * @return
     */
    public List<TranscoderJob> getBeforeTranscodingJobs(int jobLifeType) {
        return getTranscodingJobs(beforeTranscodingJobs, jobLifeType);
    }

    /**
     * @param jobLifeType {@link TranscoderJob#LONG_LIFE_TYPE_JOB}
     * @return
     */
    public List<TranscoderJob> getAfterTranscodingJobs(int jobLifeType) {
        return getTranscodingJobs(afterTranscodingJobs, jobLifeType);
    }

    /**
     * NOTE: LONG_LIFE_TYPE_JOB can only be done with submitTask
     *
     * @param job
     */
    public void addBeforeTranscodingJob(TranscoderJob job) {
        this.beforeTranscodingJobs.add(job);
    }

    /**
     * NOTE: LONG_LIFE_TYPE_JOB can only be done with submitTask
     *
     * @param job
     */
    public void addAfterTranscodingJob(TranscoderJob job) {
        this.afterTranscodingJobs.add(job);
    }

}
