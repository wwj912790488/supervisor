package com.arcsoft.supervisor.transcoder;


import com.arcsoft.supervisor.transcoder.type.TaskStatus;

import java.io.InputStream;

/**
 * @author Bing
 */
public interface ITranscodingTracker extends ITranscodingNotifier {
    //
    // internal transcoding status
    //

    /**
     * must < TRANSCODING_NOT_START
     */
    public static final int TRANSCODING_EXIT = -3;
    public static final int TRANSCODING_STOPPING = -2;
    /**
     * suspend shoud greater than stopping
     */
    public static final int TRANSCODING_SUSPEND = -1;
    /**
     * int waiting
     */
    public static final int TRANSCODING_NOT_START = 0;
    /**
     * int waiting
     */
    public static final int TRANSCODING_PREJOB = 1;
    /**
     * must >TRANSCODING_NOT_START
     */
    public static final int TRANSCODING_STARTING = 2;
    /**
     * must >TRANSCODING_NOT_START
     */
    public static final int TRANSCODING_STARTED = 3;


    public static interface IMessageNotifyListener {
        void fireTaskErrorMessage(TranscodingKey taskId, int level, int code, String msg);
    }

    public static interface IEventNotifyListener extends IMessageNotifyListener {
        void fireTaskStatusChanged(TranscodingKey taskId, TaskStatus status);
    }


    InputStream getThumbnail(final int width);

    TranscodingInfo getProgressInfo(byte[] filters);

    void switchSignalMode(int signalMode);

    int notifyStartOutput(boolean isOutput);

    int getTranscoderRunningStatus(); /*return internal detail status*/

    TaskStatus getTranscodeEndState();

    int getLastError();

    String getLastErrorDesc();

    public boolean needRestartTask();

    void seek(long p);

    long resume();

    void pause();

    long tell();

    /**
     * Show or hide the warning border on specified screen index.
     * <p><b>Note: This method is used in compose stream task otherwise task
     * may not working.</b></p>
     *
     * @param index the index of screen
     * @param isShow true show warning border otherwise is hide
     */
    void warnBorder(int index, boolean isShow);

    /**
     * Show or hide the <code>message</code> on the screen.
     * <p><b>Note: This method is used in compose stream task otherwise task
     * may not working.</b></p>
     *
     * @param groupIdx which index of output groups
     * @param message the message to be show
     * @param width the size of width
     * @param height the size of height
     */
    void displayMessage(int groupIdx, String message, int width, int height);

    void displayStyledMessage(int groupIdx, String font, int fontSize, int color, int alpha,
                                     int x, int y, int width, int height, String message);

    /**
     * Reload the given <code>transcoderXml</code>.
     * <p><b>Notes: The functionality is used for fast switch compose task.
     * And must specific value for the <code>transcoder.startupMode</code>
     * in the <code>config.properties</code> file and the value can not be 0.</b></p>
     * More config options see below:
     * <ul>
     *     <li><code>transcoder.preload.initCount</code>: the min number of pre-started transcoder process </li>
     *     <li><code>transcoder.preload.maxCount: the max number of transcoder process can be running</code></li>
     * </ul>
     *
     * @param transcoderXml the xml to be reload
     */
    void reload(String transcoderXml);


    void switchAudio(int videoSettingIndex, int displayIndex);

    Integer getPid();

    /**
     * for shutdown quickly
     */
    void destroy();

}
