package com.arcsoft.supervisor.transcoder.spi.single;

import com.arcsoft.supervisor.transcoder.*;
import com.arcsoft.supervisor.transcoder.TranscodingInfo.InputProgress;
import com.arcsoft.supervisor.transcoder.Util.TrackerInfoCache;
import com.arcsoft.supervisor.transcoder.spi.single.BinCmd.ReqCmd;
import com.arcsoft.supervisor.transcoder.spi.single.BinCmd.ResCmd;
import com.arcsoft.supervisor.transcoder.spi.single.TransSvrCmd.ICmdContext;
import com.arcsoft.supervisor.transcoder.type.TaskStatus;
import com.arcsoft.supervisor.transcoder.util.DateTimeHelper;
import com.arcsoft.supervisor.transcoder.util.FileUtils;
import com.arcsoft.supervisor.transcoder.util.RegexErrorcode;
import com.arcsoft.supervisor.transcoder.util.SystemExecutor;
import com.arcsoft.supervisor.transcoder.util.errorcode.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;


/**
 * TaskTracker (1)---(1)transcoder.EXE
 *
 * @author Bing
 */
public class TaskTracker extends AbstractTranscodingTracker {

    private static Logger logger = Logger.getLogger(TaskTracker.class);

    private static int DEBUG_MASK = 0x00000001;

    private static class Thumb {
        /**
         * last thumbnail file
         */
        private File lastThumbnailFile = null;
        /**
         * last thumbnail time to get
         */
        long lastThumbnailTime = 0;

        public synchronized File getLastThumbnailFile() {
            return lastThumbnailFile;
        }

        public synchronized void setLastThumbnailFile(File lastThumbnailFile) {
            this.lastThumbnailFile = lastThumbnailFile;
        }
    }

    private final String TASK_RUN_ID = "_" + System.nanoTime();

    //temp file suffix  (temp file format: [0-9a-zA-Z_]*taskid[0-9a-zA-Z_]*SUFFIX )
    private final String SUFFIX_EXCMD = TASK_RUN_ID + "_tt.cmd";
    private final String SUFFIX_TASK_PARAM_FILE = TASK_RUN_ID + "_transcoder_task.xml";
    private final String SUFFIX_THUMB_FILE = TASK_RUN_ID + "_tt.jpg";
    private final String SUFFIX_TASK_INFO_FILE = TASK_RUN_ID + "_info.bin";
    private final String SUFFIX_TASK_LOG_FILE = TASK_RUN_ID + ".log";
    private final String SUFFIX_TASK_STAT_FILE = TASK_RUN_ID + "_transcoder_stat.xml";

    private String contentDetectConfigPath = null;
    private List<String> seiMessageFiles = null;

    /**
     * CONFIG: minimal thumbnail request time interval, if less than the interval, last thumbnail will return
     */
    private int MIN_THUMB_REQ_INTERVAL = 3000; //ms
    private int MIN_PROGRESS_REQ_INTERVAL = 1000; //ms
    /**
     * work directory
     */
    private File workdir;
    private NativeTranscoderProcessClient processClient;
    /**
     * is trans stop by manual
     */
    private volatile int stopFlag = 0;
    /**
     * last thumbnail
     */
    private Hashtable<Integer, Thumb> lastThumbs = new Hashtable<Integer, Thumb>(2, 1);
    private NativeTranscoderProcess proc = null;
    private TranscodingParams taskParam;
    private int debugmask = AppConfig.getDebugMask();
    private boolean canProgressExpInPercent = true;
    private ReentrantLock startStopSeqLock = new ReentrantLock();
    private List<RegexErrorcode> autoRestartErrorCodes = null;
    private volatile boolean isDestroyed = false;

    /**
     * @param transcoder
     * @param taskid
     * @param taskParam  task param without CmdFilePath tag
     */
    protected TaskTracker(Transcoder transcoder, TranscodingKey taskid, int priority, TranscodingParams taskParam) {
        super(transcoder);
        this.taskParam = taskParam;
        this.priority = priority;

        this.workdir = new File(AppConfig.getProperty(AppConfig.KEY_TRANSCODER_WORK_DIR));
        this.taskid = taskid;

        File excmdFile = new File(this.workdir, taskid + SUFFIX_EXCMD);
        this.processClient = new NativeTranscoderProcessClient(taskid, excmdFile);

        Long itv;
        itv = AppConfig.getPropertyAsLong(AppConfig.KEY_MIN_GET_THUMB_INTERVAL);
        if (itv != null && itv.intValue() > 0) {
            MIN_THUMB_REQ_INTERVAL = itv.intValue();
        }
        itv = AppConfig.getPropertyAsLong(AppConfig.KEY_MIN_GET_PROGRESS_INTERVAL);
        if (itv != null && itv.intValue() > 0) {
            MIN_PROGRESS_REQ_INTERVAL = itv.intValue();
        }

        this.isDoOutput = !taskParam.isNoOutputOnInit();

    }

    /**
     * @param workdir
     * @param taskid
     * @param taskParam
     * @param excmdFile
     * @return
     */
    private File getTaskParam(File workdir, Object taskid, TranscodingParams taskParam, File excmdFile) {
        if (taskParam == null)
            return null;

        String taskParamStr = taskParam.getParamAsXml();
        contentDetectConfigPath = getContentDetectConfigPath(taskParamStr);
        seiMessageFiles = getSeiMessageFilesPath(taskParamStr);

        byte[] taskparams;
        try {
            taskparams = composeTranscoderXml(taskParamStr).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e1) {
            return null;
        }

        workdir.mkdirs();
        File fTaskParam = new File(workdir, taskid + SUFFIX_TASK_PARAM_FILE);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fTaskParam);
            fos.write(taskparams);
            fos.flush();
        } catch (Exception e) {
            logger.error(null, e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }

        return fTaskParam;
    }

    private String composeTranscoderXml(String xml) {
        StringBuilder parambuf = new StringBuilder(xml);
        int len;
        for (len = parambuf.length() - 1; len > 0; len--) {
            if (parambuf.charAt(len) == '<')
                break;
        }
        CharSequence endTag = xml.subSequence(len, xml.length());
        parambuf.setLength(len);

        parambuf.append("<CmdFilePath>")
                .append(processClient.excmdFile.getAbsolutePath())
                .append("</CmdFilePath>");
        parambuf.append("<InfoFilePath>")
                .append(new File(workdir, taskid + SUFFIX_TASK_INFO_FILE).getAbsolutePath())
                .append("</InfoFilePath>");
        parambuf.append("<LogFile>")
                .append(new File(workdir, taskid + SUFFIX_TASK_LOG_FILE).getAbsolutePath())
                .append("</LogFile>");
        parambuf.append("<StatFilePath>")
                .append(new File(workdir, taskid + SUFFIX_TASK_STAT_FILE).getAbsolutePath())
                .append("</StatFilePath>");

        parambuf.append("<OutputStream>").append(taskParam.getOutputOption()).append("</OutputStream>");
        parambuf.append("<QuickTranscoder><SlicesCount>")
                .append(AppConfig.getPropertyAsint(AppConfig.QUICK_TRANSCODER_SLICE_COUNT, 4))
                .append("</SlicesCount></QuickTranscoder>");

        parambuf.append(endTag);
        return parambuf.toString();
    }
    
    private String getContentDetectConfigPath(String xmlstr) {
    	String xpathExp = "/TranscoderTask/MultiScreenInputs/ContentDetectXml";
        XPathFactory f = XPathFactory.newInstance();
        XPath p = f.newXPath();
        try {
            return p.evaluate(xpathExp, new InputSource(new StringReader(xmlstr)));
        } catch (Exception e) {
            
        }
        return null;
    }

    private List<String> getSeiMessageFilesPath(String xmlstr) {
        List<String> seiPaths = new ArrayList<String>();
        String xpathExp = "/TranscoderTask/AVSettings/VideoSettings/VideoSetting/SEIInput";
        XPathFactory f = XPathFactory.newInstance();
        XPath p = f.newXPath();
        try {
            NodeList allsetpath = (NodeList)p.evaluate(xpathExp, new InputSource(new StringReader(xmlstr)), XPathConstants.NODESET);
            int count = allsetpath.getLength();
            for(int j = 0;j<count;j++)
            {
                Node seinode = allsetpath.item(j);
                String path = seinode.getFirstChild().getNodeValue();
                if(path !=null && !StringUtils.isBlank(path))
                {
                    seiPaths.add(path);
                }
            }


        } catch (Exception e) {

        }
        return seiPaths;
    }

    private void deleteFile(String path)
    {
        if(path == null && StringUtils.isBlank(path))
            return;

        File file = new File(path);
        try
        {
            if(file.exists())
            {
                file.delete();
            }
        }
        catch (Exception e)
        {

        }
    }

    @Override
    public String toString() {
        return "TaskTracker [taskid=" + taskid + ", startAt=" + startAt + "]";
    }

    @Override
    public TranscodingKey getTranscodingKey() {
        return this.taskid;
    }

    @Override
    public TranscodingParams getTranscodingParams() {
        return this.taskParam;
    }


    /**
     * get progress thumbnail jpeg image
     *
     * @return
     */
    public InputStream getThumbnail(final int width) {
        final String CMDID_THUMB = "thumb";
        if (this.startAt == null || procTranscoderStatus != TRANSCODING_STARTED || stopFlag != 0)
            return null;

        Thumb lastThumb = null;

        //
        //get lastThumb for width
        //
        synchronized (lastThumbs) {
            lastThumb = this.lastThumbs.get(new Integer(width));
            if (lastThumb == null) {
                lastThumb = new Thumb();
                this.lastThumbs.put(new Integer(width), lastThumb);
            }
        }

        synchronized (lastThumbs) {
            //
            //notify to update new thumb if time expired
            //

            long currtime = System.currentTimeMillis();

            if (stopFlag == 0 && !processClient.isCmdInQueue(CMDID_THUMB) &&
                    (lastThumb.getLastThumbnailFile() == null ||
                            currtime - lastThumb.lastThumbnailTime > MIN_THUMB_REQ_INTERVAL)) {
                lastThumb.lastThumbnailTime = currtime;

                logger.debug(" - start to update new thumbnail from transcoder...");

                String prefix;
                if (lastThumb.lastThumbnailFile == null || lastThumb.lastThumbnailFile.getName().startsWith("1_"))
                    prefix = "0_";
                else
                    prefix = "1_";
                //the file name is like: 0_10001_320.jpg
                final File fThumbnail = new File(this.workdir, prefix + taskid + "_" + width + SUFFIX_THUMB_FILE);

                ReqCmd cmd = new ReqCmd();
                cmd.appendCmd(TransSvrCmd.getThumbnailCmdUnit(fThumbnail.getAbsolutePath(), width));

                CmdContext cmdCxt = new CmdContext(cmd) {
                    @Override
                    public void onCmdFinished() {
                        if (this.getErrorCode() == 0 && fThumbnail.length() > 0) {
                            Thumb t = lastThumbs.get(new Integer(width));
                            t.setLastThumbnailFile(fThumbnail);
                            t.lastThumbnailTime = System.currentTimeMillis();
                        }
                        logger.debug((this.getErrorCode() == 0 ? " - end" : " - skip") + " to update new thumbnail " + fThumbnail.getName());
                    }

                    @Override
                    public boolean isInterrupted() {
                        return procTranscoderStatus <= TRANSCODING_NOT_START;
                    }
                };

                this.processClient.execute(CMDID_THUMB, cmdCxt);
            }
        }

        //
        // get thumb from the last cache
        //

        //logger.info((lastThumb.lastThumbnailFile!=null?"have":"no") + " thumb, interval=" + (System.currentTimeMillis() - lastThumb.lastThumbnailTime));

        if (lastThumb.getLastThumbnailFile() != null) {
            try {
                byte[] buf = FileUtils.getFullFile(lastThumb.getLastThumbnailFile());
                return new ByteArrayInputStream(buf);
            } catch (Exception e) {
                logger.error(null, e);
            }
        }

        return null;
    }

    /**
     * trans info filters
     */
    private static byte[] TRANSINFO_FILTERS = new byte[]{
            TransSvrCmd.CMDCODE_GET_PROGRESS,
            TransSvrCmd.CMDCODE_GET_FPS,
            TransSvrCmd.CMDCODE_GET_CPU_COUNT,
            TransSvrCmd.CMDCODE_GET_CPU_USAGE,
            TransSvrCmd.CMDCODE_GET_GPU_COUNT,
            TransSvrCmd.CMDCODE_GET_GPU_USAGE};

    /**
     * cached getTransInfoReqCmd
     */
    private static ReqCmd getTransInfoReqCmd = null;

    /**
     * cache - the last transcodingInfo
     */
    private TranscodingInfo lastTranscodingInfo = null;
    /**
     * cache - the time to set lastTranscodingInfo
     */
    private long lastTranscodingInfoTime = 0;

    /**
     * @param filters filters(ExCmd#CMDCODE_GET_), or null for all
     * @return
     */
    public TranscodingInfo getProgressInfo(byte[] filters) {
        TranscodingInfo ti = null;

        filters = TRANSINFO_FILTERS;

        //ManualStop, then return STOPPING directly
        if (procTranscoderStatus == TRANSCODING_STOPPING) {
            ti = new TranscodingInfo(this.taskid);
            ti.setStartAt(startAt);
            ti.setStatus(TaskStatus.STOPPING);
            return ti;
        }

        //if task ended
        if (procTranscoderStatus < TRANSCODING_STOPPING) {
            ti = new TranscodingInfo(this.taskid);
            ti.setStartAt(startAt);

            TaskStatus st = null;
            if (this.taskParam != null) {
                List<TranscoderJob> jobs = this.taskParam.getAfterTranscodingJobs(TranscoderJob.LONG_LIFE_TYPE_JOB);
                for (TranscoderJob job : jobs) {
                    if (job.isStarted() && !job.isDone()) {
                        st = job.getJobTaskStatus();
                        break;
                    }
                }
            }
            if (st == null)
                st = this.getTranscodeEndState();

            ti.setStatus(st);
            return ti;
        }

        //WAITING, then return WAITING directly
        if (this.startAt == null) {
            ti = new TranscodingInfo(this.taskid);
            TaskStatus st = null;
            List<TranscoderJob> jobs = this.taskParam.getBeforeTranscodingJobs(TranscoderJob.LONG_LIFE_TYPE_JOB);
            for (TranscoderJob job : jobs) {
                if (job.isStarted() && !job.isDone()) {
                    if (TaskStatus.DOWNLOADING.getKey().equals(job.getName())) {
                        st = TaskStatus.DOWNLOADING;
                    }
                    break;
                }
            }
            ti.setStatus(st == null ? TaskStatus.WAITING : st);
            return ti;
        }

        if (procTranscoderStatus == TRANSCODING_SUSPEND) {
            ti = new TranscodingInfo(this.taskid);
            ti.setStartAt(startAt);
            ti.setStatus(TaskStatus.SUSPENDED);
            if (lastTranscodingInfo != null) {
                ti.setProgress(lastTranscodingInfo.getProgress().toArray(new TranscodingInfo.InputProgress[0]));
            }
            return ti;
        }

        //try from cache: check time inteval, make sure 1000ms min request interval
        long t = System.currentTimeMillis();
        if (lastTranscodingInfo != null &&
                (t - this.lastTranscodingInfoTime) < MIN_PROGRESS_REQ_INTERVAL) {
            if ((debugmask & DEBUG_MASK) != 0) {
                logger.info("taskId=" + taskid + " get progress from cache directly.");
            }
            return lastTranscodingInfo;
        }

        //get TranscodingInfo from native transcoder
        ti = new TranscodingInfo(this.taskid);
        ti.setStartAt(startAt);
        ti.setStatus(stopFlag != 0 ? TaskStatus.STOPPING : TaskStatus.RUNNING);
        if (this.lastTranscodingInfo == null) {
            InputProgress[] progs = {new InputProgress(0, this.canProgressExpInPercent ? 0 : 101, 0, 0)};
            ti.setProgress(progs);
        }

        ReqCmd reqCmd;
        if (getTransInfoReqCmd == null) {
            reqCmd = new ReqCmd();
            for (int i = 0; i < filters.length; i++) {
                reqCmd.appendCmd(filters[i], null);
            }
            getTransInfoReqCmd = reqCmd;
        } else {
            reqCmd = getTransInfoReqCmd;
        }

        final TranscodingInfo transcodingInfo = ti;
        CmdContext cmdCxt = new CmdContext(reqCmd) {
            @Override
            public void onCmdFinished() {
                try {
                    if (getErrorCode() == 0) {
                        TransSvrCmd.cvtResToTranscodingInfo(transcodingInfo, new ResCmd(this.getResult()));
                        if (!transcodingInfo.getProgress().isEmpty()) {
                            TranscodingInfo.InputProgress tig = transcodingInfo.getProgress().get(0);
                            tig.setPower(transcodingInfo.getFramerate() == null ? 0 : transcodingInfo.getFramerate().numerator);
                            long tc = (tig.getTimeConsumed() & 0x00FFFFFFFFL);
                            long maxTCU = 0x0ffffffffL / 1000;
                            if (tc >= maxTCU) {
                                tig.setValue(tc / maxTCU * 0x0ffffffffL + tig.getValue());
                            }
                        }
                        lastTranscodingInfo = transcodingInfo;
                        lastTranscodingInfoTime = System.currentTimeMillis();
                    }
                } catch (Exception e) {
                    logger.warn(null, e);
                }
            }

            @Override
            public boolean isInterrupted() {
                return procTranscoderStatus < TRANSCODING_NOT_START;
            }
        };

        final String CMDID_PROGRESS = "progress";
        if (!processClient.isCmdInQueue(CMDID_PROGRESS)) {
            this.processClient.execute(CMDID_PROGRESS, cmdCxt);
        }

        return (this.lastTranscodingInfo != null) ? this.lastTranscodingInfo : ti;
    }

    /**
     * @param notifyListener
     * @param taskParam              params
     * @param taskParamFile          xml file to start native transcoding
     * @param taskid
     * @param priority
     * @return
     */
    protected NativeTranscoderProcess doStartNativeTranscoderProcess(
            IEventNotifyListener notifyListener,
            TranscodingParams taskParam,
            File taskParamFile,
            TranscodingKey taskid,
            int priority) {
        boolean isQuick = (priority == TaskRuntimePrioity.PRIORITY_HIGH);
        return NativeTranscoderProcess.startNativeTranscodingProcess(isQuick,
                taskParamFile, taskid, notifyListener);
    }

    /**
     * Native process runner and stdout tracker
     */
    private final class NativeProcessRunner implements Runnable {
        private NativeTranscoderProcess startNativeprocess() {

            IEventNotifyListener notifyListener = new IEventNotifyListener() {
                @Override
                public void fireTaskStatusChanged(TranscodingKey taskId, TaskStatus status) {
                    taskEventListener.fireTaskStatusChanged(taskId, status);
                }

                @Override
                public void fireTaskErrorMessage(TranscodingKey taskId, int level, int code, String msg) {
                    taskEventListener.fireTaskErrorMessage(taskId, level, code, msg);
                    if (level == ITranscodingMessageListener.LEVEL_ERROR) {
                        onNativeTranscoderError(code);
                    }
                }
            };

//            IMessageNotifyListener commandMessageListener = new IMessageNotifyListener() {
//                @Override
//                public void fireTaskErrorMessage(TranscodingKey taskId, int level, int code, String msg) {
//                    taskEventListener.fireTaskErrorMessage(taskId, level, code, msg);
//                    if (level == ITranscodingMessageListener.LEVEL_ERROR) {
//                        onNativeTranscoderError(code);
//                    }
//                }
//            };
            try {
                File taskParamFile = getTaskParam(workdir, taskid, taskParam, processClient.excmdFile);
                if (taskParamFile != null) {
                    return doStartNativeTranscoderProcess(notifyListener, taskParam, taskParamFile, taskid, priority);
                }
            } catch (Exception e) {
                logger.error(null, e);
            }
            return null;
        }

        @Override
        public void run() {
            int pid = -1;
            proc = startNativeprocess();
            if (proc != null) {
                pid = proc.pid;
                logger.info("start task success, taskid="+taskid +" pid="+pid);
                taskEventListener.fireTaskStatusChanged(taskid, TaskStatus.RUNNING);
                boolean isStarted = proc.waitForStarted(1000);
                if (!isStarted) {
                    logger.error("cannot get __STARTED__ in 1000ms!");
                }
                procTranscoderStatus = TRANSCODING_STARTED;
                if (proc.progressInPercent != null) {
                    canProgressExpInPercent = proc.progressInPercent;
                } else {
                    canProgressExpInPercent = "CORE".equalsIgnoreCase(AppConfig.getProperty(AppConfig.KEY_PRODUCT));
                }

                try {
                    if (!isStarted) {
                        proc.waitForStarted(-1);
                    }
//                    InputStream input = proc.getInputStream();
//                    while (input.read() != -1) {
//                    }
                    NativeTranscoderProcess.ProcessInputStreamLineReader processInputReader = proc.getProcessInputStreamLineReader();
                    while (processInputReader.readLine() != null) {
                        TimeUnit.MILLISECONDS.sleep(10);
                    }
                } catch (EOFException e) {
                } catch (Exception e) {
                    logger.info(null, e);
                }

                logger.info("end of proc input read! taskId=" + taskid);

                try {
                    transtaskExitCode = proc.waitFor(60000);
                    exitErrorDesc = proc.errDesc;
                } catch (Exception e1) {
                    logger.info("transtaskExitCode waitFor Exception. taskId=" + taskid, e1);
                    try {
                        proc.destroy();
                    } catch (Throwable t) {
                        logger.error(null, t);
                    }
                }
            } else {
                transtaskExitCode = ErrorCode.ERR_TASK_FAILED_TO_CREATE_NATIVE_TRANSCODER;
                logger.info("startNativeprocess failed, taskid="+taskid);
            }

            logger.info("taskId=" + taskid + " native transcoder ended, exitcode=" + String.format("0x%08X, pid=%d", transtaskExitCode,pid));

            if (!isDestroyed) {
                onNativeTranscoderEnd();
            }
        }

    }

    /**
     * @return 0, success;  ERR_NOCPU
     */
    protected final int startTask() {
        int ret = 0;
        String errdesc = null;

        startStopSeqLock.lock();
        try {

            try {
                this.taskParam.checkTranscoderXmlReady();
                this.taskParam.initTranscodingJobs(this);

                List<TranscoderJob> jobs = this.taskParam.getBeforeTranscodingJobs(TranscoderJob.SHORT_LIFE_TYPE_JOB);
                for (TranscoderJob job : jobs) {
                    job.run();
                    ret = job.getResult();
                    if (ret != 0) {
                        errdesc = job.getErrorDescription();
                        break;
                    }
                }
            } catch (Exception e) {
                ret = -1;
                errdesc = e.getMessage();
                logger.error(null, e);
            }

            if (ret == 0) {
                procTranscoderStatus = TRANSCODING_STARTING;
                Runnable transcoderRunner = new NativeProcessRunner();
                SystemExecutor.getThreadPoolExecutor().execute(transcoderRunner);
                Date starttime = new Date();
                this.startAt = DateTimeHelper.format(starttime);
            } else {
                this.transtaskExitCode = ret;
                this.exitErrorDesc = errdesc == null ? "start task error." : errdesc;
                try {
                    taskEventListener.fireTaskErrorMessage(taskid, 0, this.transtaskExitCode, this.exitErrorDesc);
                } catch (Exception e1) {
                    logger.error(null, e1);
                }
                onNativeTranscoderEnd();
                ret = 0;
            }
        } catch (Exception e) {
            logger.error(null, e);
        } finally {
            startStopSeqLock.unlock();
        }

        return ret;
    }

    @Override
    public int getStopFlag() {
        return this.stopFlag;
    }

    protected final void stop(int stopFlag) {
        if (this.stopFlag != 0 && procTranscoderStatus <= TRANSCODING_STOPPING)
            return;

        this.stopFlag = stopFlag;

        if ((debugmask & DEBUG_MASK) != 0) {
            String msg = (stopFlag != Transcoder.STOP_FLAG_MANUAL ? "to exit according to native msg." : "to stop.");
            logger.info("taskid=" + taskid + " " + msg);
        }

        startStopSeqLock.lock();
        try {
            logger.info("taskid=" + taskid + " enter stop lock section, to stopping...");

            procTranscoderStatus = TRANSCODING_STOPPING;

            this.taskEventListener.fireTaskStatusChanged(taskid, TaskStatus.STOPPING);

            if (proc == null) {
                this.taskEventListener.fireTaskStatusChanged(taskid,
                        stopFlag == Transcoder.STOP_FLAG_MANUAL ? TaskStatus.CANCELLED
                                : TaskStatus.ERROR);
            } else {
                final int MAX_WAIT_TIME = 40; //s
                final Process theProcess = proc;
                final Object theTaskId = taskid;
                Runnable chkTranscoderEnd = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if ((debugmask & DEBUG_MASK) != 0) {
                                logger.info("taskid=" + theTaskId + " to check native transcoder end, theProcess=" + theProcess);
                            }

                            if (theProcess == null)
                                return;
                            try {
                                int ret = theProcess.exitValue();
                                if ((debugmask & DEBUG_MASK) != 0) {
                                    logger.info("taskid=" + theTaskId + " native transcoder exit in normal state. ret=" + ret);
                                }
                            } catch (Exception e) {
                                theProcess.destroy();
                                logger.info("taskid=" + theTaskId + " native transcoder exit in killed state after " + MAX_WAIT_TIME + "s");
                            }
                        } catch (Exception e) {
                            logger.info(null, e);
                        }
                    }
                };
                SystemExecutor.asyncExecute(chkTranscoderEnd, MAX_WAIT_TIME * 1000);

                try {
                    ReqCmd c = new ReqCmd();

                    if (stopFlag == Transcoder.STOP_FLAG_MANUAL && AppConfig.getPropertyAsint("transcoder.stop_ex", 0) == 1) {
                        c.appendCmd(TransSvrCmd.CMDCODE_STOP_TRANCODER_EX, new byte[]{1});
                    } else {
                        c.appendCmd(TransSvrCmd.CMDCODE_STOP_TRANCODER, null);
                    }

                    this.processClient.clearCmdQueue();
                    this.processClient.execute(TransSvrCmd.CMDCODE_STOP_TRANCODER, new CmdContext(c));
                } catch (Exception e) {
                    logger.trace(null, e);
                }
            }

        } finally {
            startStopSeqLock.unlock();
        }
    }

    @Override
    public int notifyStartOutput(final boolean isOutput) {
        ReqCmd c = new ReqCmd();
        c.appendCmd(TransSvrCmd.CMDCODE_REQ_START_OUTPUT, new byte[]{(byte) (isOutput ? 1 : 0)});

        CmdContext cxt = new CmdContext(c) {
            @Override
            public void onCmdFinished() {
                try {
                    isDoOutput = isOutput;
                } catch (Exception e) {
                    logger.error(null, e);
                }
            }
        };
        this.processClient.execute(TransSvrCmd.CMDCODE_REQ_START_OUTPUT, cxt);
        return 0;
    }

    @Override
    public void destroy() {
        isDestroyed = true;
        if (this.processClient != null) {
            logger.info("taskid=" + taskid + " TaskTracker.destroy");
            this.processClient.destroy();
            this.processClient = null;
        }

        if (proc != null) {
            proc.destroy();
            proc = null;
        }

        if (this.taskParam != null) { //NOTE: taskParam cannot be destroy
            this.taskParam.uninitTranscodingJobs();
        }

        if ((DEBUG_MASK & AppConfig.getDebugMask()) == 0) {
            rmTaskTmpFile(this.workdir, String.valueOf(taskid));
        }
        procTranscoderStatus = TRANSCODING_EXIT;
    }

    @Override
    protected void finalize() throws Throwable {
        destroy();
        super.finalize();
    }


    protected void onNativeTranscoderError(int code) {
        if (this.autoRestartErrorCodes == null) {
            this.autoRestartErrorCodes = new ArrayList<RegexErrorcode>();
            String codes = AppConfig.getProperty("transcoder.error.autoRestartErrorCode");
            try {
                if (codes != null) {
                    String[] codearr = codes.split(",");
                    for (int i = 0; i < codearr.length; i++) {
                        this.autoRestartErrorCodes.add(new RegexErrorcode(codearr[i]));
                    }
                }
            } catch (Exception e) {
                logger.error(null, e);
            }
        }

        for (RegexErrorcode errcode : this.autoRestartErrorCodes) {
            if (errcode.isEqual(code)) {
                this.restartTask = true;
                break;
            }
        }

        if (this.restartTask || AppConfig.getPropertyAsint("transcoder.autoStopOnError", 0) == 1) {
            stop(Transcoder.STOP_FLAG_SYS);
        }
    }

    protected void onNativeTranscoderEnd() {
        endAt = new Date();
        procTranscoderStatus = TRANSCODING_EXIT;

        //alert
        if (transtaskExitCode != 0 && transtaskExitCode < 256 && transtaskExitCode >= -127) {
            taskEventListener.fireTaskErrorMessage(taskid,
                    ITranscodingMessageListener.LEVEL_ERROR,
                    transtaskExitCode,
                    exitErrorDesc == null ? "task process exit." : exitErrorDesc);

            if (AppConfig.getPropertyAsint("transcoder.error.autoRestartOnCrash", 0) == 1) {
                if (this.stopFlag != Transcoder.STOP_FLAG_MANUAL) {
                    this.restartTask = true;
                }
            }
        }

        try {
            int duration = 0;
            if (this.transtaskExitCode == 0) {
                //stat info
                this.transcodeStat = TranscodeStatistic.loadFromFile(new File(workdir, this.taskid + SUFFIX_TASK_STAT_FILE));
                duration = this.transcodeStat.duration;
            }
            this.taskParam.setInputSrcDuration(0, duration);
        } catch (Exception e) {
            logger.error("taskid=" + taskid, e);
        }

        //afer transcoding jobs
        procAfterTranscodingJobs();

        //notify end status
        this.taskEventListener.fireTaskStatusChanged(this.taskid, getTranscodeEndState());
    }

    /**
     * the jobs after transcoding will be exec by sequence
     * transtaskExitCode is the first error code encountered.
     */
    private void procAfterTranscodingJobs() {
        try {

            for (TranscoderJob job : this.taskParam.getAfterTranscodingJobs(TranscoderJob.SHORT_LIFE_TYPE_JOB)) {
                job.run();
                if (job.getResult() != 0) {
                    this.taskEventListener.fireTaskErrorMessage(taskid, 0, job.getResult(), job.getErrorDescription());
                    if (this.transtaskExitCode == 0) {
                        this.transtaskExitCode = job.getResult();
                    }
                }
            }

            for (TranscoderJob job : this.taskParam.getAfterTranscodingJobs(TranscoderJob.LONG_LIFE_TYPE_JOB)) {
                job.run();
                if (job.getResult() != 0) {
                    this.taskEventListener.fireTaskErrorMessage(taskid, 0, job.getResult(), job.getErrorDescription());
                    if (this.transtaskExitCode == 0) {
                        this.transtaskExitCode = job.getResult();
                    }
                }
            }

        } catch (Exception e) {
            logger.trace(null, e);
        }
    }

    /**
     * rm task temp file
     *
     * @param workdir
     * @param id      taskid or null for all
     */
    private void rmTaskTmpFile(final File workdir, final String id) {
        try {
            File[] tmpfs = workdir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return (name.endsWith(SUFFIX_EXCMD)
                            || name.endsWith(SUFFIX_TASK_PARAM_FILE)
                            || name.endsWith(SUFFIX_THUMB_FILE)
                            || name.endsWith(SUFFIX_TASK_LOG_FILE)
                            || name.endsWith(SUFFIX_TASK_INFO_FILE)
                            || name.endsWith(SUFFIX_TASK_STAT_FILE))
                            &&
                            (id == null || name.contains(id));
                }
            });

            if (tmpfs != null) {
                for (int i = 0; i < tmpfs.length; i++) {
                    File file = tmpfs[i];
                    try {
                        file.delete();
                    } catch (Exception e) {
                        logger.error("Fail to del:" + file.getAbsolutePath(), e);
                    }
                }
            }

            deleteFile(contentDetectConfigPath);

            if(seiMessageFiles!=null)
            {
                for (String seipath:seiMessageFiles) {
                    deleteFile(seipath);
                }
            }
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    /**
     * @return position or -1
     */
    @Override
    public long tell() {
        final LinkedBlockingQueue<Long> retQ = new LinkedBlockingQueue<Long>();
        ReqCmd cmd = new ReqCmd();
        cmd.appendCmd(TransSvrCmd.CMDCODE_REQ_TRANSCODING_POSITION, null);
        CmdContext cxt = new CmdContext(cmd) {
            @Override
            public void onCmdFinished() {
                long pos = -1;
                if (this.getErrorCode() == 0 && this.getResult() != null) {
                    ResCmd rescmd = new ResCmd(this.getResult());
                    for (int i = 0; i < rescmd.getResult().size(); i++) {
                        if (rescmd.getResult().get(i).getCmdCode() == TransSvrCmd.CMDCODE_RES_TRANSCODING_POSITION) {
                            byte[] buf = rescmd.getResult().get(i).getExtraData();
                            pos = BinCmd.LONG(buf, 0);
                            break;
                        }
                    }
                }
                try {
                    retQ.put(pos);
                } catch (InterruptedException e) {
                }
            }
        };

        this.processClient.execute(TransSvrCmd.CMDCODE_REQ_TRANSCODING_POSITION, cxt);

        Long ret;
        try {
            ret = retQ.poll(3000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            ret = (long) -1;
        }
        return ret;
    }

    @Override
    public void warnBorder(int index, boolean isShow) {
        byte[] data = new byte[8];
        BinCmd.int32ToBytes(index, data, 0);
        BinCmd.int32ToBytes(isShow ? 1 : 0, data, 4);
        ReqCmd cmd = new ReqCmd();
        cmd.appendCmd(TransSvrCmd.CMDCODE_REQ_WARNING_BORDER, data);
        this.processClient.execute(TransSvrCmd.CMDCODE_REQ_WARNING_BORDER, new CmdContext(cmd));
    }

    @Override
    public void displayMessage(int groupIdx, String message, int width, int height) {
        byte[] messageData = message.getBytes();
        int size = messageData.length + 20 + 1;
        byte[] data = new byte[size];
        BinCmd.int32ToBytes(groupIdx, data, 0); //The index of output group
        BinCmd.int32ToBytes(0, data, 4); //x pos
        BinCmd.int32ToBytes(height - 220, data, 8);   //y pos
        BinCmd.int32ToBytes(width, data, 12); //width
        BinCmd.int32ToBytes(200, data, 16); //height
        System.arraycopy(messageData, 0, data, 20, messageData.length); //The message will be show
        data[size - 1] = 0; //end of 0
        ReqCmd cmd = new ReqCmd();
        cmd.appendCmd(TransSvrCmd.CMDCODE_REQ_DISPLAY_MESSAGE, data);
        this.processClient.execute(TransSvrCmd.CMDCODE_REQ_DISPLAY_MESSAGE, new CmdContext(cmd));
    }

    @Override
    public void displayStyledMessage(int groupIdx, String font, int fontSize, int color, int alpha,
                                     int x, int y, int width, int height, String message) {
        byte[] messageData = message.getBytes();
        int size = messageData.length + 104 + 1;
        byte[] data = new byte[size];
        byte[] fontData = font.getBytes();
        BinCmd.int32ToBytes(groupIdx, data, 0);
        System.arraycopy(fontData, 0, data, 4, fontData.length);
        BinCmd.int32ToBytes(fontSize, data, 68);
        BinCmd.int32ToBytes(color, data, 72);
        BinCmd.int32ToBytes(alpha, data, 76);
        BinCmd.int32ToBytes(0, data, 80);
        BinCmd.int32ToBytes(0, data, 84);
        BinCmd.int32ToBytes(x, data, 88);
        BinCmd.int32ToBytes(y, data, 92);
        BinCmd.int32ToBytes(width, data, 96);
        BinCmd.int32ToBytes(height, data, 100);
        System.arraycopy(messageData, 0, data, 104, messageData.length);
        data[size - 1] = 0;
        ReqCmd cmd = new ReqCmd();
        cmd.appendCmd(TransSvrCmd.CMDCOD_REQ_DISPLAY_STYLED_MESSAGE, data);
        this.processClient.execute(TransSvrCmd.CMDCOD_REQ_DISPLAY_STYLED_MESSAGE, new CmdContext(cmd));
    }

    @Override
    public void reload(String transcoderXml) {
        //////remove temp file when reload
        deleteFile(contentDetectConfigPath);
        if(seiMessageFiles!=null)
        {
            for (String seipath:seiMessageFiles) {
                deleteFile(seipath);
            }
        }

        contentDetectConfigPath = getContentDetectConfigPath(transcoderXml);
        seiMessageFiles = getSeiMessageFilesPath(transcoderXml);

        Path reloadFilePath = Paths.get(workdir.getAbsolutePath(), taskid + SUFFIX_TASK_PARAM_FILE);
        try {
            Files.write(reloadFilePath, composeTranscoderXml(transcoderXml).getBytes());
            proc.sendCommand(NativeTranscoderProcess.Command.RELOAD, reloadFilePath.toFile().getAbsolutePath());
        } catch (IOException e) {
            logger.error("Failed to reload xml for task[" + taskid + "]", e);
        }
    }

    @Override
    public void switchAudio(int videoSettingIndex, int displayIndex) {
        try {
            proc.sendCommand(NativeTranscoderProcess.Command.AUDIO_SWITCH, videoSettingIndex + "-" +displayIndex);
        } catch (IOException e) {
            logger.error("Failed to switch audio with[" + videoSettingIndex + "," + displayIndex + "]");
        }
    }

    @Override
    public Integer getPid() {
        if(proc != null) {
            return proc.pid;
        } else {
            return -1;
        }
    }

    /**
     * @param p
     */
    @Override
    public void seek(long p) {
        byte[] arr = new byte[8];
        BinCmd.int64ToBytes(p, arr, 0);
        ReqCmd cmd = new ReqCmd();
        cmd.appendCmd(TransSvrCmd.CMDCODE_REQ_SEEK, arr);
        this.processClient.execute(TransSvrCmd.CMDCODE_REQ_SEEK, new CmdContext(cmd));
    }

    @Override
    public void pause() {
        ReqCmd cmd = new ReqCmd();
        cmd.appendCmd(TransSvrCmd.CMDCODE_REQ_PAUSE, null);
        CmdContext cxt = new CmdContext(cmd) {
            @Override
            public void onCmdFinished() {
                if (this.getErrorCode() == 0)
                    procTranscoderStatus = TRANSCODING_SUSPEND;
            }
        };
        this.processClient.execute(TransSvrCmd.CMDCODE_REQ_PAUSE, cxt);
    }

    /**
     * @return -1 error, or position
     */
    @Override
    public long resume() {
        final LinkedBlockingQueue<Long> retQ = new LinkedBlockingQueue<Long>();

        ReqCmd cmd = new ReqCmd();
        cmd.appendCmd(TransSvrCmd.CMDCODE_REQ_RESUME, null);

        CmdContext cxt = new CmdContext(cmd) {
            @Override
            public void onCmdFinished() {
                try {
                    long pos = -1;
                    if (this.getErrorCode() == 0) {
                        procTranscoderStatus = TRANSCODING_STARTED;
                        ResCmd rescmd = new ResCmd(this.getResult());
                        for (int i = 0; i < rescmd.getResult().size(); i++) {
                            if (rescmd.getResult().get(i).getCmdCode() == TransSvrCmd.CMDCODE_RES_RESUME) {
                                byte[] buf = rescmd.getResult().get(i).getExtraData();
                                if (buf != null) {
                                    pos = BinCmd.LONG(buf, 0);
                                }
                                break;
                            }
                        }
                    }
                    retQ.put(new Long(pos));
                } catch (Exception e) {
                    try {
                        retQ.put(new Long(-1));
                    } catch (InterruptedException e1) {
                    }
                }
            }
        };

        this.processClient.execute(TransSvrCmd.CMDCODE_REQ_RESUME, cxt);

        Long ret = null;
        try {
            ret = retQ.poll(6000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            ret = (long) -1;
        }
        return ret == null ? -1 : ret;
    }

    private TrackerInfoCache<SourceSignal> cacheSourceSignal = new TrackerInfoCache<SourceSignal>();

    @Override
    public SourceSignal getSourceSignalStatus() {
        SourceSignal lastSourceSignal = null;

        if (cacheSourceSignal.isExpired(MIN_PROGRESS_REQ_INTERVAL)) {
            ReqCmd cmd = new ReqCmd();
            cmd.appendCmd(TransSvrCmd.CMDCODE_GET_SIGNAL_STATUS, null);
            CmdContext cxt = new CmdContext(cmd) {
                @Override
                public void onCmdFinished() {
                    if (this.getErrorCode() == 0 && this.getResult() != null) {
                        cacheSourceSignal.setCache(TransSvrCmd.cvtResToSoureSignal(new ResCmd(this.getResult())));
                    }
                }
            };

            cacheSourceSignal.setActionSubmitTime(System.currentTimeMillis());

            this.processClient.execute(TransSvrCmd.CMDCODE_REQ_TRANSCODING_POSITION, cxt);
        }

        for (int i = 0; i < 5; i++) {
            lastSourceSignal = cacheSourceSignal.getCache();
            if (lastSourceSignal != null)
                break;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                break;
            }
        }

        return lastSourceSignal;
    }

    @Override
    public void switchSignalMode(int signalMode) {
        byte[] extra = new byte[4];
        BinCmd.int32ToBytes(signalMode, extra, 0);
        ReqCmd cmd = new ReqCmd();
        cmd.appendCmd(TransSvrCmd.CMDCODE_SWITCH_SIGNAL, extra);

        CmdContext cxt = new CmdContext(cmd);

        this.processClient.execute(TransSvrCmd.CMDCODE_SWITCH_SIGNAL, cxt);
    }

    /**
     * class CmdAction context
     */
    private class CmdContext implements ICmdContext {
        private int syscmd = 0;
        private ReqCmd reqCmd = null;
        private int errcode = 0;
        private byte[] res = null;

        public CmdContext(ReqCmd reqCmd) {
            this.reqCmd = reqCmd;
        }

        @Override
        public boolean needServerResponse() {
            return !this.reqCmd.containsCmd(TransSvrCmd.CMDCODE_STOP_TRANCODER);
        }

        @Override
        public void setResult(int errorCode, byte[] res) {
            this.errcode = errorCode;
            this.res = res;
        }

        @Override
        public ReqCmd getReqCmd() {
            return this.reqCmd;
        }

        @Override
        public boolean isInterrupted() {
            return proc != null && procTranscoderStatus == TRANSCODING_EXIT;
        }

        @Override
        public void onCmdFinished() {
        }

        @Override
        public String toString() {
            String reqcmdstr = null;
            try {
                reqcmdstr = reqCmd == null ? "null" : reqCmd.toString();
            } catch (Exception e) {
                logger.error("", e);
            }
            return "CmdContext [syscmd=" + syscmd +
                    ", reqCmd=" + reqcmdstr +
                    ", errcode=" + errcode + ", res="
                    + Arrays.toString(res) + "]";
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + errcode;
            result = prime * result
                    + ((reqCmd == null) ? 0 : reqCmd.hashCode());
            result = prime * result + Arrays.hashCode(res);
            result = prime * result + syscmd;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            CmdContext other = (CmdContext) obj;
            if (errcode != other.errcode)
                return false;
            if (reqCmd == null) {
                if (other.reqCmd != null)
                    return false;
            } else if (!reqCmd.equals(other.reqCmd))
                return false;
            if (!Arrays.equals(res, other.res))
                return false;
            if (syscmd != other.syscmd)
                return false;
            return true;
        }

        @Override
        public int getErrorCode() {
            return this.errcode;
        }

        @Override
        public byte[] getResult() {
            return this.res;
        }

    }
}
