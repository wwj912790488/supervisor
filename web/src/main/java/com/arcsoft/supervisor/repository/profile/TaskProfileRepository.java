package com.arcsoft.supervisor.repository.profile;

import com.arcsoft.supervisor.model.domain.task.TaskProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author zw.
 */
public interface TaskProfileRepository extends JpaRepository<TaskProfile, Integer> {

    List<TaskProfile> findByScreenRowAndScreenColumn(int row, int column);

}
