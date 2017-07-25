package com.arcsoft.supervisor.cluster.service;

/**
 *
 * A listener for add callbacks of the {@link HeartBeatSender}.
 *
 * @author zw.
 */
public interface HeartBeatSenderListener {

    /**
     * Called after {@link HeartBeatSender#start()}
     */
    void onStart();

    /**
     * Called after {@link HeartBeatSender#stop()}
     */
    void onStop();

}
