package com.arcsoft.supervisor.repository.alarm;

import com.arcsoft.supervisor.model.domain.alarm.AlarmPushLog;
import com.arcsoft.supervisor.service.alarm.impl.CustomAlarmLogQueryParams;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Arrays;
import java.util.List;

/**
 * Default implementation for {@link com.arcsoft.supervisor.repository.log.JpaAlarmPushLogRepository}.
 *
 * @author jt.
 */
@SuppressWarnings("all")
public class AlarmPushLogRepositoryImpl implements JpaAlarmPushLogRepository {

     @PersistenceContext
     private EntityManager em;

    @Override
    public List<AlarmPushLog> findAll(CustomAlarmLogQueryParams params) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<AlarmPushLog> criteriaQuery = criteriaBuilder.createQuery(AlarmPushLog.class);
        Root<AlarmPushLog> root = criteriaQuery.from(AlarmPushLog.class);

        List<Integer> types = Arrays.asList(0, 1 ,2, 27, 28, 29, 30, 31, 32, 33, 34, 37);

        criteriaQuery.where(
                criteriaBuilder.and(
                        criteriaBuilder.greaterThan(root.<Long>get("startTime"), params.getStartTime().getTime()),
                        criteriaBuilder.lessThan(root.<Long>get("startTime"), params.getEndTime().getTime())
                ),
                root.<Integer>get("type").in(types),
                root.<Integer>get("alarmConfig").in(params.getConfigId()));
        criteriaQuery.orderBy(criteriaBuilder.desc(root.<Long>get("startTime")));

        return em.createQuery(criteriaQuery).getResultList();
    }
}
