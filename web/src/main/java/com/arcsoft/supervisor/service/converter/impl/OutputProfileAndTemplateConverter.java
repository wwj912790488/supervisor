package com.arcsoft.supervisor.service.converter.impl;

import com.arcsoft.supervisor.commons.json.JsonMapper;
import com.arcsoft.supervisor.model.domain.task.OutputProfile;
import com.arcsoft.supervisor.model.domain.task.ProfileTemplate;
import com.arcsoft.supervisor.model.vo.task.profile.AudioProfile;
import com.arcsoft.supervisor.model.vo.task.profile.OutputProfileDto;
import com.arcsoft.supervisor.model.vo.task.profile.VideoProfile;
import com.arcsoft.supervisor.repository.profile.OutputProfileRepository;
import com.arcsoft.supervisor.service.converter.ConverterAdapter;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Converter for convert from {@link OutputProfileDto} to {@link OutputProfile} or back.
 *
 * @author zw.
 */
@Service("outputProfileAndTemplateConverter")
public class OutputProfileAndTemplateConverter extends ConverterAdapter<OutputProfileDto, OutputProfile> {

    private static String[] defaultIgnoredFieldsOfOutputProfile = {"template", "videoAndAudioDescription"};

    private static String[] defaultIgnoredFieldsOfOutputProfileTemplate = {"videoprofiles", "audioprofiles"};

    private final String[] ignoredFieldsOfOutputProfile;

    private final String[] ignoredFieldsOfOutputProfileTemplate;

    @Autowired
    private OutputProfileRepository outputProfileRepository;

    public OutputProfileAndTemplateConverter() {
        this(defaultIgnoredFieldsOfOutputProfile, defaultIgnoredFieldsOfOutputProfileTemplate);
    }

    public OutputProfileAndTemplateConverter(String[] ignoredFieldsOfOutputProfile,
                                             String[] ignoredFieldsOfOutputProfileTemplate) {
        this.ignoredFieldsOfOutputProfile = ignoredFieldsOfOutputProfile;
        this.ignoredFieldsOfOutputProfileTemplate = ignoredFieldsOfOutputProfileTemplate;
    }

    @Override
    public OutputProfile doForward(OutputProfileDto source) throws IOException {
        boolean hasId = source.getId() != null;
        OutputProfile profile = hasId ? outputProfileRepository.findOne(source.getId()) : new OutputProfile();
        String[] actualIgnoredPropertiesOfOutputProfile = hasId ? ArrayUtils.add(ignoredFieldsOfOutputProfile, "id")
                : ignoredFieldsOfOutputProfile;
        BeanUtils.copyProperties(source, profile, actualIgnoredPropertiesOfOutputProfile);
        if (hasId) {
            profile.getProfileTemplate().setTemplate(JsonMapper.toJson(getVideoAndAudioMap(source)));
        } else {
            profile.setProfileTemplate(ProfileTemplate.from(JsonMapper.toJson(getVideoAndAudioMap(source))));
        }
        profile.setVideoAndAudioDescription(createVideoAndAudioDescription(source));
        return profile;
    }

    private Map<String, Object> getVideoAndAudioMap(OutputProfileDto source) {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put(OutputProfileDto.NODE_NAME_VIDEOPROFILES, source.getVideoprofiles());
        values.put(OutputProfileDto.NODE_NAME_AUDIOPROFILES, source.getAudioprofiles());
        return values;
    }

    private String createVideoAndAudioDescription(OutputProfileDto profileTemplate) {
        StringBuilder description = new StringBuilder();
        VideoProfile firstVideoProfile = profileTemplate.getVideoprofiles().get(0);
        if(firstVideoProfile.getVideopassthrough()){
            description.append("Video pass through");
        }else {
            description.append(firstVideoProfile.getCodec()).append(" ")
                    .append(firstVideoProfile.getWidth()).append("x").append(firstVideoProfile.getHeight()).append(" ")
                    .append(firstVideoProfile.getBitrateControl()).append(" ")
                    .append(firstVideoProfile.getBitrate()).append("Kbps");
        }

        if (profileTemplate.getAudioprofiles() != null && profileTemplate.getAudioprofiles().size() > 0) {
            AudioProfile firstAudioProfile = profileTemplate.getAudioprofiles().get(0);
            if(firstAudioProfile.getAudiopassthrough()){
                description.append(" | ").append("Audio pass through");
            }else {
                description.append(" | ").append(firstAudioProfile.getAudiocodec()).append(" ")
                        .append(firstAudioProfile.getAudiosamplerate().getValue() / 1000.0).append("KHz").append(" ")
                        .append(firstAudioProfile.getAudiochannel().getValue()).append(" 声道 ")
                        .append(firstAudioProfile.getAudiobitrate()).append("Kbps");
            }
        }
        return description.toString();
    }


    @Override
    public OutputProfileDto doBack(OutputProfile source) throws IOException {
        OutputProfileDto template = JsonMapper.getMapper().readValue(source.getProfileTemplate().getTemplate(),
                OutputProfileDto.class);
        BeanUtils.copyProperties(source, template, ignoredFieldsOfOutputProfileTemplate);
        return template;
    }


}
