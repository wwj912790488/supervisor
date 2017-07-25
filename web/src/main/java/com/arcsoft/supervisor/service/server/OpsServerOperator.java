package com.arcsoft.supervisor.service.server;

import com.arcsoft.supervisor.model.domain.server.AbstractOpsServer;

/**
 * Defines ops operations to communicate with <tt>ops server</tt>.
 *
 * @author zw.
 */
public interface OpsServerOperator<T extends AbstractOpsServer> {

    /**
     * Sends the <code>url</code> to specific <code>ops server</code> let
     * the <code>server</code> to play the <code>url</code>.
     *
     * @param server the ops server instance
     * @param url    the url will push to <code>server</code>
     */
    void start(T server, String url, String source);


    /**
     * Sends the <code>number</code> to the <code>server</code> to do
     * recognize operation.
     *
     * @param server the ops server instance
     * @param number the number of the server instance
     */
    void recognize(T server, int number);


    /**
     * Sends a <code>stop</code> signal to the <code>server</code>.
     *
     * @param server the ops server instance
     */
    void stop(T server);
    
    /**
     * Sends a <code>deploy</code> signal to the <code>server</code>.
     *
     * @param server the ops server instance
     */
    void deployPackage(T server, String url, String hash);


}
