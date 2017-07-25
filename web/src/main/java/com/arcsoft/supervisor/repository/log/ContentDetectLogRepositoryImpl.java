package com.arcsoft.supervisor.repository.log;

import com.arcsoft.supervisor.model.domain.channel.Channel;
import com.arcsoft.supervisor.model.domain.channel.ChannelGroup;
import com.arcsoft.supervisor.model.domain.log.ContentDetectLog;
import com.arcsoft.supervisor.model.domain.task.Task;
import com.arcsoft.supervisor.model.vo.task.TaskStatus;
import com.arcsoft.supervisor.model.vo.task.TaskType;
import com.arcsoft.supervisor.repository.channel.ChannelRepository;
import com.arcsoft.supervisor.repository.task.TaskRepository;
import com.arcsoft.supervisor.service.log.impl.ChannelsContentDetectQueryParams;
import com.arcsoft.supervisor.service.log.impl.ContentDetectQueryParams;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Default implementation for {@link com.arcsoft.supervisor.repository.log.JpaContentDetectLogRepository}.
 *
 * @author zw.
 */
@SuppressWarnings("all")
public class ContentDetectLogRepositoryImpl implements JpaContentDetectLogRepository {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ChannelRepository channelRepository;


    /**
     * The predicate object to check the {@code Channel} is enable content detect or not by
     * {@link Channel#getEnableContentDetect()}.
     */
    private static final Predicate<Channel> CONTENT_DETECT_AND_RECORD_ENABLE = new Predicate<Channel>() {
        @Override
        public boolean apply(Channel input) {
            return input.getEnableContentDetect() && input.getEnableRecord();
        }
    };

    /**
     * The function object do transform the {@code Channel} to {@code Integer} by {@link Channel#getId()}.
     */
    private static final Function<Channel, Integer> CHANNEL_INTEGER_FUNCTION = new Function<Channel, Integer>() {
        @Nullable
        @Override
        public Integer apply(Channel input) {
            return input.getId();
        }
    };

    /**
     * The function object do transform the {@code Task} to {@code Integer} by {@link Task#getReferenceId()}.
     */
    private static final Function<Task, Integer> TASK_INTEGER_FUNCTION = new Function<Task, Integer>() {
        @Nullable
        @Override
        public Integer apply(@Nullable Task input) {
            return input.getReferenceId();
        }
    };


    @Override
    public List<ContentDetectLog> findNeedGenerateM3u8LogsWithPeriod() {
        final List<Integer> channelIds = Lists.transform(taskRepository.findByTypeAndStatus(TaskType.RTSP.getType(),
                TaskStatus.RUNNING.toString()), TASK_INTEGER_FUNCTION);
        if (channelIds.isEmpty()) {
            return Collections.emptyList();
        }

        final List<Integer> enabledContentDetectChannelIds = FluentIterable.from(channelRepository.findAll(channelIds))
                .filter(CONTENT_DETECT_AND_RECORD_ENABLE)
                .transform(CHANNEL_INTEGER_FUNCTION)
                .toList();
        if (enabledContentDetectChannelIds.isEmpty()) {
            return Collections.emptyList();
        }

        return em.createNativeQuery("SELECT * FROM content_detect_log WHERE " +
                " video_file_path IS NULL AND channel_id IN (?1)", ContentDetectLog.class)
                .setParameter(1, enabledContentDetectChannelIds)
                .getResultList();
    }


	@Override
	public void delete(ContentDetectQueryParams params) {
		CriteriaBuilder criteriaBuilder= em.getCriteriaBuilder();
	    CriteriaDelete<ContentDetectLog> criteriaDelete = criteriaBuilder.createCriteriaDelete(ContentDetectLog.class);

	    Root<ContentDetectLog> root = criteriaDelete.from(ContentDetectLog.class);
	    List<javax.persistence.criteria.Predicate> predicates = buildQueryPredicates(
				params, criteriaBuilder, root);
	    
		criteriaDelete.where(predicates.toArray(new javax.persistence.criteria.Predicate[] {}));
		
		em.createQuery(criteriaDelete).executeUpdate();
	}

	@Override
	public List<ContentDetectLog> find(ContentDetectQueryParams params) {
		CriteriaBuilder criteriaBuilder= em.getCriteriaBuilder();
	    CriteriaQuery<ContentDetectLog> criteriaQuery = criteriaBuilder.createQuery(ContentDetectLog.class);

	    Root<ContentDetectLog> root = criteriaQuery.from(ContentDetectLog.class);
	    List<javax.persistence.criteria.Predicate> predicates = buildQueryPredicates(
				params, criteriaBuilder, root);
	    
		criteriaQuery.where(predicates.toArray(new javax.persistence.criteria.Predicate[] {}));
		
		TypedQuery<ContentDetectLog> q = em.createQuery(criteriaQuery);
		return q.getResultList();
	}

    @Override
    public List<ContentDetectLog> find(ChannelsContentDetectQueryParams params) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ContentDetectLog> criteriaQuery = criteriaBuilder.createQuery(ContentDetectLog.class);

        Root<ContentDetectLog> root = criteriaQuery.from(ContentDetectLog.class);

        criteriaQuery.where(
                criteriaBuilder.or(
                        criteriaBuilder.and(
                                criteriaBuilder.greaterThan(root.<Long>get("startTime"), params.getStartTime().getTime()),
                                criteriaBuilder.lessThan(root.<Long>get("startTime"), params.getEndTime().getTime())),
                        criteriaBuilder.and(
                                criteriaBuilder.lessThan(root.<Long>get("startTime"), params.getStartTime().getTime()),
                                criteriaBuilder.or(
                                        criteriaBuilder.greaterThan(root.<Long>get("endTime"), params.getStartTime().getTime()),
                                        criteriaBuilder.equal(root.<Long>get("endTime"), 0))
                                )
                        ),
                root.<Integer>get("type").in(params.getTypes()),
                root.<Integer>get("channelId").in(params.getChannelIds()));

        return em.createQuery(criteriaQuery).getResultList();
    }

    @Override
    public List<ContentDetectLog> findByAllType(ChannelsContentDetectQueryParams params) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ContentDetectLog> criteriaQuery = criteriaBuilder.createQuery(ContentDetectLog.class);

        Root<ContentDetectLog> root = criteriaQuery.from(ContentDetectLog.class);

        List<Integer> types = Arrays.asList(0, 1 ,2, 27, 28, 29, 30, 31, 32, 33, 34, 37);

        criteriaQuery.where(
                    criteriaBuilder.and(
                            criteriaBuilder.greaterThan(root.<Long>get("startTime"), params.getStartTime().getTime()),
                            criteriaBuilder.lessThan(root.<Long>get("startTime"), params.getEndTime().getTime())
                    ),
                root.<Integer>get("type").in(types),
                root.<Integer>get("channelId").in(params.getChannelIds()));
        criteriaQuery.orderBy(criteriaBuilder.desc(root.<Long>get("startTime")));

        return em.createQuery(criteriaQuery).getResultList();
    }


    private List<javax.persistence.criteria.Predicate> buildQueryPredicates(
			ContentDetectQueryParams params, CriteriaBuilder criteriaBuilder,
			Root<ContentDetectLog> root) {
		List<javax.persistence.criteria.Predicate> predicates = new ArrayList<javax.persistence.criteria.Predicate>();
	    if (params.getStartTime() != null && params.getEndTime() != null){
	    	predicates.add(criteriaBuilder.or(
                            criteriaBuilder.and(
                                    criteriaBuilder.greaterThan(root.<Long>get("startTime"), params.getStartTime().getTime()),
                                    criteriaBuilder.lessThan(root.<Long>get("startTime"), params.getEndTime().getTime())),
                            criteriaBuilder.and(
                                    criteriaBuilder.lessThan(root.<Long>get("startTime"), params.getStartTime().getTime()),
                                    criteriaBuilder.or(
                                            criteriaBuilder.greaterThan(root.<Long>get("endTime"), params.getStartTime().getTime()),
                                            criteriaBuilder.equal(root.<Long>get("endTime"), 0))
                            )
                    )
            );
        }

        List<ChannelGroup> groups = FluentIterable.from(params.getGroups()).transform(new Function<Integer, ChannelGroup>() {

            @Nullable
            @Override
            public ChannelGroup apply(Integer input) {
                if(input != -1) {
                    ChannelGroup group = new ChannelGroup();
                    group.setId(input);
                    return group;
                }
                return null;
            }
        }).filter(Predicates.notNull()).toList();

        List<Channel> channels = channelRepository.findByGroupIn(groups);

        if(params.getGroups().contains(-1)) {
            channels.addAll(channelRepository.findByGroupNull());
        }

        List<Integer> channelIds = FluentIterable.from(channels).transform(new Function<Channel, Integer>() {

                @Nullable
                @Override
                public Integer apply(@Nullable Channel channel) {
                    if (channel != null) {
                        return channel.getId();
                    }
                    return null;
                }
            }).filter(Predicates.notNull()).toList();


        predicates.add(root.<Integer>get("channelId").in(channelIds));
	    
        predicates.add(root.<Integer>get("type").in(params.getTypes()));

        if (StringUtils.isNotBlank(params.getChannelName())) {
            predicates.add(criteriaBuilder.like(root.<String>get("channelName"), "%" + params.getChannelName() + "%"));
        }

        if (params.getChannelId() != null) {
            predicates.add(criteriaBuilder.equal(root.<Integer>get("channelId"), params.getChannelId()));
        }
		return predicates;
	}

}
