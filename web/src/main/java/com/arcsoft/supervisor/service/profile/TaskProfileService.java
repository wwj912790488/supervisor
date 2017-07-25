package com.arcsoft.supervisor.service.profile;

import com.arcsoft.supervisor.model.domain.task.TaskProfile;
import com.arcsoft.supervisor.model.vo.task.profile.TaskProfileDto;

import java.util.List;

/**
 * Interface for task profile.
 *
 * @author zw.
 */
public interface TaskProfileService extends ProfileService<TaskProfile, TaskProfileDto> {

    List<TaskProfile> findByLayout(int row, int column);

    List<TaskProfile> findAllTaskProfile();

}
