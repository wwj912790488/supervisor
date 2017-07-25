package com.arcsoft.supervisor.transcoder.util.errorcode;

public class ErrorCode {
    public static final int OK = 0;
    public static final int ERR_NONE = 0;
    public static final int ERR_UNKNOWN = -1;

    public static final int ERR_TASK_NOT_EXIST = 0x3E8;

    public static final int ERR_CLIP_MAX_REACHED = 0x1001;
    public static final int ERR_QUEUE_MAX_SIZE_REACHED = 0x2001;
    public static final int ERR_START_TASK_FAILED = 0x2002;
    public static final int ERR_TRANSSVR_NOT_STARTED = 0x2003;
    public static final int ERR_SUPER_TASK_IN_RUNNING = 0x2004;
    public static final int ERR_LIVEPORFILE_REFERENCED_BY_AUTOMATION = 0x4001;

    public static final int ERR_FILE_NOT_EXIST = 0x61AE5;
    public static final int ERR_INPUT_READ = ERR_FILE_NOT_EXIST;
    public static final int ERR_OUTPUT_WRITE = 0x61AE6;
    public static final int ERR_CANNOT_GET_PROFILE = 0x61AE7;

    public static final int ERR_START_NATIVE_TRANSCODER = 0x81000000;
    public static final int ERR_TOO_LONG_START_TRANSCODER = 0x81000001;

    /**
     * value cannot be changed, the same return value will be in the native transcoder
     */
    public static final byte ERR_NATIVETRANSCODER_NOCPU = -10; //0xF6

    public static final int ERR_LICENSE_LIMIT = 0x81000100;
    public static final int ERR_LICENSE_LIMIT_TRANSCODER_COUNT = 0x81000101;
    public static final int ERR_LICENSE_LIMIT_OUTGROUP_COUNT = 0x81000102;
    public static final int ERR_LICENSE_LIMIT_HD_COUNT = 0x81000103;
    public static final int ERR_LICENSE_LIMIT_SD_COUNT = 0x81000104;
    public static final int ERR_LICENSE_LIMIT_SDI_COUNT = 0x81000105;
    public static final int ERR_LICENSE_LIMIT_ASI_COUNT = 0x81000106;

    public static boolean inCatExccedLicenseLimit(int err) {
        return ERR_LICENSE_LIMIT == (err & 0xFFFFFF00);
    }

    public static final int TASK_AUTO_RESTART = 0x01000001;

    public static final int ERR_NOCPU = 0x01000200;
    public static final int ERR_NOCPU_TRANSCODER_COUNT = 0x01000201;    //warning
    public static final int ERR_NOCPU_HD_COUNT = 0x01000202;
    public static final int ERR_NOCPU_SD_COUNT = 0x01000203;
    public static final int ERR_NOCPU_SDI_COUNT = 0x01000204;
    public static final int ERR_NOCPU_ASI_COUNT = 0x01000205;
    public static final int ERR_NOCPU_SUPER_TASK_COUNT = 0x01000206;

    public static boolean inCatNoCPU(int err) {
        return ERR_NOCPU == (err & 0xFFFFFF00);
    }

    public static final int ERR_TASK_EXCCED_WAITING_COUNT = 0x01000301;

    public static final int ERR_FTP_UPLOAD_FILE = 0x81000301;
    public static final int ERR_FTP_CONNECT = 0x81000302;
    public static final int ERR_FTP_LOGIN = 0x81000303;
    public static final int ERR_FTP_URL = 0x81000304;

    public static final int ERR_SOURCE = 0x81000400;
    public static final int ERR_SOURCE_INTERRUPT = 0x81000401;
    public static final int ERR_SOURCE_NO_VIDEO = 0x81000403;
    public static final int ERR_SOURCE_NO_AUDIO = 0x81000405;
    public static final int ERR_SOURCE_RESUME = 0x81000407;
    public static final int ERR_SOURCE_PACKAGE_LOST = 0x81000409;

    public static final int ERR_HARDWARE = 0x81000500;
    public static final int ERR_HARDWARE_HIGH_CPU_LOAD = 0x81000501;
    public static final int ERR_HARDWARE_FULL_CPU_LOAD = 0x81000502;
    public static final int ERR_HARDWARE_HIGH_MEMORY_LOAD = 0x81000503;
    public static final int ERR_HARDWARE_FULL_MEMORY_LOAD = 0x81000504;
    /**
     * space used - nealy full
     */
    public static final int ERR_HARDWARE_HIGH_STORAGE_LOAD = 0x81000505;
    /**
     * space used - full
     */
    public static final int ERR_HARDWARE_FULL_STORAGE_LOAD = 0x81000506;
    /**
     * storage not mounted
     */
    public static final int ERR_HARDWARE_STORAGE_LOST = 0x81000507;
    /**
     * storage not work well
     */
    public static final int ERR_HARDWARE_STORAGE_ABNORMAL = 0x81000508;

    public static final int ERR_HARDWARE_HIGH_TRAFFIC_LOAD = 0x81000509;
    public static final int ERR_HARDWARE_FULL_TRAFFIC_LOAD = 0x8100050A;
    public static final int ERR_HARDWARE_HIGH_CPU_TEMPERATURE = 0x8100050B;
    public static final int ERR_HARDWARE_CRIT_CPU_TEMPERATURE = 0x8100050C;
    public static final int ERR_HARDWARE_HIGH_GPU_LOAD = 0x81000511;
    public static final int ERR_HARDWARE_FULL_GPU_LOAD = 0x81000512;
    public static final int ERR_HARDWARE_HIGH_GPU_MEMORY_LOAD = 0x81000513;
    public static final int ERR_HARDWARE_FULL_GPU_MEMORY_LOAD = 0x81000514;
    public static final int ERR_HARDWARE_HIGH_GPU_TEMPERATURE = 0x81000515;
    public static final int ERR_HARDWARE_CRIT_GPU_TEMPERATURE = 0x81000516;

    public static final int ERR_TASK = 0x81000600;
    public static final int ERR_TASK_OPEN_FILE_FAILED = 0x81000601;
    public static final int ERR_TASK_READ_FILE_FAILED = 0x81000602;
    public static final int ERR_TASK_WRITE_FILE_FAILED = 0x81000603;
    public static final int ERR_TASK_FILE_ERROR = 0x81000604;
    public static final int ERR_TASK_FILE_ILLEGAL = 0x81000605;
    public static final int ERR_TASK_FILE_ENCRYPTED = 0x81000606;
    public static final int ERR_TASK_EXECUTION_FAILED = 0x81000607;
    public static final int ERR_TASK_EXECUTION_WARNING = 0x81000608;
    public static final int ERR_TASK_EXECUTION_TIMEOUT = 0x81000609;
    public static final int ERR_TASK_QUEUE_TOO_LONG = 0x8100060A;
    public static final int ERR_TASK_FAILED_TO_CREATE_NATIVE_TRANSCODER = 0x8100060B;
    public static final int ERR_TASK_FIELD_FRAMERATE = 0x8100060C;
    public static final int ERR_TASK_UPLOAD_FILE_FAILED = 0x8100060D;

    public static final int ERR_TASK_UNKNOWN_WARNING = 0x810006FE;
    public static final int ERR_TASK_UNKNOWN = 0x810006FF;

    public static final int TASK_ERROR_MASK = 0x80000000;


    /**
     * class ErrorCodeExcpetion
     */
    public static class ErrorCodeException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        private int errorCode = 0;

        public ErrorCodeException(int errorCode) {
            this.errorCode = errorCode;
        }

        public ErrorCodeException(int errorCode, String message) {
            super(message);
            this.errorCode = errorCode;
        }

        public ErrorCodeException(int errorCode, String message, Throwable t) {
            super(message, t);
            this.errorCode = errorCode;
        }

        public int getErrorCode() {
            return errorCode;
        }
    }

}
