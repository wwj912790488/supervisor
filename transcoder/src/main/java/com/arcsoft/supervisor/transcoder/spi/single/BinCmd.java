package com.arcsoft.supervisor.transcoder.spi.single;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * binary command encoding and decoding
 *
 * @author Bing
 */
public class BinCmd {
    private static final Logger LOG = LoggerFactory.getLogger(BinCmd.class);
    private static final byte[] CMD_START_TAG = {(byte) 0xff, (byte) 0xfe, (byte) 0xfd, (byte) 0xfc};

    /**
     * 0x7F - flag for server to end read. no extra data.
     */
    public static final byte CMDCODE_SERVER_END = 127;
    /**
     * 0xFF - flag for client to end read. no extra data.
     */
    public static final byte CMDCODE_CLIENT_END = (byte) 255;

    /**
     * extra data for req/res
     */
    public static final byte CMDCODE_SESSION_EXTRA_DATA = 0;


    /**
     * @param buf
     * @return -1 or pos
     */
    static int utilGetResEndPosition(ByteBuffer buf) {
        int len = (buf.get(0) & 0xFF) + ((buf.get(1) & 0xFF) << 8);
        if (len < 8)
            return -1;

        if (buf.get(len - 3) == (byte) CMDCODE_CLIENT_END)
            return len;
        else
            return -1;
    }

    public static ByteBuffer utilReadCmd(final InputStream inStream) throws IOException {
        ReadableByteChannel ch = new ReadableByteChannel() {
            @Override
            public boolean isOpen() {
                return true;
            }

            @Override
            public void close() throws IOException {
            }

            @Override
            public int read(ByteBuffer dst) throws IOException {
                byte[] buf = new byte[dst.limit() - dst.position()];
                int len = inStream.read(buf);
                if (len > 0) {
                    dst.put(buf, 0, len);
                }
                return len;
            }
        };
        return utilReadCmd(ch);
    }

    /**
     * read command from a channel
     *
     * @param ch
     * @return
     * @throws java.io.IOException
     */
    public static ByteBuffer utilReadCmd(ReadableByteChannel ch) throws IOException {
        ByteBuffer ret = null;
        int readCount = 0;
        byte[] buf = new byte[256];
        ByteBuffer dst = ByteBuffer.wrap(buf);

        readCount = ch.read(dst);
        if (readCount > 0) {
            int len = BinCmd.INT(buf, 0);
            if (len > readCount) {
                byte[] tmp = new byte[len];
                System.arraycopy(buf, 0, tmp, 0, readCount);
                dst = ByteBuffer.wrap(tmp);
                dst.position(readCount);
                readCount += ch.read(dst);
            }
            dst.flip();
            ret = dst;
        }
        return ret;
    }

    public static byte[] utilGetExtra(CmdDecoder cmd, byte cmdCode) {
        for (Iterator<CmdUnit> iter = cmd.getResult().iterator(); iter.hasNext(); ) {
            CmdUnit u = iter.next();
            if (u.getCmdCode() == cmdCode)
                return u.getExtraData();
        }
        return null;
    }

    public static void int64ToBytes(long c, byte[] arr, int offset) {
        arr[0 + offset] = (byte) (c & 0xFF);
        arr[1 + offset] = (byte) ((c >> 8) & 0xFF);
        arr[2 + offset] = (byte) ((c >> 16) & 0xFF);
        arr[3 + offset] = (byte) ((c >> 24) & 0xFF);

        arr[4 + offset] = (byte) ((c >> 32) & 0xFF);
        arr[5 + offset] = (byte) ((c >> 40) & 0xFF);
        arr[6 + offset] = (byte) ((c >> 48) & 0xFF);
        arr[7 + offset] = (byte) ((c >> 56) & 0xFF);
    }

    public static void int32ToBytes(int c, byte[] arr, int offset) {
        arr[0 + offset] = (byte) (c & 0xFF);
        arr[1 + offset] = (byte) ((c >> 8) & 0xFF);
        arr[2 + offset] = (byte) ((c >> 16) & 0xFF);
        arr[3 + offset] = (byte) ((c >> 24) & 0xFF);
    }

    public static long LONG(byte[] arr/*size=8*/, int offset) {
        return ((long) (arr[offset] & 0xFF))
                | ((arr[offset + 1] & 0xFF) << 8)
                | ((arr[offset + 2] & 0xFF) << 16)
                | ((arr[offset + 3] & 0xFF) << 24)
                | ((arr[offset + 4] & 0xFF) << 32)
                | ((arr[offset + 5] & 0xFF) << 40)
                | ((arr[offset + 6] & 0xFF) << 48)
                | ((arr[offset + 7] & 0xFF) << 56);
    }

    /**
     * byte[4] to int (little-endian)
     *
     * @param arr
     * @return
     */
    public static int INT(byte[] arr/*size=4*/, int offset) {
        return (arr[offset] & 0xFF)
                | ((arr[offset + 1] & 0xFF) << 8)
                | ((arr[offset + 2] & 0xFF) << 16)
                | ((arr[offset + 3] & 0xFF) << 24);
    }

    public static short SHORT(byte[] arr/*size=4*/, int offset) {
        return (short) ((arr[offset] & 0xFF) | ((arr[offset + 1] & 0xFF) << 8));
    }

    /**
     * class ReqCmd  - encoding command for client request
     *
     * @author Bing
     */
    public static class ReqCmd extends CmdEncoder {
        /**
         * with no reqId
         */
        ReqCmd() {
            super(CMDCODE_SERVER_END);
        }

        public ReqCmd(int reqId, byte[] extraData) {
            super(reqId, extraData, CMDCODE_SERVER_END);
        }

    }

    /**
     * class ResCmd - decoding response from server
     *
     * @author Bing
     */
    public static class ResCmd extends CmdDecoder {
        public ResCmd(byte[] buf) {
            super(buf, 0, (buf == null ? 0 : buf.length), CMDCODE_CLIENT_END);
        }

        public ResCmd(byte[] buf, int offset, int len) {
            super(buf, offset, len, CMDCODE_CLIENT_END);
        }
    } //~class

    /**
     * class  encoding command
     *
     * @author Bing
     */
    public static class CmdEncoder {
        private List<CmdUnit> cmds = new ArrayList<CmdUnit>();
        private byte endCmdCode;

        /**
         * @param endCmdCode
         */
        CmdEncoder(int endCmdCode) {
            this.endCmdCode = (byte) endCmdCode;
        }

        public CmdEncoder(int reqId, byte[] extraData, int endCmdCode) {
            this.endCmdCode = (byte) endCmdCode;
            if (reqId != 0 || extraData != null) {
                int len = 4 + (extraData == null ? 0 : extraData.length);
                byte[] extra = new byte[len];
                int32ToBytes(reqId, extra, 0);
                if (extraData != null && extraData.length > 0) {
                    System.arraycopy(extraData, 0, extra, 4, extraData.length);
                }
                appendCmd(CMDCODE_SESSION_EXTRA_DATA, extra);
            }
        }

        public int getReqId() {
            int reqId = 0;
            for (int i = 0; i < cmds.size(); i++) {
                if (cmds.get(i).getCmdCode() == CMDCODE_SESSION_EXTRA_DATA) {
                    byte[] buf = cmds.get(i).getExtraData();
                    reqId = INT(buf, 0);
                    break;
                }
            }
            return reqId;
        }

        public void removeCmd(int cmdCode) {
            for (int i = 0; i < cmds.size(); i++) {
                if (cmds.get(i).getCmdCode() == cmdCode) {
                    cmds.remove(i);
                    break;
                }
            }
        }

        public boolean containsCmd(int cmdCode) {
            for (int i = 0; i < cmds.size(); i++) {
                if (cmds.get(i).getCmdCode() == cmdCode) {
                    return true;
                }
            }
            return false;
        }

        public List<CmdUnit> getCmds() {
            return this.cmds;
        }

        public byte[] getCmdContent() {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            //len preserved
            byte[] arr = {0, 0, 0, 0};
            try {
                out.write(arr); //len preserved
            } catch (IOException e) {
                LOG.error("", e);
            }
            //iter cmd
            for (int i = 0; i < cmds.size(); i++) {
                CmdUnit c = cmds.get(i);
                try {
                    c.encode(out);
                } catch (IOException e) {
                    LOG.error("", e);
                }
            }
            //write end
            CmdUnit endu = new CmdUnit((byte) endCmdCode);
            try {
                endu.encode(out);
            } catch (IOException e) {
                LOG.error("", e);
            }

            byte[] ret = out.toByteArray();

            //reset len
            int len = ret.length;
            ret[0] = (byte) (len & 0xFF);
            ret[1] = (byte) ((len >> 8) & 0xFF);
            ret[2] = (byte) ((len >> 16) & 0xFF);
            ret[3] = (byte) ((len >> 24) & 0xFF);

            return ret;
        }

        public void appendCmd(CmdUnit cmd) {
            cmds.add(cmd);
        }

        public void appendCmd(byte cmdCode, byte[] extraData) {
            appendCmd(new CmdUnit(cmdCode, extraData, 0, extraData == null ? 0 : extraData.length));
        }

        @Override
        public String toString() {
            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < this.cmds.size(); i++) {
                if (cmds.get(i).getCmdCode() == CMDCODE_SESSION_EXTRA_DATA) {
                    byte[] data = cmds.get(i).getExtraData();
                    buf.append(" SESSION:").append(INT(data, 0));
                    buf.append(" EXTRA:").append(INT(data, 4));
                } else {
                    buf.append(" CMD:").append(String.format("0x%02X", cmds.get(i).getCmdCode()));
                }
            }
            return buf.toString();
        }

    } //~class

    /**
     * cmd decoding
     *
     * @author Bing
     */
    public static class CmdDecoder {
        private List<CmdUnit> cmds;

        public CmdDecoder(byte[] buf, int offset, int len, int endCmdCode) {
            cmds = new ArrayList<CmdUnit>();

            int p = offset + 4; //skip len
            while (p < len) {
                CmdUnit u = new CmdUnit();
                int b = u.decode(buf, p);
                if (b == -1)
                    break;
                p += b;
                if (u.cmdCode == (byte) endCmdCode)
                    break;
                cmds.add(u);
            }
        }

        public List<CmdUnit> getResult() {
            return this.cmds;
        }

        public int getReqId() {
            int reqId = 0;
            for (int i = 0; i < cmds.size(); i++) {
                if (cmds.get(i).getCmdCode() == CMDCODE_SESSION_EXTRA_DATA) {
                    byte[] buf = cmds.get(i).getExtraData();
                    reqId = INT(buf, 0);
                    break;
                }
            }
            return reqId;
        }

        @Override
        public String toString() {
            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < this.cmds.size(); i++) {
                if (cmds.get(i).getCmdCode() == CMDCODE_SESSION_EXTRA_DATA) {
                    byte[] data = cmds.get(i).getExtraData();
                    buf.append(" SESSION:").append(INT(data, 0));
                    buf.append(" EXTRA:").append(data.length >= 8 ? INT(data, 4) : "");
                } else {
                    buf.append(" CMD CODE:").append(String.format("0x%02X", cmds.get(i).getCmdCode()));
                    buf.append(" EXTRA: ").append(Arrays.toString(cmds.get(i).getExtraData()));
                }
            }
            return buf.toString();
        }
    } //~class

    /**
     * class CmdUnit
     *
     * @author Bing
     */
    public static class CmdUnit {
        byte cmdCode = 0;
        int offset = 0;
        int len = 0;
        byte[] data = null;

        public CmdUnit() {
        }

        public CmdUnit(byte cmdCode) {
            this.cmdCode = cmdCode;
        }

        public CmdUnit(byte cmdCode, byte[] data, int offset, int len) {
            this.cmdCode = cmdCode;
            this.data = data;
            this.len = len;
            this.offset = offset;
        }

        public byte getCmdCode() {
            return cmdCode;
        }

        public byte[] getExtraData() {
            byte[] ret = new byte[len];
            System.arraycopy(data, offset, ret, 0, len);
            return ret;
        }

        /**
         * @param cmd
         * @param offset
         * @return bytes read, or -1 fail
         */
        public int decode(byte[] cmd, int offset) {
            if (cmd[offset + 0] != (byte) 0xff &&
                    cmd[offset + 1] != (byte) 0xfe &&
                    cmd[offset + 2] != (byte) 0xfd &&
                    cmd[offset + 3] != (byte) 0xfc)
                return -1;
            //p= 4; //0x00, reserved for cmd_code extension
            this.cmdCode = cmd[offset + 5]; //cmd code
            this.len = (cmd[offset + 6] & 0x00FF) + ((cmd[offset + 7] & 0x00FF) << 8);
            this.offset = offset + 8;
            this.data = cmd;
            return 8 + len;
        }

        public int encode(OutputStream out) throws IOException {
            out.write(CMD_START_TAG);
            out.write(0x00); //reserved for cmd_code extension
            out.write(cmdCode);
            out.write(len & 0xFF);
            out.write((len >> 8) & 0xFF);
            if (len > 0)
                out.write(data, offset, len);

            return 8 + len;
        }
    } //~class
}
