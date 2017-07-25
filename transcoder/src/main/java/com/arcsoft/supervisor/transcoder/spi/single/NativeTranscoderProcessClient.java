package com.arcsoft.supervisor.transcoder.spi.single;

import com.arcsoft.supervisor.transcoder.AppConfig;
import com.arcsoft.supervisor.transcoder.TranscodingKey;
import com.arcsoft.supervisor.transcoder.spi.single.TransSvrCmd.ICmdContext;
import com.arcsoft.supervisor.transcoder.util.SystemExecutor;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

class NativeTranscoderProcessClient {
    private Logger logger = Logger.getLogger(NativeTranscoderProcessClient.class);

    private static int DEBUG_MASK_CMD = 0x40000000;
    /**
     * CONFIG: exchange data with exe using file mapping mode or file mode
     */
    private static final boolean EX_MODE_FILEMAPPING = false;
    /**
     * response timeout, default 10s
     */
    private static final int CMD_RES_TIMEOUT = AppConfig.getPropertyAsint("transcoder.res.timeout", 10000);

    //private static final int RES_ERR_CMD_INTERRUPTED = 10;
    private static final int RES_ERR_TASK_RES_TIMEOUT = 12;

    /**
     * cmd queue
     */
    private Queue<Runnable> cmdQueue = new ConcurrentLinkedQueue<Runnable>();
    /**
     * cmd executor
     */
    private Executor cmdExecutor = SystemExecutor.createSerialExecutor(cmdQueue);

    /**
     * task id
     */
    private TranscodingKey taskid;
    /**
     * cmd exchange file
     */
    File excmdFile;
    /**
     * cmd file
     */
    private RandomAccessFile cmdFile = null;
    /**
     * cmd buffer mapped
     */
    private ByteBuffer cmdBuffer = null;
    /**
     * debug mask
     */
    private int debugmask = AppConfig.getDebugMask();


    public NativeTranscoderProcessClient(TranscodingKey taskid, File excmdFile) {
        this.taskid = taskid;
        this.excmdFile = excmdFile;
    }

    private final void loadCmdBuffer() throws IOException {
        if (EX_MODE_FILEMAPPING) {
            if (this.cmdFile == null) {
                this.cmdFile = new RandomAccessFile(this.excmdFile, "rw");
                this.cmdFile.setLength(1024 * 4);
                this.cmdBuffer = this.cmdFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, 1024 * 4).load();
            } else {
                ((MappedByteBuffer) this.cmdBuffer).load();
            }
        } else {
            this.cmdFile = null;
            while (this.cmdFile == null) {
                try {
                    this.cmdFile = new RandomAccessFile(this.excmdFile, "rw");
                } catch (Exception e) {
                    logger.info("Fail to oepn " + this.excmdFile.getName() + "\tError:" + e.getMessage());
                    this.cmdFile = null;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e1) {
                        logger.error(null, e1);
                    }
                }
            }
            if (this.cmdBuffer == null || this.cmdBuffer.capacity() != this.cmdFile.length()) {
                this.cmdBuffer = ByteBuffer.allocate(1024 * 4);
            }
            this.cmdBuffer.clear();
            this.cmdFile.getChannel().read(this.cmdBuffer);
            this.cmdFile.close();
            this.cmdFile = null;
        }
    }

    private final void forceCmdBuffer() throws IOException {
        if (EX_MODE_FILEMAPPING) {
            ((MappedByteBuffer) this.cmdBuffer).force();
        } else {
            this.cmdFile = null;
            while (this.cmdFile == null) {
                try {
                    this.cmdFile = new RandomAccessFile(this.excmdFile, "rw");
                } catch (Exception e) {
                    logger.info("Fail to oepn " + this.excmdFile.getName() + "\tError:" + e.getMessage());
                    this.cmdFile = null;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e1) {
                        logger.error(null, e1);
                    }
                }
            }
            this.cmdBuffer.rewind();
            this.cmdFile.getChannel().write(this.cmdBuffer);
            this.cmdFile.getChannel().force(true);
            this.cmdFile.close();
            this.cmdFile = null;
        }
    }

    private final FileLock lockFile() throws IOException {
        return EX_MODE_FILEMAPPING ? this.cmdFile.getChannel().lock() : null;
    }

    private final void unlockFile(FileLock l) throws IOException {
        if (l != null)
            l.release();
    }


    public boolean isCmdInQueue(String cmdid) {
        for (Iterator<Runnable> iter = this.cmdQueue.iterator(); iter.hasNext(); ) {
            Runnable o = iter.next();
            if (o instanceof CmdAction) {
                CmdAction a = (CmdAction) o;
                String[] ids = a.cmdid.split(",");
                for (int i = 0; i < ids.length; i++) {
                    if (ids[i].equals(cmdid))
                        return true;
                }
            }
        }
        return false;
    }

    public void execute(byte cmd, ICmdContext cxt) {
        this.cmdExecutor.execute(new CmdAction(cmd, cxt));
    }

    public void execute(String cmdids, ICmdContext cxt) {
        this.cmdExecutor.execute(new CmdAction(cmdids, cxt));
    }

    public void clearCmdQueue() {
        this.cmdQueue.clear();
    }

    protected void destroy() {
        if (this.cmdBuffer != null) {
            try {
                this.cmdBuffer.clear();
                while (this.cmdBuffer.hasRemaining()) {
                    this.cmdBuffer.put((byte) 0);
                }
                forceCmdBuffer();
            } catch (Exception e) {
                logger.warn(null, e);
            }
        }

        if (this.cmdFile != null) {
            try {
                this.cmdFile.close();
                this.cmdFile = null;
            } catch (IOException e) {
                logger.warn(null, e);
            }
        }
    }


    private final class CmdAction implements Runnable {
        String cmdid;
        ICmdContext oCmdCxt;

        public CmdAction(byte cmd, ICmdContext oCmdCxt) {
            this(String.valueOf(cmd), oCmdCxt);
        }

        public CmdAction(String cmdids, ICmdContext oCmdCxt) {
            this.cmdid = cmdids;
            this.oCmdCxt = oCmdCxt;
        }

        private void sendReqCmd() throws IOException {
            FileLock flock = lockFile();
            try {
                //write cmd to cmd buffer
                if (cmdBuffer == null) {
                    loadCmdBuffer();
                }
                cmdBuffer.clear();
                cmdBuffer.put(oCmdCxt.getReqCmd().getCmdContent());
                while (cmdBuffer.hasRemaining()) {
                    cmdBuffer.put((byte) 0);
                }
                if (!EX_MODE_FILEMAPPING)
                    forceCmdBuffer();
            } finally {
                unlockFile(flock);    //notify trans task
            }
        }

        private void waitCmdRes() throws IOException {
            if (oCmdCxt.needServerResponse()) {
                lockFile();
                //check result, if not valid, continue to wait till RESTIMEOUT
                if (!EX_MODE_FILEMAPPING)
                    loadCmdBuffer();

                boolean isTimeout = false;
                long t = 0;
                int resEndPos = -1;
                while (!oCmdCxt.isInterrupted() && (resEndPos = BinCmd.utilGetResEndPosition(cmdBuffer)) == -1) {
                    if (t > CMD_RES_TIMEOUT) {
                        isTimeout = true;
                        break;
                    }
                    t += 500;
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        break;
                    }
                    if (!EX_MODE_FILEMAPPING)
                        loadCmdBuffer();
                }

                //set res
                if (isTimeout) {
                    oCmdCxt.setResult(RES_ERR_TASK_RES_TIMEOUT, null);
                    if ((debugmask & DEBUG_MASK_CMD) != 0) {
                        logger.info("taskid=" + taskid + " ... read cmd response time out!");
                    }
                } else {
                    if (resEndPos > 0) {
                        byte[] taskres = new byte[resEndPos];
                        try {
                            cmdBuffer.rewind();
                            cmdBuffer.get(taskres);
                            oCmdCxt.setResult(0, taskres);
                        } catch (Exception e) {
                            logger.error("" + taskid, e);
                            oCmdCxt.setResult(-1, null);
                        }
                    }

                    if ((debugmask & DEBUG_MASK_CMD) != 0) {
                        logger.info("taskid=" + taskid + " ... Got response, end position=" + resEndPos);
                    }
                }
            }

        }

        @Override
        public void run() {
            try {
                if (oCmdCxt == null || oCmdCxt.getReqCmd() == null || oCmdCxt.isInterrupted())
                    return;

                if ((debugmask & DEBUG_MASK_CMD) != 0) {
                    logger.info("taskid=" + taskid + oCmdCxt.toString());
                }
                sendReqCmd();
                waitCmdRes();
            } catch (Exception e) {
                logger.info(null, e);
            } finally {
                oCmdCxt.onCmdFinished();
            }
        }

    }//~class CmdAction

}
