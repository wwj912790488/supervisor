package com.arcsoft.supervisor.transcoder.spi.single;

import com.arcsoft.supervisor.transcoder.AppConfig;
import com.arcsoft.supervisor.transcoder.ITranscodingMessageListener;
import com.arcsoft.supervisor.transcoder.ITranscodingTracker.IEventNotifyListener;
import com.arcsoft.supervisor.transcoder.TranscodingKey;
import com.arcsoft.supervisor.transcoder.Util.TLogger;
import com.arcsoft.supervisor.transcoder.Util.TrackerEventNotifyAdapter;
import com.arcsoft.supervisor.transcoder.type.TaskStatus;
import com.arcsoft.supervisor.transcoder.util.SystemExecutor;
import com.arcsoft.supervisor.utils.app.Environment;
import org.apache.log4j.Logger;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * native transcoder process wrapper
 *
 * @author Bing
 */
public abstract class NativeTranscoderProcess extends Process {
    /**
     * normal startup
     */
    public static final int STARTUP_MODE_NORMAL = 0;
    /**
     * quick startup
     */
    public static final int STARTUP_MODE_QUICK = 1;

    private static Logger logger = Logger.getLogger(NativeTranscoderProcess.class);

    protected Process ownProc = null;
    public Integer pid = -1;
    protected TranscodingKey taskId = null;
    protected boolean isExited = false;
    private IEventNotifyListener notifyListener = null;
    protected Integer errCode = null;
    protected String errDesc = null;

    Boolean progressInPercent = null;
    private LineWatcherInputStream pInput = null;
    private ProcessInputStreamLineReader reader = null;
    private int debugmask = AppConfig.getDebugMask();

    protected NativeTranscoderProcess() {
    }

    static Process createSimulatorProcess(File paramFile, boolean reusedProcessMode) {
        try {
            Class<?> clz = Class.forName("com.arcsoft.supervisor.transcoder.spi.single.TESTnativeTranscoderSimulator");
            Method m = clz.getMethod("exec", String.class, Boolean.TYPE);
            return (Process) m.invoke(null, paramFile == null ? null : paramFile.getAbsolutePath(), reusedProcessMode);
        } catch (Exception e) {
            logger.warn(null, e);
        }
        return null;
    }


    /**
     * __TRANSCODER__$[COMMAND][1]_/usr/test.xml
     * <p/>
     * 1 : command id
     * /usr/test.xml : xml path
     */
//    static final String COMMAND_PREFIX = "__TRANSCODER__$[COMMAND][";
//    static final String COMMAND_EXIT = "2";

    private static final String COMMAND_TEMPLATE_PREFIX = "__TRANSCODER__$[COMMAND][%d]";
    private static final String COMMAND_TEMPLATE_SUFFIX = "_%s";

    public enum Command {
        START(1), STOP(2), RELOAD(3), AUDIO_SWITCH(4);

        final int code;

        Command(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    static ReusedModeTranscoderProcess createReusedModeTranscoderProcess() {
        return Environment.getProfiler().isSartf() ? new SartfReusedModeTranscoderProcess() : new ReusedModeTranscoderProcess();
    }

    static int getPid(Process process) {
        try {
            Class<?> cProcessImpl = process.getClass();
            Field fPid = cProcessImpl.getDeclaredField("pid");
            if (!fPid.isAccessible()) {
                fPid.setAccessible(true);
            }
            return fPid.getInt(process);
        } catch (Exception e) {
            return -1;
        }
    }


    /**
     * @param quickTranscoder
     * @param paramFile
     * @param taskId
     * @param notifyListener
     * @return null: failed to create
     */
    static NativeTranscoderProcess startNativeTranscodingProcess(boolean quickTranscoder,
                                                                 File paramFile, TranscodingKey taskId, IEventNotifyListener notifyListener) {
        NativeTranscoderProcess ret = null;
        Process proc = null;
        /*0: normal; 1:quick start;*/
        final int startupMode = AppConfig.getPropertyAsint("transcoder.startupMode", STARTUP_MODE_NORMAL);
        try {
            if (!quickTranscoder && startupMode != STARTUP_MODE_NORMAL) {
                ret = createReusedModeTranscoderProcess();
                proc = ProcessPool.getInstance().take();
//                String cmdstart = COMMAND_PREFIX + "1]_" + paramFile.getAbsolutePath() + "\n";
                proc.getOutputStream().write(constructCommand(Command.START, paramFile.getAbsolutePath()).getBytes());
                proc.getOutputStream().flush();
            } else {
                ret = new NormalModeTranscoderProcess();
                String TRANSTASK_EXE;
                if (quickTranscoder) {
                    TRANSTASK_EXE = AppConfig.getProperty(AppConfig.KEY_QUICK_TRANSCODER_PATH);
                } else {
                    TRANSTASK_EXE = AppConfig.getProperty(AppConfig.KEY_TRANSCODER_PATH);
                }
                String[] command = {TRANSTASK_EXE, paramFile.getAbsolutePath()};
                if (AppConfig.getPropertyAsint("transcoder.simulator", 0) == 1) {
                    proc = createSimulatorProcess(paramFile, false);
                } else {
                    ProcessBuilder pb = new ProcessBuilder(command);
                    pb.directory(new File(TRANSTASK_EXE).getParentFile());
                    proc = pb.start();
                }
            }
        } catch (Exception e) {
            logger.error(null, e);
        }

        if (proc == null)
            return null;

        ret.ownProc = proc;
        ret.taskId = taskId;
        ret.notifyListener = notifyListener;

        if(proc != null) {
            ret.pid = getPid(proc);
        }

//		if(!ret.waitForStarted(1000)){
//			logger.error("cannot get __STARTED__ in 1000ms!");
//		}

        return ret;
    }

    private Future<Boolean> waitSTARTED = null;

    /**
     * @param timeout -1, forever
     * @return
     */
    boolean waitForStarted(long timeout) {
        boolean ret = true;

        if (waitSTARTED == null) {
            waitSTARTED = SystemExecutor.getThreadPoolExecutor().submit(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    //__STARTED__, __STARTED__S__, __STARTED__%__
                    logger.info("taskid=" + taskId + " wait for __STARTED__ in exe stdout!");
                    try {
//                        while (!((LineWatcherInputStream) getInputStream()).readline().startsWith("__STARTED__")) {
//                        }
                        while (!getProcessInputStreamLineReader().readLine().startsWith("__STARTED__")) {
                        }
                    } catch (EOFException e) {
                        return Boolean.FALSE;
                    } catch (Exception e) {
                        logger.info(null, e);
                        return Boolean.FALSE;
                    }
                    return Boolean.TRUE;
                }
            });
        }

        try {
            if (timeout == -1) {
                ret = waitSTARTED.get();
            } else {
                ret = waitSTARTED.get(timeout, TimeUnit.MILLISECONDS);
            }
            logger.info("waitForStarted end.");
        } catch (TimeoutException e1) {
            ret = false;
        } catch (Exception e) {
            logger.info(null, e);
            ret = false;
        }

        return ret;
    }

    private static String constructCommand(Command command) {
        return constructCommand(command, null);
    }

    private static String constructCommand(Command command, String parameter) {
        String commandTemplate = COMMAND_TEMPLATE_PREFIX;
        if (parameter != null) {
            commandTemplate += COMMAND_TEMPLATE_SUFFIX;
        }
        return String.format(commandTemplate, command.getCode(), parameter) + "\n";
    }


    public void sendCommand(Command command) throws IOException {
        sendCommand(command, null);
    }

    public void sendCommand(Command command, String parameter) throws IOException {
        String c = constructCommand(command, parameter);
        logger.info("Send command [" + c.substring(0, c.length() - 1) + "]");
        ownProc.getOutputStream().write(c.getBytes());
        ownProc.getOutputStream().flush();
    }

    @Override
    public OutputStream getOutputStream() {
        return ownProc.getOutputStream();
    }

    @Override
    public InputStream getInputStream() {
        if (this.pInput == null) {
            this.pInput = new LineWatcherInputStream(ownProc.getInputStream());
        }
        return this.pInput;
    }

    public ProcessInputStreamLineReader getProcessInputStreamLineReader() {
        if (this.reader == null) {
            this.reader = new ProcessInputStreamLineReader(new InputStreamReader(ownProc.getInputStream()));
        }
        return this.reader;
    }

    @Override
    public InputStream getErrorStream() {
        return ownProc.getErrorStream();
    }

    @Override
    public int waitFor() throws InterruptedException {
        int ret = 0;
        while (true) {
            try {
                ret = exitValue();
                break;
            } catch (Exception e) {
                Thread.sleep(100);
            }
        }
        return ret;
    }

    public int waitFor(int maxTime) throws InterruptedException {
        int ret = 0;
        int m = 0;
        while (true) {
            try {
                ret = exitValue();
                break;
            } catch (Exception e) {
                m += 100;
                if (maxTime != -1 && m > maxTime) {
                    throw new InterruptedException();
                }
                Thread.sleep(100);
            }
        }
        return ret;
    }


    @Override
    public void destroy() {
        if (!isExited && (errCode == null || errCode.intValue() == 0)) {
            errCode = -1;
        }
        isExited = true;
        synchronized (this) {
            if (ownProc != null) {
                ownProc.destroy();
            }
        }
    }

    protected abstract void doExited();

    /**
     * NOTE: now only for reused mode
     */
    protected static void notifyNativeProcessExit(Process osProcess) {
//        final String exitcmd = COMMAND_PREFIX + COMMAND_EXIT + "]\n";
        final String exitcmd = constructCommand(Command.STOP);
        try {
            osProcess.getOutputStream().write(exitcmd.getBytes());
            osProcess.getOutputStream().flush();
        } catch (Exception e) {
            logger.error(null, e);
        }
    }


    protected IEventNotifyListener getNotifyListener() {
        if (this.notifyListener == null) {
            this.notifyListener = new TrackerEventNotifyAdapter();
        }
        return this.notifyListener;
    }

    /**
     * __WARNING__$[error code]:error string
     */
    static final String WARNING_PREFIX = "__WARNING__$[";
    /**
     * __ERROR__$[error code]:error string
     */
    static final String ERROR_PREFIX = "__ERROR__$[";

    static int ntParseErrorCode(String line) {
        return Long.decode(line.substring(line.indexOf('[') + 1, line.indexOf(']'))).intValue();
    }

    static String ntParseErrorDesc(String line) {
        return line.substring(line.indexOf(':') + 1);
    }

    private long lastWarningTime = 0;
    private String lastWarningLine = null;
    private long lastErrorTime = 0;
    private String lastErrorLine = null;

    protected void handleOutputLine(String line) {
        if (line == null) {
            return;
        }
        if (line.startsWith("__STOPPED__")) {
            logger.info("taskId=" + taskId + line);
            doExited();
        } else if (line.startsWith("__STARTED__")) {
            logger.info("taskId=" + taskId + line);
            if (progressInPercent == null)
                progressInPercent = !line.startsWith("__STARTED__S__");
        } else if (line.startsWith(WARNING_PREFIX)) {
            logger.info("taskId=" + taskId + line);
            long t = System.currentTimeMillis();
			/* NOT filter, let native transcoder control it.
			if(t - lastWarningTime < 60000 && line.equals(lastWarningLine)){
				return;
			}*/
            lastWarningTime = t;
            lastWarningLine = line;
            int errcode = -1;
            String desc = null;
            try {
                errcode = ntParseErrorCode(line);
                desc = ntParseErrorDesc(line);
            } catch (Exception e) {
                logger.error("taskid=" + taskId, e);
            }
            getNotifyListener().fireTaskErrorMessage(taskId, ITranscodingMessageListener.LEVEL_WARNING, errcode, "[WARNING]" + desc);
        } else if (line.startsWith(NativeTranscoderProcess.ERROR_PREFIX)) {
            logger.info("taskId=" + taskId + line);
            long t = System.currentTimeMillis();
			/* NOT filter, let native transcoder control it.
			if(t - lastErrorTime < 60000 && line.equals(lastErrorLine)){
				return;
			}*/
            lastErrorTime = t;
            lastErrorLine = line;
            int errcode = -1;
            String desc = null;
            try {
                errcode = ntParseErrorCode(line);
                desc = ntParseErrorDesc(line);
            } catch (Exception e) {
                logger.error("taskid=" + taskId, e);
            }
            errCode = errcode;
            errDesc = desc;
            getNotifyListener().fireTaskErrorMessage(taskId, ITranscodingMessageListener.LEVEL_ERROR, errcode, desc);
        } else {
            if ((debugmask & TLogger.DEBUG_MASK_NOUT) != 0) logger.info(line);
        }
    }

    protected class ProcessInputStreamLineReader extends BufferedReader {

        public ProcessInputStreamLineReader(Reader in) {
            super(in);
        }

        @Override
        public String readLine() throws IOException {
            if (NativeTranscoderProcess.this.isExited) {
                throw new EOFException();
            }
            String line = super.readLine();
            NativeTranscoderProcess.this.handleOutputLine(line);
            return line;
        }
    }

    private final class LineWatcherInputStream extends InputStream {
        private InputStream in;
        private byte[] cmdline = new byte[1024];
        private int pCmdline = 0;
        private String linetmp = null;

        public LineWatcherInputStream(InputStream in) {
            this.in = in;
        }

        @Override
        public int available() throws IOException {
            return in.available();
        }

        public String readline() throws IOException {
            linetmp = null;
            do {
                if (-1 == read()) {
                    break;
                }
            } while (linetmp == null);

            return linetmp;
        }

        @Override
        public int read() throws IOException {
            if (isExited) {
                throw new EOFException();
            }
            int c = in.read();
            if (c != -1) {
                cmdline[pCmdline++] = (byte) c;
            }

            if (c == '\n') {
                int len = pCmdline - 1;
                if (len > 0 && cmdline[len - 1] == '\r')
                    len--;
                linetmp = new String(cmdline, 0, len).trim();
                handleOutputLine(linetmp);
                pCmdline = 0;
            } else {
                if (pCmdline >= cmdline.length) {
                    linetmp = new String(cmdline, 0, cmdline.length);
                    pCmdline = 0;
                }
            }

            return c;
        }

    }


    public static class NormalModeTranscoderProcess extends NativeTranscoderProcess {
        @Override
        public int exitValue() {
            this.errCode = this.ownProc.exitValue();
            return this.errCode == null ? 0 : this.errCode;
        }

        @Override
        protected void doExited() {
            isExited = true;
        }
    }

    public static class ReusedModeTranscoderProcess extends NativeTranscoderProcess {
        @Override
        public int exitValue() {
            boolean ownProcExist = true;
            if (this.ownProc != null) {
                try {
                    int c = this.ownProc.exitValue();
                    if (this.errCode == null)
                        this.errCode = c;
                    ownProcExist = false;
                } catch (Exception e) {
                    ownProcExist = true;
                }
            }
            if (ownProcExist && !this.isExited)
                throw new IllegalThreadStateException();
            return this.errCode == null ? 0 : this.errCode;
        }

        @Override
        protected void doExited() {
            isExited = true;
            synchronized (this) {
                ProcessPool.getInstance().add(ownProc);
                ownProc = null;
            }
        }
    }

    /**
     * A process implementation add additional status perform.
     *
     */
    public static class SartfReusedModeTranscoderProcess extends ReusedModeTranscoderProcess {
        @Override
        protected void handleOutputLine(String line) {
            super.handleOutputLine(line);
            if (line.contains("__TRANSCODER_RUNNING__")) {
                logger.info("taskid=" + taskId + line);
                getNotifyListener().fireTaskStatusChanged(taskId, TaskStatus.READY);
            }
        }
    }


}
