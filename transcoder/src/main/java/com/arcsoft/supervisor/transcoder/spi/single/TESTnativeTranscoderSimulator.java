package com.arcsoft.supervisor.transcoder.spi.single;

import com.arcsoft.supervisor.transcoder.spi.single.BinCmd.CmdDecoder;
import com.arcsoft.supervisor.transcoder.spi.single.BinCmd.CmdEncoder;
import com.arcsoft.supervisor.transcoder.spi.single.BinCmd.CmdUnit;
import com.arcsoft.supervisor.utils.NamedThreadFactory;
import org.apache.log4j.Logger;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * nativeTranscoderSimulator
 * <p/>
 * exec params:  taskxml
 *
 * @author Bing
 */
final class TESTnativeTranscoderSimulator extends Process implements Runnable {
//    static boolean OUTPUT = "1".equals(System.getenv("sim.output"));
    static boolean OUTPUT = true;
    static ExecutorService executor = Executors.newCachedThreadPool(NamedThreadFactory.create("Transcoder-" +
            "TESTnativeTranscoderSimulator:executor"));
    private static Logger logger = Logger.getLogger(TESTnativeTranscoderSimulator.class);
    /**
     * __WARNING__$[error code]:error string
     */
    static final String WARNING_PREFIX = "__WARNING__$[";
    /**
     * __ERROR__$[error code]:error string
     */
    static final String ERROR_PREFIX = "__ERROR__$[";

    boolean reusedProcessMode = false;

    private PipedOutputStream out = new PipedOutputStream();
    private PipedInputStream ins = new PipedInputStream();
    private PipedInputStream procInput = null;
    private PipedOutputStream procOutput = null;

    private String taskxml;
    int totalSteps = 100;
    Future<?> waitret = null;
    boolean toDestroy = false;

    private Integer exitCode = null;

    TESTnativeTranscoderSimulator(String taskxml) {
        this.taskxml = taskxml;
        if (procInput == null) {
            try {
                procInput = new PipedInputStream(this.out);
            } catch (IOException e) {
                logger.error(null, e);
            }
        }
        if (procOutput == null) {
            try {
                procOutput = new PipedOutputStream(this.ins);
            } catch (IOException e) {
                logger.error(null, e);
            }
        }
    }

    void output(String s) {
        System.out.println(s);
        try {
            out.write(s.getBytes());
            out.write('\n');
        } catch (Exception e) {
            logger.error(null, e);
        }
    }

    static void writeThumb(File t) {
        try {
            String src = t.getName().indexOf("0_") != -1 ? "/usr/thumb0.jpg" : "/usr/thumb1.jpg";
            FileOutputStream fos = new FileOutputStream(t);
            fos.write(getFullFile(new File(src)));
            fos.close();
        } catch (Exception e) {
            logger.error(null, e);
        }
    }

    int GetReqEndPosition(byte[] buf) {
        if (buf == null || buf.length == 0)
            return -1;
        int len = (buf[0] & 0xFF) + ((buf[1] & 0xFF) << 8);
        if (len < 8 || len > buf.length) {
            output("sim.GetReqEndPosition: cannot get end in buf, len=" + len + ", buf.length=" + buf.length);
            return -1;
        }

        if (buf[len - 3] == (byte) BinCmd.CMDCODE_SERVER_END)
            return len;
        else
            return -1;
    }

    void genOutputs() throws IOException {
        NativeTranscodingParams p = new NativeTranscodingParams(this.taskxml);
        int count = p.getOutputGroupCount();
        for (int i = 0; i < count; i++) {
            String t = NativeTranscodingParams.uGetValInXml(this.taskxml, "/TranscoderTask/OutputGroups/OutputGroup[@idx=" + i + "]/OutputType");
            if ("FileArchive".equalsIgnoreCase(t)) {
                String dst = NativeTranscodingParams.uGetValInXml(this.taskxml, "/TranscoderTask/OutputGroups/OutputGroup[@idx=" + i + "]/TargetPath");
                File fdst = new File(dst);
                if (fdst.createNewFile()) {
                    RandomAccessFile fa = new RandomAccessFile(fdst, "rw");
                    fa.setLength(10 * 1024 * 1024);
                    fa.close();
                }
            }
        }
    }

    private void exec() throws Exception {
        File cmdFile = new File(getCmdFilePath(taskxml));
        for (int i = 0; i< 100; i++){
            if (OUTPUT) output(WARNING_PREFIX + "0x00010001" + "]:this is test warning message.");
            if (OUTPUT) output(ERROR_PREFIX + "0x80090001" + "]:this is test error message.");
        }

        output("__STARTED__");
        try {
            long t0 = System.currentTimeMillis();
            int step = 0;
            while (step++ < totalSteps && !toDestroy) {
                byte[] cmdbuf = null;

                while (cmdbuf == null && !toDestroy) {
                    cmdbuf = getFullFile(cmdFile);
                    if (cmdbuf == null)
                        Thread.sleep(50);
                }

                byte[] buf;
                int len;
                if ((len = GetReqEndPosition(cmdbuf)) != -1) {
                    //output("sim cmdbuf.len=" + len + ",step="+step);
                    CmdDecoder dec = new CmdDecoder(cmdbuf, 0, len, BinCmd.CMDCODE_SERVER_END);
                    if (OUTPUT) output("sim got:" + dec.toString());
                    CmdEncoder enc = new CmdEncoder(dec.getReqId(), null, BinCmd.CMDCODE_CLIENT_END);
                    for (CmdUnit u : dec.getResult()) {
                        switch (u.getCmdCode()) {
                            case TransSvrCmd.CMDCODE_GET_PROGRESS:
                                buf = new byte[12];
                                BinCmd.int32ToBytes(0, buf, 0);
                                BinCmd.int32ToBytes(step * 100 / totalSteps, buf, 4);
                                BinCmd.int32ToBytes((int) (System.currentTimeMillis() - t0), buf, 8);
                                enc.appendCmd(TransSvrCmd.CMDCODE_RES_PROGRESS, buf);
                                break;
                            case TransSvrCmd.CMDCODE_GET_FPS:
                                buf = new byte[4];
                                BinCmd.int32ToBytes(10 * 100, buf, 0);
                                enc.appendCmd(TransSvrCmd.CMDCODE_RES_FPS, buf);
                                break;
                            case TransSvrCmd.CMDCODE_GET_THUMB:
                                writeThumb(new File(new String(u.getExtraData(), 0, u.getExtraData().length - 4, "UTF-8")));
                                enc.appendCmd(TransSvrCmd.CMDCODE_RES_THUMB, null);
                                break;
                            case TransSvrCmd.CMDCODE_GET_SIGNAL_STATUS:
                                buf = new byte[8];
                                buf[2] = 1;
                                enc.appendCmd(TransSvrCmd.CMDCODE_RSP_SIGNAL_STATUS, buf);
                                break;
                            case TransSvrCmd.CMDCODE_STOP_TRANCODER:
                                output("got CMDCODE_STOP_TRANCODER and to exit!");
                                exitCode = 0;
                                return;
                            default:
                                break;
                        }
                    }

                    FileOutputStream fos = null;
                    while (fos == null && !toDestroy) {
                        try {
                            fos = new FileOutputStream(cmdFile);
                        } catch (Exception e) {
                        }
                        if (fos == null && !toDestroy)
                            Thread.sleep(50);
                    }
                    if (fos != null) {
                        fos.write(enc.getCmdContent());
                        fos.close();
                    }
                }

                if (!toDestroy)
                    Thread.sleep(600);

                if ((step % 10) == 0) {
                    if (OUTPUT)
                        output(WARNING_PREFIX + (step < 20 ? "0x00010001" : "0x00010002") + "]:this is test warning message.");
                }
                if (step > 0 && (step % 20) == 0) {
                    if (OUTPUT) output(ERROR_PREFIX + "0x80090001" + "]:this is test error message.");
                }
            } //~while

            try {
                genOutputs();
            } catch (Exception e) {
                logger.error(null, e);
            }

        } finally {
            output("__STOPPED__");
        }
    }

    @Override
    public void run() {
        while (!toDestroy) {
            String taskxmlFilename = null;
            if (reusedProcessMode) {
                try {
                    final String prefix = "__TRANSCODER__$[COMMAND][1]_";
                    BufferedReader bis = new BufferedReader(new InputStreamReader(ins));
                    while ((taskxmlFilename = bis.readLine()) != null) {
                        if (taskxmlFilename.startsWith(prefix)) {
                            taskxmlFilename = taskxmlFilename.substring(prefix.length());
                            break;
                        }
                    }
                    this.taskxml = new String(getFullFile(new File(taskxmlFilename)), "UTF-8");
                    output("command start task:" + taskxmlFilename);
                } catch (Exception e) {
                    logger.error(null, e);
                }
            }

            try {
                exec();
            } catch (Exception e) {
                logger.error(null, e);
            }

            if (!reusedProcessMode) {
                break;
            }
        }
        exitCode = 0;
        try {
            this.out.flush();
            this.out.close();
        } catch (Exception e) {
            logger.error(null, e);
        }
    }


    @Override
    public OutputStream getOutputStream() {
        return procOutput;
    }

    @Override
    public InputStream getInputStream() {
        return procInput;
    }

    @Override
    public InputStream getErrorStream() {
        return null;
    }

    @Override
    public int waitFor() throws InterruptedException {
        try {
            this.waitret.get();
        } catch (ExecutionException e) {
            logger.error(null, e);
        }
        return this.exitCode;
    }

    @Override
    public int exitValue() {
        if (this.exitCode == null)
            throw new IllegalThreadStateException();
        return exitCode;
    }

    @Override
    public void destroy() {
        toDestroy = true;
    }

    static byte[] getFullFile(File f) {
        if (f == null)
            return null;
        byte[] buf = null;
        FileInputStream fis = null;
        try {
            buf = new byte[(int) f.length()];
            fis = new FileInputStream(f);
            fis.read(buf);
        } catch (Exception e1) {
            buf = null;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                }
            }
        }
        return buf;
    }

    private static String getCmdFilePath(String taskxml) {
        XPathFactory f = XPathFactory.newInstance();
        XPath p = f.newXPath();
        try {
            return p.evaluate("/TranscoderTask/CmdFilePath", new InputSource(new StringReader(taskxml)));
        } catch (Exception e) {
            logger.error(null, e);
        }
        return null;
    }

    public static TESTnativeTranscoderSimulator exec(String taskxmlFilename, boolean reusedProcessMode) throws Exception {
        String taskxml = null;
        if (taskxmlFilename != null)
            taskxml = new String(getFullFile(new File(taskxmlFilename)), "UTF-8");
        TESTnativeTranscoderSimulator sim = new TESTnativeTranscoderSimulator(taskxml);
        sim.reusedProcessMode = reusedProcessMode;
        sim.waitret = executor.submit(sim);
        return sim;
    }

    public static void main(String[] args) {
        try {
            TESTnativeTranscoderSimulator sim = exec(args[0], false);
            sim.waitFor();
        } catch (Exception e) {
            logger.error(null, e);
        }
        System.exit(0);
    }

}
