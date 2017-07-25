package com.arcsoft.supervisor.service.task;

import com.arcsoft.supervisor.model.vo.task.TaskType;
import com.arcsoft.supervisor.model.vo.task.compose.TaskOutputResolutionAndIndexMapper;
import com.arcsoft.supervisor.model.vo.task.profile.*;
import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An object holds some data used for {@link TranscoderXmlBuilder} to build transcoder xml.
 *
 * @author zw.
 */
public class TranscoderXmlBuilderResource {


    private final TaskProfileDto taskProfileDto;
    private List<VideoProfileAndTaskOutputTargetType> videoProfileAndTaskOutputTargetTypes;
    private List<AudioProfile> audioProfiles;
    private List<TaskOutputVideoAndAudiosMapper> taskOutputVideoAndAudiosMappers;

    public TranscoderXmlBuilderResource(int TotalAudioCount,Integer AudioStartPid,TaskProfileDto taskProfileDto, TaskType taskType,boolean mosaic) {
        this.taskProfileDto = taskProfileDto;
        checkAndAddVideoProfileWithinTaskProfileDto(taskType);
        this.taskOutputVideoAndAudiosMappers = new ArrayList<>();
        this.videoProfileAndTaskOutputTargetTypes = new ArrayList<>();
        this.audioProfiles = new ArrayList<>();

        int videoIndex = 0, audioIndex = 0;
        for (int outputIndex = 0; outputIndex < this.taskProfileDto.getOutputs().size(); outputIndex++) {
            TaskOutput taskOut = this.taskProfileDto.getOutputs().get(outputIndex);
            TaskOutputVideoAndAudiosMapper taskOutputVideoAndAudiosMapper = findByTaskOutputProfile(taskOut.getLinkedProfile());
            if (taskOutputVideoAndAudiosMapper == null) {
                OutputProfileDto outputProfileDto = this.taskProfileDto.getOutputProfiles().get(taskOut.getLinkedProfile());
                VideoProfileIndexMapper videoIndexMapper = new VideoProfileIndexMapper(
                        outputProfileDto.getVideoprofiles().get(0),
                        videoIndex++,
                        taskOut.getLinkedProfile()
                );

                addVideoProfileAndTargetType(taskOut.getTargetType(), videoIndexMapper.getVideoProfile());

                List<AudioProfileIndexMapper> audioIndexMappers = new ArrayList<>();
                for (AudioProfile audioProfile : outputProfileDto.getAudioprofiles()) {
                    AudioProfileIndexMapper audioIndexMapper = new AudioProfileIndexMapper(
                            audioProfile,
                            audioIndex++,
                            taskOut.getLinkedProfile()
                    );
                    audioIndexMappers.add(audioIndexMapper);
                    this.audioProfiles.add(audioIndexMapper.getAudioProfile());
                }

                if(TotalAudioCount>1)
                {
                    if(taskOut instanceof TsOverUdpOutput)
                    {
                        TsOverUdpOutput tsOutput = (TsOverUdpOutput)taskOut;
                        TsOverUdpOutput.TSOption tsOption = tsOutput.getTsOption();
                        String totalbitrates =tsOption.getTstotalbitrate();

                        if(totalbitrates == null || StringUtils.isBlank(totalbitrates))
                        {
                            Integer videobitrates = outputProfileDto.getVideoprofiles().get(0).getBitrate();
                            Integer audiobitrates = outputProfileDto.getAudioprofiles().get(0).getAudiobitrate();

                            Integer total = mosaic?((videobitrates + audiobitrates* TotalAudioCount)/1000+1)*1200:(videobitrates+2000);
                            totalbitrates = String.valueOf(total);

                            tsOption.setTstotalbitrate(totalbitrates);
                        }

//                        String tsaudiopid =tsOption.getTsaudiopid();
//                        if(tsaudiopid==null || StringUtils.isBlank(tsaudiopid))
                        if(mosaic)
                        {
                            tsOption.setTsaudiopid(String.valueOf(AudioStartPid));
                        }
                    }
                }

                addToTaskOutputVideoAndAudiosMappers(
                        taskOut,
                        videoIndexMapper,
                        audioIndexMappers
                );
            } else {
                addToTaskOutputVideoAndAudiosMappers(
                        taskOut,
                        taskOutputVideoAndAudiosMapper.getVideoProfileIndexMapper(),
                        taskOutputVideoAndAudiosMapper.getAudioProfileIndexMappers()
                );
            }
        }
    }

    public TaskProfileDto getTaskProfileDto() {
        return taskProfileDto;
    }

    /**
     *
     * Checks and add {@link TaskProfileDto#outputProfiles} within {@link #taskProfileDto}.
     * <p>Because the codec must be {@code RAW} of sdi task and the {@link VideoProfile} doesn't
     * contains the codec and the sdi task may be contains two outputs(<code>one for sdi and other for
     * RTSP</code>) and the two output may be linked with a same {@link VideoProfile},
     * so we need find and re-created {@link VideoProfile} and re-linked it.</p>
     *
     * @param taskType the type of task
     */
    private void checkAndAddVideoProfileWithinTaskProfileDto(TaskType taskType) {
        if (taskType == TaskType.SDI_STREAM_COMPOSE && this.taskProfileDto.getOutputs().size() > 1) {
            final List<TaskOutput> taskOutputs = this.taskProfileDto.getOutputs();
            final Map<Integer, TaskOutput> needResetLinkedProfileOfTaskOutputs = new HashMap<>();
            for (int idx = 0; idx < taskOutputs.size(); idx++) {
                for (int idx2 = 0; idx2 < taskOutputs.size(); idx2++) {
                    TaskOutput taskOutputOfIdx2 = taskOutputs.get(idx2);
                    if (idx != idx2
                            && taskOutputs.get(idx).getLinkedProfile().equals(taskOutputOfIdx2.getLinkedProfile())
                            && !needResetLinkedProfileOfTaskOutputs.containsKey(taskOutputOfIdx2.getLinkedProfile())) {
                        needResetLinkedProfileOfTaskOutputs.put(taskOutputOfIdx2.getLinkedProfile(), taskOutputOfIdx2);
                    }
                }
            }

            if (!needResetLinkedProfileOfTaskOutputs.isEmpty()) {
                //Add a new output profile for sdi
                for (Map.Entry<Integer, TaskOutput> linkedProfileIdAndTaskOutputEntry
                        : needResetLinkedProfileOfTaskOutputs.entrySet()) {
                    OutputProfileDto outputProfileDto = this.taskProfileDto.getOutputProfiles()
                            .get(linkedProfileIdAndTaskOutputEntry.getValue().getLinkedProfile());
                    this.taskProfileDto.getOutputProfiles().add(outputProfileDto);
                    linkedProfileIdAndTaskOutputEntry.getValue().setLinkedProfile(
                            this.taskProfileDto.getOutputProfiles().size() - 1
                    );
                }
            }
        }
    }

    private void addToTaskOutputVideoAndAudiosMappers(TaskOutput taskOutput,
                                                      VideoProfileIndexMapper videoProfileIndexMapper,
                                                      List<AudioProfileIndexMapper> audioProfileIndexMappers) {
        this.taskOutputVideoAndAudiosMappers.add(
                new TaskOutputVideoAndAudiosMapper(taskOutput, videoProfileIndexMapper, audioProfileIndexMappers)
        );
    }

    private void addVideoProfileAndTargetType(TaskOutput.TargetType targetType, VideoProfile videoProfile) {
        this.videoProfileAndTaskOutputTargetTypes.add(
                new VideoProfileAndTaskOutputTargetType(targetType, videoProfile)
        );
    }

    /**
     * Returns items of {@link TaskOutputResolutionAndIndexMapper} converted
     * from {@link #taskOutputVideoAndAudiosMappers}.
     *
     * @return the items of {@link TaskOutputResolutionAndIndexMapper}
     */
    public List<TaskOutputResolutionAndIndexMapper> getTaskOutputResolutionAndIndexMappers() {
        List<TaskOutputResolutionAndIndexMapper> taskOutputResolutionAndIndexMappers = new ArrayList<>();
        for (int idx = 0; idx < taskOutputVideoAndAudiosMappers.size(); idx++) {
            TaskOutputVideoAndAudiosMapper tvam = taskOutputVideoAndAudiosMappers.get(idx);
            taskOutputResolutionAndIndexMappers.add(
                    TaskOutputResolutionAndIndexMapper.builder()
                            .index(idx)
                            .width(tvam.videoProfileIndexMapper.getVideoProfile().getWidth())
                            .height(tvam.videoProfileIndexMapper.getVideoProfile().getHeight())
                            .build()
            );
        }
        return taskOutputResolutionAndIndexMappers;
    }

    public List<VideoProfileAndTaskOutputTargetType> getVideoProfileAndTaskOutputTargetTypes() {
        return videoProfileAndTaskOutputTargetTypes;
    }

    public List<AudioProfile> getAudioProfiles() {
        return audioProfiles;
    }

    public List<TaskOutputVideoAndAudiosMapper> getTaskOutputVideoAndAudiosMappers() {
        return taskOutputVideoAndAudiosMappers;
    }

    private TaskOutputVideoAndAudiosMapper findByTaskOutputProfile(int profile) {
        for (TaskOutputVideoAndAudiosMapper taskOutputVideoAndAudiosMapper : taskOutputVideoAndAudiosMappers) {
            if (taskOutputVideoAndAudiosMapper.getTaskOutput().getLinkedProfile() == profile) {
                return taskOutputVideoAndAudiosMapper;
            }
        }
        return null;
    }

    public static class VideoProfileAndTaskOutputTargetType {
        private final TaskOutput.TargetType targetType;
        private final VideoProfile videoProfile;

        public VideoProfileAndTaskOutputTargetType(TaskOutput.TargetType targetType, VideoProfile videoProfile) {
            this.targetType = targetType;
            this.videoProfile = videoProfile;
        }

        public VideoProfile getVideoProfile() {
            return videoProfile;
        }

        public TaskOutput.TargetType getTargetType() {
            return targetType;
        }
    }

    public static class IndexMapper {
        private int index;
        private int linkedProfile;

        public IndexMapper(int index, int linkedProfile) {
            this.index = index;
            this.linkedProfile = linkedProfile;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public int getLinkedProfile() {
            return linkedProfile;
        }

        public void setLinkedProfile(int linkedProfile) {
            this.linkedProfile = linkedProfile;
        }
    }

    public static class VideoProfileIndexMapper extends IndexMapper {

        private VideoProfile videoProfile;

        public VideoProfileIndexMapper(VideoProfile videoProfile, int index, int linkedProfile) {
            super(index, linkedProfile);
            this.videoProfile = videoProfile;
        }

        public VideoProfile getVideoProfile() {
            return videoProfile;
        }

        public void setVideoProfile(VideoProfile videoProfile) {
            this.videoProfile = videoProfile;
        }

    }

    public static class AudioProfileIndexMapper extends IndexMapper {
        private AudioProfile audioProfile;

        public AudioProfileIndexMapper(AudioProfile audioProfile, int index, int linkedProfile) {
            super(index, linkedProfile);
            this.audioProfile = audioProfile;
        }

        public AudioProfile getAudioProfile() {
            return audioProfile;
        }

        public void setAudioProfile(AudioProfile audioProfile) {
            this.audioProfile = audioProfile;
        }
    }

    public static class TaskOutputVideoAndAudiosMapper {

        private TaskOutput taskOutput;
        private VideoProfileIndexMapper videoProfileIndexMapper;
        private List<AudioProfileIndexMapper> audioProfileIndexMappers;

        public TaskOutputVideoAndAudiosMapper(TaskOutput taskOutput, VideoProfileIndexMapper videoProfileIndexMapper,
                                              List<AudioProfileIndexMapper> audioProfileIndexMappers) {
            this.taskOutput = taskOutput;
            this.videoProfileIndexMapper = videoProfileIndexMapper;
            this.audioProfileIndexMappers = audioProfileIndexMappers;
        }

        public VideoProfileIndexMapper getVideoProfileIndexMapper() {
            return videoProfileIndexMapper;
        }

        public void setVideoProfileIndexMapper(VideoProfileIndexMapper videoProfileIndexMapper) {
            this.videoProfileIndexMapper = videoProfileIndexMapper;
        }

        public List<AudioProfileIndexMapper> getAudioProfileIndexMappers() {
            return audioProfileIndexMappers;
        }

        public void setAudioProfileIndexMappers(List<AudioProfileIndexMapper> audioProfileIndexMappers) {
            this.audioProfileIndexMappers = audioProfileIndexMappers;
        }

        public TaskOutput getTaskOutput() {
            return taskOutput;
        }

        public void setTaskOutput(TaskOutput taskOutput) {
            this.taskOutput = taskOutput;
        }
    }

}
