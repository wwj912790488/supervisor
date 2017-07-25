package com.arcsoft.supervisor.transcoder;

import com.arcsoft.supervisor.transcoder.util.errorcode.ErrorCode;


/**
 * default limit checker, you can subclass it,
 * then reset it by call {@link Transcoder#setLimitChecker(com.arcsoft.supervisor.transcoder.LimitChecker)}
 *
 * @author Bing
 */
public class LimitChecker {
    /**
     * transcoder limit, default use TranscoderLimit
     */
    protected TranscoderLimit transcoderLimit;
    /**
     * transcoder
     */
    protected Transcoder transcoder;

    protected int maxOutputGroup;
    protected int maxHDCount;
    protected int maxSDCount;
    protected int maxTranscoderCount;
    protected int maxSDICount;
    protected int maxASICount;
    protected int maxSupperTaskCount;

    protected LimitChecker() {
    }

    /**
     * @param transcoder
     */
    LimitChecker(Transcoder transcoder) {
        this(transcoder, new TranscoderLimit());
    }

    /**
     * @param transcoder
     * @param transcoderLimit
     */
    public LimitChecker(Transcoder transcoder, TranscoderLimit transcoderLimit) {
        this.transcoder = transcoder;
        this.transcoderLimit = transcoderLimit;
    }

    protected int doCheckOneTaskLimit(ITranscodingTracker toDo, StringBuilder outErrDesc) {

        int todoOutGroupCount = toDo.getTranscodingParams().getOutputGroupCount();
        if (todoOutGroupCount > maxOutputGroup) {
            if (outErrDesc != null) {
                outErrDesc.append("outputGroupCount " + todoOutGroupCount);
                outErrDesc.append(" > maxOutputGroup " + maxOutputGroup);
            }
            return ErrorCode.ERR_LICENSE_LIMIT_OUTGROUP_COUNT;
        }

        if (toDo.getTranscodingParams().getHDOuputCount() > maxHDCount) {
            if (outErrDesc != null) {
                outErrDesc.append("HD count " + toDo.getTranscodingParams().getHDOuputCount());
                outErrDesc.append(" > maxHDCount " + maxHDCount);
            }
            return ErrorCode.ERR_LICENSE_LIMIT_HD_COUNT;
        }

        if (toDo.getTranscodingParams().getSDOuputCount() > maxSDCount) {
            if (outErrDesc != null) {
                outErrDesc.append("SD count " + toDo.getTranscodingParams().getSDOuputCount());
                outErrDesc.append(" > maxSDCount " + maxSDCount);
            }
            return ErrorCode.ERR_LICENSE_LIMIT_SD_COUNT;
        }

        return ErrorCode.ERR_NONE;
    }

    protected int doCheckRunningTaskLimit(ITranscodingTracker toDo, StringBuilder outErrDesc) {
        int ret = 0;

        if (transcoder.getRunningTaskTracker().size() < 1) {
            return ErrorCode.ERR_NONE;
        }

        int iTaskCount = 0;
        int iHDOuputCount = 0;
        int iSDOuputCount = 0;
        int iSDI = 0;
        int iASI = 0;
        int iSuper = 0;

        ITranscodingTracker[] trackers = transcoder.getAllTranscodingTrackers();

        if (toDo != null && transcoder.trackers.containsKey(toDo.getTranscodingKey())) {
            iTaskCount = transcoder.getRunningTaskCount();
            iHDOuputCount = getHDOuputCount(trackers, ITranscodingTracker.TRANSCODING_STARTING);
            iSDOuputCount = getSDOuputCount(trackers, ITranscodingTracker.TRANSCODING_STARTING);
            iSDI = getSDITaskCount(trackers, ITranscodingTracker.TRANSCODING_STARTING);
            iASI = getASITaskCount(trackers, ITranscodingTracker.TRANSCODING_STARTING);
            iSuper = getSupperTaskCount(trackers, ITranscodingTracker.TRANSCODING_STARTING);
        } else {
            iTaskCount = transcoder.trackers.size();
            iHDOuputCount = getHDOuputCount(trackers, ITranscodingTracker.TRANSCODING_NOT_START);
            iSDOuputCount = getSDOuputCount(trackers, ITranscodingTracker.TRANSCODING_NOT_START);
            iSDI = getSDITaskCount(trackers, ITranscodingTracker.TRANSCODING_NOT_START);
            iASI = getASITaskCount(trackers, ITranscodingTracker.TRANSCODING_NOT_START);
            iSuper = getSupperTaskCount(trackers, ITranscodingTracker.TRANSCODING_NOT_START);
        }

        if (toDo == null) { //used by temobi to check whether server is busy
            if (iTaskCount >= maxTranscoderCount)
                ret = ErrorCode.ERR_NOCPU_TRANSCODER_COUNT;
            else if (iHDOuputCount >= maxHDCount)
                ret = ErrorCode.ERR_NOCPU_HD_COUNT;
            else if (iSDOuputCount >= maxSDCount)
                ret = ErrorCode.ERR_NOCPU_SD_COUNT;
        } else {
            int todoHD = toDo.getTranscodingParams().getHDOuputCount();
            int todoSD = toDo.getTranscodingParams().getSDOuputCount();
            int todoSDI = isSDIInput(toDo.getTranscodingParams()) ? 1 : 0;
            int todoASI = isASIInput(toDo.getTranscodingParams()) ? 1 : 0;
            int todoSuper = toDo.getPriority() == TaskRuntimePrioity.PRIORITY_HIGH ? 1 : 0;

            if (iTaskCount >= maxTranscoderCount) {
                ret = ErrorCode.ERR_NOCPU_TRANSCODER_COUNT;
                errRunningTaskLimit(ret, outErrDesc, iTaskCount, 1);
            } else if (todoHD > 0 && iHDOuputCount + todoHD > maxHDCount) {
                ret = ErrorCode.ERR_NOCPU_HD_COUNT;
                errRunningTaskLimit(ret, outErrDesc, iHDOuputCount, todoHD);
            } else if (todoSD > 0 && iSDOuputCount + todoSD > maxSDCount) {
                ret = ErrorCode.ERR_NOCPU_SD_COUNT;
                errRunningTaskLimit(ret, outErrDesc, iSDOuputCount, todoSD);
            } else if (todoSDI > 0 && iSDI + todoSDI > maxSDICount) {
                ret = ErrorCode.ERR_NOCPU_SDI_COUNT;
                errRunningTaskLimit(ret, outErrDesc, iSDI, todoSDI);
            } else if (todoASI > 0 && iASI + todoASI > maxASICount) {
                ret = ErrorCode.ERR_NOCPU_ASI_COUNT;
                errRunningTaskLimit(ret, outErrDesc, iASI, todoASI);
            } else if (todoSuper > 0 && iSuper + todoSuper > maxSupperTaskCount) {
                ret = ErrorCode.ERR_NOCPU_SUPER_TASK_COUNT;
                errRunningTaskLimit(ret, outErrDesc, iSuper, todoSuper);
            }
        }

        return ret;
    }

    private void errRunningTaskLimit(int err, StringBuilder out, int iCurr, int iTodo) {
        if (err == ErrorCode.ERR_NONE || out == null)
            return;
        switch (err) {
            case ErrorCode.ERR_NOCPU_TRANSCODER_COUNT:
                out.append("task count:" + (iCurr + iTodo)).append(" exceed maxTaskCount:" + maxTranscoderCount).append(" licensed");
                break;
            case ErrorCode.ERR_NOCPU_HD_COUNT:
                out.append("HD count:" + (iCurr + iTodo)).append(" exceed maxHDCount:" + maxHDCount).append(" licensed");
                break;
            case ErrorCode.ERR_NOCPU_SD_COUNT:
                out.append("SD count:" + (iCurr + iTodo)).append(" exceed maxSDCount:" + maxSDCount).append(" licensed");
                break;
            case ErrorCode.ERR_NOCPU_SDI_COUNT:
                out.append("SDI count:" + (iCurr + iTodo)).append(" exceed maxSDICount:" + maxSDICount).append(" licensed");
                break;
            case ErrorCode.ERR_NOCPU_ASI_COUNT:
                out.append("ASI count:" + (iCurr + iTodo)).append(" exceed maxASICount:" + maxASICount).append(" licensed");
                break;
            case ErrorCode.ERR_NOCPU_SUPER_TASK_COUNT:
                out.append("quick transcoder count:" + (iCurr + iTodo))
                        .append(" exceed maxQuickTranscoderCount:" + maxSupperTaskCount).append(" supported in this device.");
                break;
            default:
                break;
        }
    }

    /**
     * @param toDo       task to do
     * @param outErrDesc [optional] used to record error description.
     * @return 0, {@link ErrorCode#ERR_NOCPU}, {@link ErrorCode#ERR_LICENSE_LIMIT}
     */
    public int checkLimit(ITranscodingTracker toDo, StringBuilder outErrDesc) {
        int ret = 0;

        maxOutputGroup = this.transcoderLimit.getMaxOutputGroupCount();
        maxHDCount = this.transcoderLimit.getMaxHDCount();
        maxSDCount = this.transcoderLimit.getMaxSDCount();
        maxTranscoderCount = this.transcoderLimit.getMaxTranscoderCount();
        maxSDICount = this.transcoderLimit.getMaxSDICount();
        maxASICount = this.transcoderLimit.getMaxASICount();
        maxSupperTaskCount = this.transcoderLimit.getMaxSupperTaskCount();

        if (toDo != null) {
            ret = doCheckOneTaskLimit(toDo, outErrDesc);
            if (ret != ErrorCode.ERR_NONE)
                return ret;
        }

        ret = doCheckRunningTaskLimit(toDo, outErrDesc);

        return ret;
    }

    protected int getHDOuputCount(ITranscodingTracker[] trackers, int execStatus) {
        int c = 0;
        for (int i = 0; i < trackers.length; i++) {
            ITranscodingTracker t = trackers[i];
            if (t == null)
                continue;
            if (t.getTranscoderRunningStatus() >= execStatus) {
                c += t.getTranscodingParams().getHDOuputCount();
            }
        }
        return c;
    }

    protected int getSDOuputCount(ITranscodingTracker[] trackers, int execStatus) {
        int c = 0;
        for (int i = 0; i < trackers.length; i++) {
            ITranscodingTracker t = trackers[i];
            if (t == null)
                continue;
            if (t.getTranscoderRunningStatus() >= execStatus) {
                c += t.getTranscodingParams().getSDOuputCount();
            }
        }
        return c;
    }

    protected static int getTypeTaskCount(ITranscodingTracker[] trackers, int execStatus, String type) {
        int c = 0;
        for (int i = 0; i < trackers.length; i++) {
            ITranscodingTracker t = trackers[i];
            if (t == null)
                continue;
            String tiType = t.getTranscodingParams().getInputType();
            if (t.getTranscoderRunningStatus() >= execStatus && type.equalsIgnoreCase(tiType)) {
                c++;
            }
        }
        return c;
    }

    protected int getSDITaskCount(ITranscodingTracker[] trackers, int execStatus) {
        return getTypeTaskCount(trackers, execStatus, "SDI");
    }

    protected int getASITaskCount(ITranscodingTracker[] trackers, int execStatus) {
        return getTypeTaskCount(trackers, execStatus, "ASI");
    }

    protected boolean isSDIInput(TranscodingParams p) {
        return "SDI".equalsIgnoreCase(p.getInputType());
    }

    protected boolean isASIInput(TranscodingParams p) {
        return "ASI".equalsIgnoreCase(p.getInputType());
    }

    protected static int getSupperTaskCount(ITranscodingTracker[] trackers, int execStatus) {
        int c = 0;
        for (int i = 0; i < trackers.length; i++) {
            ITranscodingTracker t = trackers[i];
            if (t == null)
                continue;
            if (t.getPriority() == TaskRuntimePrioity.PRIORITY_HIGH && t.getTranscoderRunningStatus() >= execStatus) {
                c++;
            }
        }
        return c;
    }
}
