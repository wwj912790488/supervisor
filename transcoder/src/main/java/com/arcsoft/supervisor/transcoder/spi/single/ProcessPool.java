package com.arcsoft.supervisor.transcoder.spi.single;

import com.arcsoft.supervisor.transcoder.AppConfig;
import com.arcsoft.supervisor.transcoder.util.SystemExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

/**
 * OS level process pool
 *
 * @author Bing
 */
public class ProcessPool {
    private static final Logger LOG = LoggerFactory.getLogger(ProcessPool.class);
    private static final ProcessPool processPool = new ProcessPool();

    private final int maxQueueSize = AppConfig.getPropertyAsint("transcoder.preload.maxCount", 8);
    private final LinkedList<Process> workProcessQueue = new LinkedList<>();
    private ReusedProcessFactory reusedProcessFactory = new ReusedProcessFactory();

    protected ProcessPool() {
    }

    public static ProcessPool getInstance() {
        return processPool;
    }

    public void setReusedProcessFactory(ReusedProcessFactory reusedProcessFactory) {
        this.reusedProcessFactory = reusedProcessFactory;
    }


    /**
     * prepare Process pool
     *
     * @param count
     */
    public void prepareProcesses(int count) {
        SystemExecutor.asyncExecute(new AddBgWorkProc(this, count), 0);
    }

    public void freeProcesses() {
        notifyProcessExit();
    }

    public Process take() {
        Process p;
        synchronized (workProcessQueue) {
            p = workProcessQueue.poll();
            if (workProcessQueue.isEmpty()) {
                SystemExecutor.asyncExecute(new AddBgWorkProc(this, 2), 0);
            }
        }
        if (p == null) {
            try {
                p = reusedProcessFactory.createReusedProcess();
            } catch (IOException e) {
                LOG.error("", e);
            }
        }
        return p;
    }

    public void add(Process p) {
        synchronized (workProcessQueue) {
            if (workProcessQueue.contains(p))
                return;
        }
        if (workProcessQueue.size() > maxQueueSize) {
            p.destroy();
            return;
        }
        synchronized (workProcessQueue) {
            workProcessQueue.add(p);
        }
    }

    private void notifyProcessExit() {
        if (workProcessQueue.isEmpty())
            return;
        final LinkedList<Process> stopProcessQueue = this.workProcessQueue;
        for (final Process p : stopProcessQueue) {
            Runnable r = new Runnable() {
                public void run() {
                    try {
                        NativeTranscoderProcess.notifyNativeProcessExit(p);
                        p.waitFor();
                        workProcessQueue.remove(p);
                    } catch (Exception e) {
                        LOG.error("", e);
                    }
                }
            };
            SystemExecutor.getThreadPoolExecutor().execute(r);
        }
    }

    private static final class AddBgWorkProc implements Runnable {
        ProcessPool processPool;
        int count = 0;

        public AddBgWorkProc(ProcessPool processPool, int count) {
            this.processPool = processPool;
            this.count = count;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < count; i++)
                    processPool.add(processPool.reusedProcessFactory.createReusedProcess());
            } catch (IOException e) {
                LOG.error("", e);
            }
        }
    }

    public static class ReusedProcessFactory {
        public Process createReusedProcess() throws IOException {
            Process proc;
            String TRANSTASK_EXE = AppConfig.getProperty(AppConfig.KEY_TRANSCODER_PATH);
            String[] command = {TRANSTASK_EXE, "-w"};
            if (AppConfig.getPropertyAsint("transcoder.simulator", 0) == 1) {
                proc = NativeTranscoderProcess.createSimulatorProcess(null, true);
            } else {
                ProcessBuilder pb = new ProcessBuilder(command);
                pb.directory(new File(TRANSTASK_EXE).getParentFile());
                proc = pb.start();
            }
            return proc;
        }
    }
}
