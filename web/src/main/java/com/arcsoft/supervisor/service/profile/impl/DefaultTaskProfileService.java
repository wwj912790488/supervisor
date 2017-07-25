package com.arcsoft.supervisor.service.profile.impl;

import com.arcsoft.supervisor.model.domain.task.TaskProfile;
import com.arcsoft.supervisor.model.vo.task.profile.TaskProfileDto;
import com.arcsoft.supervisor.repository.profile.TaskProfileRepository;
import com.arcsoft.supervisor.service.TransactionSupport;
import com.arcsoft.supervisor.service.converter.impl.TaskProfileAndTaskProfileDtoConverter;
import com.arcsoft.supervisor.service.profile.TaskProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zw.
 */
@Service("defaultTaskProfileService")
public class DefaultTaskProfileService extends AbstractProfileService<TaskProfile, TaskProfileDto>
        implements TransactionSupport, TaskProfileService {

    private final TaskProfileRepository taskProfileRepository;

    @Autowired
    public DefaultTaskProfileService(TaskProfileRepository taskProfileRepository,
                                     TaskProfileAndTaskProfileDtoConverter taskProfileAndTaskProfileDtoConverter) {
        super(taskProfileRepository, taskProfileAndTaskProfileDtoConverter);
        this.taskProfileRepository = taskProfileRepository;
    }

    @Override
    public List<TaskProfile> findByLayout(int row, int column) {
        return taskProfileRepository.findByScreenRowAndScreenColumn(row, column);
    }

    @Override
    public List<TaskProfile> findAllTaskProfile() {
        return taskProfileRepository.findAll();
    }
}
