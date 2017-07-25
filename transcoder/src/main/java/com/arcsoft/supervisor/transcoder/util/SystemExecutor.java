package com.arcsoft.supervisor.transcoder.util;


import com.arcsoft.supervisor.transcoder.AppConfig;
import com.arcsoft.supervisor.utils.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.*;
import java.util.concurrent.*;

/**
 * SystemExecutor
 * - ExecutorService
 * - native exe command
 *
 * @author Bing
 */
public class SystemExecutor {
    private static final int DEBUG_MASK_COMMAND = 0x00000001;

    private static final Logger LOG = LoggerFactory.getLogger(SystemExecutor.class);

    private static SystemExecutor inst = new SystemExecutor();

    private static int asyncExecCountLast = 0;
    private static int asyncExecCount = 0;
    private static long asyncExecTimeLast = 0;

    private ExecutorService threadPoolExecutor = Executors.newCachedThreadPool(NamedThreadFactory.create("Transcoder-" +
            "SystemExecutor:threadPoolExecutor"));
    private ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(2, NamedThreadFactory.create(
            "Transcoder-SystemExecutor:scheduledExecutor"
    ));
    private SerialExecutor systemSerialExecutor = new SerialExecutor(new LinkedList<Runnable>());

    /**
     * private constructor
     */
    private SystemExecutor() {
    }

    /**
     * create a serial executor, it will wait the previous to end. then run in background
     *
     * @param queue
     * @return
     */
    public static Executor createSerialExecutor(Queue<Runnable> queue) {
        return new SerialExecutor(queue);
    }

    /**
     * Creates a thread pool that creates new threads as needed, but
     * will reuse previously constructed threads when they are
     * available.  These pools will typically improve the performance
     * of programs that execute many short-lived asynchronous tasks.
     * Calls to <tt>execute</tt> will reuse previously constructed
     * threads if available. If no existing thread is available, a new
     * thread will be created and added to the pool. Threads that have
     * not been used for sixty seconds are terminated and removed from
     * the cache. Thus, a pool that remains idle for long enough will
     * not consume any resources. Note that pools with similar
     * properties but different details (for example, timeout parameters)
     * may be created using {@link java.util.concurrent.ThreadPoolExecutor} constructors.
     *
     * @return the newly created thread pool
     */
    public static ExecutorService getThreadPoolExecutor() {
        return inst.threadPoolExecutor;
    }

    /**
     * NOTE: only short-lived runnable can be executed by ScheduledExecutorService
     * eg. less than 200ms
     *
     * @return
     */
    public static ScheduledExecutorService getScheduledExecutor() {
        return inst.scheduledExecutor;
    }

    /**
     * schedule a long-live runner, eg. greater than 300ms, or runner which life maybe long.
     *
     * @param runner
     * @param delay  ms
     */
    public static void asyncExecute(final Runnable runner, long delay) {
        try {
            if (delay == 0) {
                inst.threadPoolExecutor.execute(runner);
            } else {
                Runnable asyncRunner = new Runnable() {
                    public void run() {
                        getThreadPoolExecutor().execute(runner);
                    }
                };
                inst.scheduledExecutor.schedule(asyncRunner, delay, TimeUnit.MILLISECONDS);
            }
        } catch (Exception e) {
            LOG.info("", e);
        }

        //statistic for debug
        long t = System.currentTimeMillis();
        asyncExecCount++;
        if (t - asyncExecTimeLast > 60000) {
            int c = asyncExecCount - asyncExecCountLast;
            if (c > 300) {
                LOG.info("DEBUG MSG -- TOO MANY call asyncExecute:", c + "/min");
            }
            asyncExecTimeLast = t;
            asyncExecCountLast = asyncExecCount;
        }
    }

    /**
     * use the system's serial executor, wait the previous to end. then run in background
     *
     * @param r
     */
    public static void serialExecute(Runnable r) {
        inst.systemSerialExecutor.execute(r);
    }

    public static final int EXEC_WAIT_TIME_NONE = 0;
    public static final int EXEC_WAIT_TIME_INFINITE = -1;

    /**
     * execute cmd using OS shell: start a shell, and then execute the cmd in it.
     * using in such cases:
     * <p/>
     * <p>     - rm /usr/local/arcsoft/temp/*                                 </p>
     * <p>     - ifconfig eth0|grep eth                                       </p>
     * <p/>
     * <p> in these cases, use the shell is more convenient.
     * the purpose of this method is to reduce OS dependency.</p>
     *
     * @param cmd
     * @return
     */
    public static int osShellExecute(String cmd) {
        return osShellExecute(cmd, null, EXEC_WAIT_TIME_INFINITE);
    }

    public static int osShellExecute(String cmd, Writer out, int maxWaitTime) {
        String[] p;

        if (OSInfo.getOS() == OSInfo.WINDOW) {
            p = new String[]{"cmd", "/C", cmd};
        } else {
            p = new String[]{"sh", "-c", cmd};
        }
        return execOSCommand(true, null, Arrays.asList(p), out, maxWaitTime, true);
    }

    /**
     * exec system script file
     *
     * @param scriptFile
     * @param params
     * @param out
     * @param maxWaitTime
     * @return
     */
    public static int execOSScript(String scriptFile, String params, Writer out, int maxWaitTime) {
        ArrayList<String> cmds = new ArrayList<String>();

        if (OSInfo.getOS() == OSInfo.WINDOW) {
            cmds.add("cmd");
            cmds.add("/C");
        } else {
            cmds.add("sh");
            cmds.add("-c");
        }

        String execStr = scriptFile;
        if (execStr.indexOf(' ') != -1) {
            execStr = '"' + execStr + '"';
        }
        if (params != null)
            execStr = execStr + " " + params;
        int p = scriptFile.lastIndexOf('.');
        String ext = scriptFile.substring(p);
        if (ext.equalsIgnoreCase(".py")) {
            execStr = "python " + execStr;
        } else if (ext.equalsIgnoreCase(".pl")) {
            execStr = "perl " + execStr;
        } else {
            return -1;
        }
        cmds.add(execStr);

        return execOSCommand(true, null, cmds, out, maxWaitTime, true);
    }

    /**
     * sync execute native os command, it will wait util exe end.
     *
     * @param cmd
     * @param out
     * @return
     */
    public static int execOSCommand(String cmd, Writer out) {
        return execOSCommand(true, null, parseCommand(cmd), out, EXEC_WAIT_TIME_INFINITE, true);
    }

    /**
     * @param cmd
     * @param out         out, eg. StringWriter
     * @param maxWaitTime EXEC_WAIT_TIME_NONE,EXEC_WAIT_TIME_INFINITE or other value(ms).
     * @return error code
     */
    public static int execOSCommand(String cmd, Writer out, int maxWaitTime) {
        return execOSCommand(true, null, parseCommand(cmd), out, maxWaitTime, true);
    }

    /**
     * execute os command and wait it end
     *
     * @param p
     * @return
     */
    public static int execOSCommand(String... p) {
        return execOSCommand(true, null, Arrays.asList(p), null, EXEC_WAIT_TIME_INFINITE, true);
    }

    public static int execOSCommand(File workdir, Writer out, String... p) {
        return execOSCommand(true, workdir, Arrays.asList(p), out, EXEC_WAIT_TIME_INFINITE, true);
    }

    /**
     * if timeout, "timeout" will be write to out and return code not 0.
     *
     * @param redirectErr2Out
     * @param workdir
     * @param cmds
     * @param out
     * @param maxWaitTime
     * @return
     */
    public static int execOSCommand(boolean redirectErr2Out,
                                    File workdir, List<String> cmds, final Writer out,
                                    int maxWaitTime, boolean destroyProcessOnTimeout) {
        int ret = -1;
        try {
            if (cmds != null && cmds.size() > 0) {
                ProcessBuilder pb = new ProcessBuilder(cmds);
                pb.environment().put("LANG", "en_US.utf-8");
                if (workdir != null)
                    pb.directory(workdir);
                if (redirectErr2Out)
                    pb.redirectErrorStream(true);
                final Process proc = pb.start();

                Callable<Integer> readstdproc = new Callable<Integer>() {
                    public Integer call() {
                        Integer err = -1;
                        try {
                            InputStreamReader inr = new InputStreamReader(proc.getInputStream());
                            char[] buf = new char[512];
                            int len;
                            while ((len = inr.read(buf)) != -1) {
                                if (out != null) {
                                    out.write(buf, 0, len);
                                    out.flush();
                                }
                            }
                            err = proc.waitFor();
                        } catch (Exception e) {
                            LOG.error(e.getMessage());
                        }
                        return err;
                    }
                };

                Future<Integer> f = inst.threadPoolExecutor.submit(readstdproc);

                Integer rCode = null;

                try {
                    if (maxWaitTime == EXEC_WAIT_TIME_INFINITE)
                        rCode = f.get();
                    else if (maxWaitTime > 0)
                        rCode = f.get(maxWaitTime, TimeUnit.MILLISECONDS);
                    else
                        rCode = 0;
                } catch (TimeoutException e) {
                    if (destroyProcessOnTimeout) {
                        proc.destroy();
                    }
                    if (out != null) {
                        out.write("timeout");
                    }
                    LOG.info("timeout:" + cmds.toString());
                }

                if (rCode != null) {
                    ret = rCode.intValue();
                }

            }

            if ((AppConfig.getDebugMask() & DEBUG_MASK_COMMAND) != 0) {
                String cmdIncs = AppConfig.getProperty("debug_cmd_filter");
                if (cmdIncs != null) {
                    boolean debugOutputCmd = false;
                    String[] ft = cmdIncs.split(",");
                    for (String c : cmds) {
                        for (int j = 0; j < ft.length; j++) {
                            if (c.indexOf(ft[j]) != -1) {
                                debugOutputCmd = true;
                                break;
                            }
                        }
                        if (debugOutputCmd) {
                            LOG.info("cmds:" + cmds);
                            if (out != null)
                                LOG.info("exec result:" + out.toString());
                            break;
                        }
                    }
                }
            }

        } catch (Exception e) {
            LOG.error("Fail to exec: " + cmds + "\t" + e.getMessage());
        }

        return ret;
    }

    /**
     * parse oneline cmd to string list
     *
     * @param cmd
     * @return
     */
    public static List<String> parseCommand(String cmd) {
        List<String> ret = new ArrayList<String>();
        int p = 0;
        for (int i = 0; i < cmd.length(); i++) {
            char c = cmd.charAt(i);
            if (c == ' ') {
                continue;
            } else if (c == '"') {
                ++i;
                p = cmd.indexOf('"', i);
            } else {
                p = cmd.indexOf(' ', i);
            }
            if (p == -1) {
                p = cmd.length();
            }
            ret.add(cmd.substring(i, p));
            i = p;
        }
        return ret;
    }

    public static void destroy() {
        if (inst != null) {
            try {
                inst.scheduledExecutor.shutdown();
            } catch (Exception e) {
                LOG.error("", e);
            }
            try {
                inst.threadPoolExecutor.shutdown();
            } catch (Exception e) {
                LOG.error("", e);
            }
            inst = null;
        }
    }

    private static class SerialExecutor implements Executor {
        private Queue<Runnable> runerQueue;
        private volatile Runnable active = null;

        public SerialExecutor(Queue<Runnable> runerQueue) {
            this.runerQueue = runerQueue;
        }

        private synchronized void executeNext() {
            if ((active = runerQueue.poll()) != null) {
                SystemExecutor.getThreadPoolExecutor().execute(active);
            }
        }

        @Override
        public synchronized void execute(final Runnable command) {
            runerQueue.offer(new Runnable() {
                public void run() {
                    try {
                        command.run();
                    } finally {
                        executeNext();
                    }
                }
            });

            if (active == null) {
                executeNext();
            }
        }
    }
}
