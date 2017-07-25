package com.arcsoft.supervisor.transcoder;


/**
 * @author Bing
 */
public interface ITranscodingMessageListener {

    public static final int LEVEL_WARNING = 0;
    public static final int LEVEL_ERROR = 1;

    /**
     * @param transcodingNotifier task tracker
     * @param level               {@link #LEVEL_WARNING}, {@link #LEVEL_ERROR}
     * @param code                error code
     * @param msg                 error message
     */
    void fireTaskErrorMessage(ITranscodingNotifier transcodingNotifier, int level, int code, String msg);

}
