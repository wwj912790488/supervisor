package com.arcsoft.supervisor.repository.task;

import com.arcsoft.supervisor.model.domain.channel.TaskChannelAssociatedScreenPosition;
import com.arcsoft.supervisor.model.domain.task.QTask;
import com.arcsoft.supervisor.model.domain.task.Task;
import com.arcsoft.supervisor.model.vo.task.TaskStatus;
import com.arcsoft.supervisor.model.vo.task.TaskType;
import com.mysema.query.jpa.impl.JPAQuery;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A implementation of <code>JpaTaskRepository</code>.
 *
 * @author zw.
 */
public class TaskRepositoryImpl implements JpaTaskRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<TaskChannelAssociatedScreenPosition> findIPSteamComposeTaskChannelAssociatedScreenPositionByChannelId(int channelId) {
        return findComposeTaskChannelAssociatedScreenPositionByChannelIdAndTypes(channelId, TaskType.IP_STREAM_COMPOSE.getType());
    }

    @Override
    public List<TaskChannelAssociatedScreenPosition> findComposeTaskChannelAssociatedScreenPositionByChannelIdAndTypes(int channelId, Integer... types) {
        List results = em.createNativeQuery("SELECT t.id as taskId, p.row, p.column," +
                " ss.row_count as rowCount, ss.column_count as columnCount FROM screen s" +
                " RIGHT  JOIN screen_position p ON s.active_schema_id = p.schema_id" +
                " LEFT JOIN screen_schema ss on ss.id = s.active_schema_id" +
                " RIGHT JOIN task t ON s.id = t.ref_id" +
                " WHERE p.channel_id = ?1 AND s.id > 0 AND t.type in (?2) AND t.`status` = 'RUNNING'" +
                " AND NOT length(t.server_id) = 0")
                .setParameter(1, channelId)
                .setParameter(2, Arrays.asList(types))
                .getResultList();
        List<TaskChannelAssociatedScreenPosition> taskChannelAssociatedScreenPositions = new ArrayList<>();
        for (Object o : results) {
            Object[] values = (Object[]) o;
            taskChannelAssociatedScreenPositions.add(new TaskChannelAssociatedScreenPosition((int) values[0], (byte) values[3], (byte) values[4], (byte) values[1], (byte) values[2]));
        }
        return taskChannelAssociatedScreenPositions;
    }

    @Override
    public List<Task> findRunningIPStreamComposeTasks() {
        return findRunningComposeTasksByTypes(TaskType.IP_STREAM_COMPOSE.getType());
    }

    @Override
    public List<Task> findRunningComposeTasksByTypes(Integer... types) {
        JPAQuery query = new JPAQuery(em);
        QTask qTask = QTask.task;
        query.from(qTask)
                .where(
                        qTask.type.in(Arrays.asList(types))
                                .and(qTask.status.eq(TaskStatus.RUNNING.toString()))
                );
        return query.list(qTask);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Task> findRunningIPStreamComposeTasksByWallId(int wallId) {
        return findRunningComposeTasksByWallIdAndTypes(wallId, TaskType.IP_STREAM_COMPOSE.getType());
    }

    @SuppressWarnings("all")
    @Override
    public List<Task> findRunningComposeTasksByWallIdAndTypes(int wallId, Integer... types){
        return findComposeTasksByWallIdAndTypesAndStatus(wallId, new TaskStatus[]{ TaskStatus.RUNNING }, types);
    }

    @Override
    public List<Task> findComposeTasksByWallIdAndTypesAndStatus(int wallId, TaskStatus[] status, Integer... types) {
        List<String> taskStatusList = new ArrayList<>();
        for (TaskStatus st : status) {
            taskStatusList.add(st.name());
        }
        return em.createNativeQuery("SELECT t.* FROM task t JOIN screen s ON (t.ref_id = s.id)" +
                " JOIN wall_position wp ON wp.id = s.wall_position_id" +
                " JOIN wall w ON (w.id = wp.wall_id AND w.id = :wallId)" +
                " WHERE t.type IN :types AND (t.`status` IN :statusList OR t.`status` IS NULL)", Task.class)
                .setParameter("wallId", wallId)
                .setParameter("statusList", taskStatusList)
                .setParameter("types", Arrays.asList(types))
                .getResultList();
    }
}
