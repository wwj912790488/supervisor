package com.arcsoft.supervisor.service.converter.impl;

import com.arcsoft.supervisor.model.domain.task.ProfileTemplate;
import com.arcsoft.supervisor.model.domain.task.TaskProfile;
import com.arcsoft.supervisor.model.vo.task.profile.TaskProfileDto;
import com.arcsoft.supervisor.repository.profile.TaskProfileRepository;
import com.arcsoft.supervisor.service.converter.Converter;
import com.arcsoft.supervisor.service.converter.ConverterAdapter;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.arcsoft.supervisor.commons.json.JsonMapper.composeExistedNodesAsJson;
import static com.arcsoft.supervisor.commons.json.JsonMapper.getMapper;

/**
 * {@link Converter} implementation to convert between {@link TaskProfile} and {@link TaskProfileDto}.
 *
 * @author zw.
 */
@Service("taskProfileAndTaskProfileDtoConverter")
public class TaskProfileAndTaskProfileDtoConverter extends ConverterAdapter<TaskProfileDto, TaskProfile> {

    private static final String[] defaultIgnoredFields = {"outputProfiles", "outputs"};

    private final String[] ignoredFields;

    @Autowired
    private TaskProfileRepository taskProfileRepository;

    public TaskProfileAndTaskProfileDtoConverter() {
        this(defaultIgnoredFields);
    }

    public TaskProfileAndTaskProfileDtoConverter(String[] ignoredFields) {
        this.ignoredFields = ignoredFields;
    }

    @Override
    public TaskProfileDto doBack(TaskProfile source) throws Exception {
        TaskProfileDto taskProfileDto = getMapper().readValue(
                source.getProfileTemplate().getTemplate(),
                TaskProfileDto.class
        );
        BeanUtils.copyProperties(source, taskProfileDto, ignoredFields);
        return taskProfileDto;
    }


    @Override
    public TaskProfile doForward(TaskProfileDto source) throws Exception {
        boolean hasId = source.getId() != null;
        TaskProfile taskProfile = hasId ? taskProfileRepository.findOne(source.getId()) : new TaskProfile();
        String json = composeExistedNodesAsJson(getMapper().writeValueAsString(source),
                TaskProfileDto.NODE_NAME_OUTPUTPROFILES,
                TaskProfileDto.NODE_NAME_TASKOUTPUTS
        );
        String[] actualIgnoredFields = hasId ? ArrayUtils.add(ignoredFields, "id") : ignoredFields;
        if (hasId) {
            taskProfile.getProfileTemplate().setTemplate(json);
        } else {
            taskProfile.setProfileTemplate(ProfileTemplate.from(json));
        }
        BeanUtils.copyProperties(source, taskProfile, actualIgnoredFields);
        taskProfile.setAmountOfOutput(source.getOutputs() == null ? 0 : source.getOutputs().size());
        return taskProfile;
    }

}
