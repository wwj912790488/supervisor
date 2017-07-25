package com.arcsoft.supervisor.repository.task;

import com.arcsoft.supervisor.model.domain.task.Task;
import com.arcsoft.supervisor.model.vo.task.TaskStatus;
import com.arcsoft.supervisor.model.vo.task.TaskType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author zw.
 */
public interface TaskRepository extends JpaRepository<Task, Integer>, JpaTaskRepository {

    public long countByServerId(String serverId);

    public Task findByTypeAndReferenceId(int type, int referenceId);

    public List<Task> findByTypeAndStatusIsNotNull(int type);

    public List<Task> findByTypeAndReferenceIdIn(int type, List<Integer> referenceIds);

    public List<Task> findByTypeInAndServerIdAndStatus(List<Integer> types, String serverId, String status);

    public List<Task> findByTypeInAndServerId(List<Integer> types, String serverId);

    /**
     * Finds {@code task} item with the given {@code type} and {@code status}.
     *
     * @param type   the type of task. see {@link TaskType}
     * @param status the status of task. see {@link TaskStatus}
     * @return item with the given {@code type} and {@code status}
     */
    public List<Task> findByTypeAndStatus(int type, String status);

    public void deleteByTypeAndReferenceId(int type, int referenceId);

    List<Task> findByServerIdIn(List<String> serverIds);

    public List<Task> findByReferenceId(int referenceId);
}
