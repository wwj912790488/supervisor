package com.arcsoft.supervisor.transcoder.spi.single;

import com.arcsoft.supervisor.transcoder.SourceSignal;
import com.arcsoft.supervisor.transcoder.TranscodingInfo;
import com.arcsoft.supervisor.transcoder.spi.single.BinCmd.CmdUnit;
import com.arcsoft.supervisor.transcoder.spi.single.BinCmd.ReqCmd;
import com.arcsoft.supervisor.transcoder.spi.single.BinCmd.ResCmd;
import com.arcsoft.supervisor.transcoder.type.Framerate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;

/**
 * transcoder command
 *
 * @author Bing
 */
public class TransSvrCmd {

    private static final Logger LOG = LoggerFactory.getLogger(TransSvrCmd.class);

    //
    // command code for transcoder
    //
    /**
     * no extra data
     */
    public static final byte CMDCODE_GET_PROGRESS = 1;
    /**
     * no extra data
     */
    public static final byte CMDCODE_GET_FPS = 2;
    /**
     * extra data: path.getByte("UTF-8") + 0x00 0x00 + ushort(width)
     */
    public static final byte CMDCODE_GET_THUMB = 3;
    /**
     * no extra data
     */
    public static final byte CMDCODE_GET_CPU_COUNT = 4;
    /**
     * no extra data
     */
    public static final byte CMDCODE_GET_GPU_COUNT = 5;
    /**
     * no extra data
     */
    public static final byte CMDCODE_GET_CPU_USAGE = 6;
    /**
     * no extra data
     */
    public static final byte CMDCODE_GET_GPU_USAGE = 7;

    /**
     * no extra data
     */
    public static final byte CMDCODE_STOP_TRANCODER = 8;

    /**
     * size=1: 0,close;1:start
     */
    public static final byte CMDCODE_REQ_START_OUTPUT = 9;

    /**
     * extra data size=8
     */
    public static final byte CMDCODE_REQ_SEEK = 10;
    /**
     * size=0
     */
    public static final byte CMDCODE_REQ_PAUSE = 11;
    /**
     * size=0
     */
    public static final byte CMDCODE_REQ_RESUME = 12;
    public static final byte CMDCODE_REQ_TRANSCODING_POSITION = 12;

    /**
     * 8bytes: {0} the same as CMDCODE_STOP_TRANCODER; 1: not destroy graph
     */
    public static final byte CMDCODE_STOP_TRANCODER_EX = 14;

    public static final byte CMDCODE_GET_SIGNAL_STATUS = 15;
    /**
     * 4 bytes, signalmode: 0:master+backup+pad, 1:master+pad, 2:backup+pad, 3:only pad
     */
    public static final byte CMDCODE_SWITCH_SIGNAL = 16;

    public static final byte CMDCODE_REQ_START_TRANCODER = (byte) 126;

    /**
     * The command indicate show or hide the waring border of compose stream screen.
     * The data of this command is a 8 bytes data
     * <ul>
     *     <li>the lower 4 bytes indicate which index(the index is between 0-15)</li>
     *     <li>the higher 4 bytes indicate show or hide the waring border(value 1
     *      indicate show and 0 indicate hide)</li>
     * </ul>
     */
    public static final byte CMDCODE_REQ_WARNING_BORDER = 17;

    /**
     * The response command for warning border.This command do nothing.
     */
    public static final byte CMDCODE_RES_WARNING_BORDER = (byte)145;

    /**
     * The command indicate show or hide the specified message.
     */
    public static final byte CMDCODE_REQ_DISPLAY_MESSAGE = 18;

    public static final byte CMDCOD_REQ_DISPLAY_STYLED_MESSAGE = 27;

    /**
     * The response command for display message.This command do nothing.
     */
    public static final byte CMDCODE_RES_DISPLAY_MESSAGE = (byte) 146;


    /**
     * extra data:
     * input_count = (size_low + size_high*256)/12;
     * for(input_count){Input_idx: 4 byte,Progress 4 byte(%), timeConsumed 4byte}
     */
    public static final byte CMDCODE_RES_PROGRESS = (byte) 129;
    /**
     * extra data: size=4;  (DWORD)actual_fps*100
     */
    public static final byte CMDCODE_RES_FPS = (byte) 130;
    /**
     * extra data: size=0
     */
    public static final byte CMDCODE_RES_THUMB = (byte) 131;
    /**
     * extra data: size=4;  (DWORD)
     */
    public static final byte CMDCODE_RES_CPU_COUNT = (byte) 132;
    /**
     * extra data: size=4;  (DWORD)
     */
    public static final byte CMDCODE_RES_GPU_COUNT = 5;
    /**
     * extra data: size=4;  (DWORD)
     */
    public static final byte CMDCODE_RES_CPU_USAGE = 6;
    /**
     * extra data: size=4;  (DWORD)
     */
    public static final byte CMDCODE_RES_GPU_USAGE = 7;

    /**
     * no extra data
     */
    public static final byte CMDCODE_RES_START_OUTPUT = (byte) 137;

    /**
     * extra data size=0
     */
    public static final byte CMDCODE_RES_SEEK = (byte) 138;
    /**
     * size=0
     */
    public static final byte CMDCODE_RES_PAUSE = (byte) 139;
    /**
     * size=8, ms
     */
    public static final byte CMDCODE_RES_RESUME = (byte) 140;
    /**
     * 8 bytes:pos (ms); 8bytes duration (ms)
     */
    public static final byte CMDCODE_RES_TRANSCODING_POSITION = (byte) 141;

    public static final byte CMDCODE_RES_STOP_EX = (byte) 142;
    /**
     * byte[0]: master signal status, 0:abnormal, 1:normal ;
     * byte[1]: backup signal status, 0:abnormal, 1:normal ;
     * byte[2]: current signal, 1:master signal, 2:backup signal, 3:pad signal ;
     * byte[3]: signal mode, 0:master+backup+pad, 1:master+pad, 2:backup+pad,3:only pad;
     * byte[4]: pad signal status, 0:abnormal, 1:normal , 0xFF;
     * byte[5]~byte[7]: reserved ;
     */
    public static final byte CMDCODE_RSP_SIGNAL_STATUS = (byte) 143;

    /**
     * error size=8
     */
    private static final byte CMDCODE_RES_ERROR = (byte) 253;
    /**
     * extra data: size=8, task status + errorcode
     */
    public static final byte CMDCODE_RES_TASK_STATUS = (byte) 254;

    //
    //error code
    //
    public static final int ERR_TASK_ENDED = 0xFF;

    public static int getErrorCode(ResCmd res) {
        byte[] data = BinCmd.utilGetExtra(res, CMDCODE_RES_ERROR);
        return data == null ? 0 : BinCmd.INT(data, 0);
    }

    /**
     * Get task id from res command
     *
     * @param res
     * @return
     */
    public static int uGetTaskId(ResCmd res) {
        byte[] data = BinCmd.utilGetExtra(res, BinCmd.CMDCODE_SESSION_EXTRA_DATA);
        return BinCmd.INT(data, 4);
    }

    public static int uGetTaskStatus(ResCmd res) {
        byte[] data = BinCmd.utilGetExtra(res, CMDCODE_RES_TASK_STATUS);
        return BinCmd.INT(data, 0);
    }

    public static CmdUnit getThumbnailCmdUnit(String path, int width) {
        byte[] data = null;
        try {
            byte[] barr = path.getBytes("UTF-8");
            data = new byte[barr.length + 4];
            System.arraycopy(barr, 0, data, 0, barr.length);
            data[barr.length] = 0;
            data[barr.length + 1] = 0;
            data[barr.length + 2] = (byte) (width & 0xff);
            data[barr.length + 3] = (byte) ((width >> 8) & 0xff);
        } catch (UnsupportedEncodingException e) {
            LOG.error("", e);
            return null;
        }

        return new CmdUnit(CMDCODE_GET_THUMB, data, 0, data.length);
    }

    /**
     * ResCmd to ti
     *
     * @param ti
     * @param res
     */
    public static void cvtResToTranscodingInfo(TranscodingInfo ti, ResCmd res) {
        Iterator<CmdUnit> iter = res.getResult().iterator();
        while (iter.hasNext()) {
            CmdUnit u = iter.next();
            switch (u.cmdCode) {
                case CMDCODE_RES_PROGRESS:
                    final int SPAN = 12;
                    int idxCount = u.len / SPAN;
                    byte[] data = u.getExtraData();
                    TranscodingInfo.InputProgress[] pgs = null;
                    if (idxCount > 0) {
                        pgs = new TranscodingInfo.InputProgress[idxCount];
                        for (int i = 0; i < idxCount; i++) {
                            //int idx 		= BinCmd.SHORT(data, i*SPAN + 0);
                            //int progflag	= BinCmd.SHORT(data, i*SPAN + 2);
                            int value = BinCmd.INT(data, i * SPAN + 4); //progress
                            int timeConsumed = BinCmd.INT(data, i * SPAN + 8);
                            pgs[i] = new TranscodingInfo.InputProgress(i, value & 0x00000000ffffffffL, timeConsumed, 0);
                            //logger.info(pgs[i]);
                        }
                    }
                    ti.setProgress(pgs);
                    break;
                case CMDCODE_RES_FPS:
                    ti.setFramerate(new Framerate(BinCmd.INT(u.getExtraData(), 0), 100));
                    break;
                case CMDCODE_RES_CPU_COUNT:
                    ti.setCpuCount(BinCmd.INT(u.getExtraData(), 0));
                    break;
                case CMDCODE_RES_GPU_COUNT:
                    ti.setGpuCount(BinCmd.INT(u.getExtraData(), 0));
                    break;
                case CMDCODE_RES_CPU_USAGE:
                    ti.setCpuUsage(BinCmd.INT(u.getExtraData(), 0));
                    break;
                case CMDCODE_RES_GPU_USAGE:
                    ti.setGpuUsage(BinCmd.INT(u.getExtraData(), 0));
                    break;
                default:
                    break;
            }
        }
    }

    public static SourceSignal cvtResToSoureSignal(ResCmd rescmd) {
        SourceSignal ret = null;

        byte[] buf = null;
        for (int i = 0; i < rescmd.getResult().size(); i++) {
            if (rescmd.getResult().get(i).getCmdCode() == TransSvrCmd.CMDCODE_RSP_SIGNAL_STATUS) {
                buf = rescmd.getResult().get(i).getExtraData();
                break;
            }
        }

        // byte[0]: master signal status, 0:abnormal, 1:normal ;
        // byte[1]: backup signal status, 0:abnormal, 1:normal ;
        // byte[2]: current signal, 1:master signal, 2:backup signal, 3:pad signal ;
        // byte[3]: signal mode, 0:master+backup+pad, 1:master+pad, 2:backup+pad,3:only pad;
        // byte[4]: pad signal status, 0:abnormal, 1:normal , 0xFF;
        // byte[5]~byte[7]: reserved ;

        if (buf != null && buf.length >= 5) {
            ret = new SourceSignal(buf[0], buf[1], buf[4], buf[2], buf[3]);
        }

        return ret;
    }

    /**
     * not used in this version
     *
     * @author Bing
     */
    static class CallbackCommand {
        protected ReqCmd req = null;
        protected int errcode = 0;

        public CallbackCommand(ReqCmd req) {
            this.req = req;
        }

        public ReqCmd getReq() {
            return this.req;
        }

        protected void completed(ResCmd rescmd, Object attachment) {
        }

        protected void failed(Throwable ex, Object attachment) {
        }

    }

    static interface ICmdContext {
        ReqCmd getReqCmd();

        boolean needServerResponse();

        boolean isInterrupted();

        void onCmdFinished();

        void setResult(int errorCode, byte[] res);

        int getErrorCode();

        byte[] getResult();
    }

} //~class
