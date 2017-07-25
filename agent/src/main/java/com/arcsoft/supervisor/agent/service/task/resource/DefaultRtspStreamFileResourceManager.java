package com.arcsoft.supervisor.agent.service.task.resource;

import com.arcsoft.supervisor.agent.service.task.RtspStreamFileResourceManager;
import com.arcsoft.supervisor.agent.service.task.TranscodingTrackerResource;
import com.arcsoft.supervisor.transcoder.ITranscodingTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * @author zw.
 */
public class DefaultRtspStreamFileResourceManager implements RtspStreamFileResourceManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRtspStreamFileResourceManager.class);

    private static final String RTSP_STREAM_FILE_SUFFIX = ".stream";

    @SuppressWarnings("unchecked")
    @Override
    public void deleteStreamFile(ITranscodingTracker tracker){
        if (tracker != null) {
            Object taskResourceHolder = tracker.getUserData();
            if (taskResourceHolder != null && taskResourceHolder instanceof TaskResourceHolder) {
                TaskResourceHolder holder = (TaskResourceHolder) taskResourceHolder;
                TranscodingTrackerResource<List<String>> rtspResource = holder.<List<String>>getByType(RtspTranscodingTrackerResource.class);
                if (rtspResource != null){
                    for (String streamPath : rtspResource.getResource()){
                        if (streamPath.endsWith(RTSP_STREAM_FILE_SUFFIX)){
                            try {
                                Files.deleteIfExists(Paths.get(streamPath));
                            } catch (IOException e) {
                                LOGGER.error("Failed to delete stream file [{}]", streamPath);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public String composeAndWriteUrl(String rtspStoragePath, String rtspFileName, String udpUrl) throws IOException {
        String path = composeRtspStreamFilePath(rtspStoragePath, rtspFileName);
        writeUrlToStreamFile(udpUrl, path);
        return path;
    }

    @Override
    public String composeRtspStreamFilePath(String rtspStoragePath, String rtspFileName) {
        return rtspStoragePath + rtspFileName + RTSP_STREAM_FILE_SUFFIX;
    }

    @Override
    public void writeUrlToStreamFile(String udpUrl, String streamFilePath) throws IOException {
        File file = new File(streamFilePath);
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(udpUrl);
            writer.newLine();
        } catch (IOException e) {
            if (file.exists()) {
                file.delete();
            }
            throw e;
        }
    }

    @Override
    public void setStreamFilePathToItranscodingTracker(ITranscodingTracker tracker, String streamFilePath) {
        setStreamFilePathToItranscodingTracker(tracker, new String[]{streamFilePath});
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setStreamFilePathToItranscodingTracker(ITranscodingTracker tracker, String... streamFilePaths) {
        if (tracker != null){
            if (tracker.getUserData() != null){
                if (tracker.getUserData() instanceof TaskResourceHolder){
                    TaskResourceHolder holder = (TaskResourceHolder) tracker.getUserData();
                    TranscodingTrackerResource<List<String>> resource = holder.<List<String>>getByType(RtspTranscodingTrackerResource.class);
                    if (resource != null){
                        resource.setResource(Arrays.asList(streamFilePaths));
                    }else{
                        holder.addResource(new RtspTranscodingTrackerResource(streamFilePaths));
                    }
                }
            }else{
                tracker.setUserData(TaskResourceHolder.constructRtspResource(streamFilePaths));
            }
        }
    }


}
