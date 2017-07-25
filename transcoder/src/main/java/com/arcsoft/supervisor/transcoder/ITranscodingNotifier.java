package com.arcsoft.supervisor.transcoder;

import java.util.Date;

/**
 * Notifier (task tracker) on callback
 */
public interface ITranscodingNotifier {
    Object getUserData();

    void setUserData(Object userData);

    TranscodingKey getTranscodingKey();

    TranscodingParams getTranscodingParams();

    Date getTaskStartAt();

    Date getTaskEndAt();

    TranscodeStatistic getStatistic();

    SourceSignal getSourceSignalStatus();

    int getPriority();

    boolean isOnOutput();

    int getStopFlag();

    int getLastError();

    String getLastErrorDesc();

    boolean needRestartTask();

}
