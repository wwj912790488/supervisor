package com.arcsoft.supervisor.agent.service.task;

import com.arcsoft.supervisor.transcoder.ITranscodingTracker;

import java.io.IOException;

/**
 * A cleaner to clear rtsp stream files.
 *
 * @author zw.
 */
public interface RtspStreamFileResourceManager {

    /**
     * Deletes the rtsp stream file with specific <code>tracker</code>.
     * <p>The <code>tracker</code> contains a <code>getUserData()</code> method to retrieves
     * the rtsp stream file path.</p>
     *
     * @param tracker {@link com.arcsoft.supervisor.transcoder.ITranscodingTracker}
     */
    void deleteStreamFile(ITranscodingTracker tracker);


    String composeAndWriteUrl(String rtspStoragePath, String rtspFileName, String udpUrl) throws IOException;


    /**
     * Returns the absolute path of <code>rtspStoragePath</code> and <code>rtspFileName</code>
     * representations file.
     *
     * @param rtspStoragePath the rtsp server publish folder
     * @param rtspFileName    the stream file name
     * @return the absolute path of stream file
     */
    String composeRtspStreamFilePath(String rtspStoragePath, String rtspFileName);


    /**
     * Writes the <code>udpUrl</code> to the <code>streamFilePath</code> representations file.
     * <p><b>Note: This method will checks the <code>streamFilePath</code> file is exist or not. Deletes
     * the file if it already existed before write <code>udpUrl</code> to it.</b></p>
     *
     * @param udpUrl         the output udp url
     * @param streamFilePath the rtsp stream file path
     * @throws IOException if occurs exception when deleteStreamFile or write <code>udpUrl</code> to file
     */
    void writeUrlToStreamFile(String udpUrl, String streamFilePath) throws IOException;


    /**
     * Sets the <code>streamFilePath</code> to <code>ITranscodingTracker</code>.
     *
     * @param tracker        {@link com.arcsoft.supervisor.transcoder.ITranscodingTracker}
     * @param streamFilePath the rtsp stream file path
     */
    void setStreamFilePathToItranscodingTracker(ITranscodingTracker tracker, String streamFilePath);

    /**
     * Sets the <code>streamFilePaths</code> to <code>ITranscodingTracker</code>.
     *
     * @param tracker         {@link com.arcsoft.supervisor.transcoder.ITranscodingTracker}
     * @param streamFilePaths the rtsp stream file paths
     */
    void setStreamFilePathToItranscodingTracker(ITranscodingTracker tracker, String... streamFilePaths);


}
