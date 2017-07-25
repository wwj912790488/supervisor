package com.arcsoft.supervisor.service.log.impl;

import com.arcsoft.supervisor.commons.spring.SessionCallBack;
import com.arcsoft.supervisor.commons.spring.SessionTemplate;
import com.arcsoft.supervisor.commons.spring.event.EventReceiver;
import com.arcsoft.supervisor.exception.service.BusinessException;
import com.arcsoft.supervisor.exception.service.BusinessExceptionDescription;
import com.arcsoft.supervisor.model.domain.channel.Channel;
import com.arcsoft.supervisor.model.domain.channel.ChannelGroup;
import com.arcsoft.supervisor.model.domain.channel.ChannelRecordHistory;
import com.arcsoft.supervisor.model.domain.log.ContentDetectLog;
import com.arcsoft.supervisor.model.domain.log.QContentDetectLog;
import com.arcsoft.supervisor.model.vo.task.cd.ContentDetectResult;
import com.arcsoft.supervisor.repository.channel.ChannelRepository;
import com.arcsoft.supervisor.repository.log.ContentDetectLogRepository;
import com.arcsoft.supervisor.service.log.ContentDetectLogReactor;
import com.arcsoft.supervisor.service.log.ContentDetectLogService;
import com.arcsoft.supervisor.service.log.event.ContentDetectLogEvent;
import com.arcsoft.supervisor.service.m3u8.M3u8;
import com.arcsoft.supervisor.service.m3u8.PlayItem;
import com.arcsoft.supervisor.service.remote.RemoteExecutorServiceSupport;
import com.arcsoft.supervisor.service.task.impl.TaskStopEvent;
import com.arcsoft.supervisor.utils.NamedThreadFactory;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.mysema.query.BooleanBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author zw.
 */
@Service
public class DefaultContentDetectLogServiceImpl extends RemoteExecutorServiceSupport implements ContentDetectLogService {

    @Autowired
    private ContentDetectLogRepository detectLogRepository;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private SessionTemplate sessionTemplate;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private M3u8 m3u8;


    @Autowired(required = false)
    private List<ContentDetectLogReactor> detectLogReactors;

    private final DelayQueue<DelayedRequest> delayedCreateM3u8Queue;

    private static final String CHILD_M3U8 = "1.m3u8";

    private static final long M3U8_DELAYED_SECONDS = 70;

    private final ExecutorService m3u8MakerPool;

    private final ExecutorService contentDetectLogReactorPool;

    public DefaultContentDetectLogServiceImpl() {
        this.m3u8MakerPool = Executors.newSingleThreadExecutor(NamedThreadFactory.create("ContentDetectLogService"));
        this.contentDetectLogReactorPool = Executors.newCachedThreadPool(NamedThreadFactory.create("ContentDetectLogReactor"));
        this.delayedCreateM3u8Queue = new DelayQueue<>();
    }

    @PostConstruct
    public void init() {
        if (SystemUtils.IS_OS_LINUX) {
            this.m3u8MakerPool.execute(new ContentDetectM3u8Maker());
        }
    }

    @PreDestroy
    public void destroy() {
        if (SystemUtils.IS_OS_LINUX) {
            this.delayedCreateM3u8Queue.put(new DelayedStopRequest(1));
        }
        this.m3u8MakerPool.shutdown();
        this.contentDetectLogReactorPool.shutdown();
    }

    @Override
    public void save(ContentDetectResult result) {

    }

    @Override
    @Transactional
    public void deleteByChannelId(final Integer id) {
        transactionTemplate.execute(new TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus status) {
                detectLogRepository.deleteByChannelId(Integer.valueOf(id));
                return null;
            }

        });
    }


    @Override
    public void deleteByIds(List<ContentDetectLog> contentDetectLogs) {
        detectLogRepository.deleteInBatch(contentDetectLogs);
    }

    public long getCount() {
        return detectLogRepository.count();
    }

    public ContentDetectLog findLatestOne() {
        return detectLogRepository.findLatestOne();
    }

    @Override
    public ContentDetectLog getById(long id) {
        return detectLogRepository.findOne(id);
    }

    @Override
    public void updateVideoFilePathAndOffset(long id, long offset, String path) {
        ContentDetectLog log = detectLogRepository.findOne(id);
        if (log != null) {
            log.setVideoFilePath(path);
            log.setStartOffset(offset);
        }
    }

    @Override
    public void cleanVideoFilePathDeleted(String path) {
        List<ContentDetectLog> logs = detectLogRepository.findByVideoFilePath(path);
        for (ContentDetectLog log : logs) {
            log.setVideoFilePath(null);
            log.setStartOffset(null);
        }
    }

    @Override
    public BusinessExceptionDescription updateConfirmDate(long id) {
        ContentDetectLog log = detectLogRepository.findOne(id);
        if (log != null) {
            try {
                if (log.getConfirmdate() != null)
                    return BusinessExceptionDescription.LOG_ALREADY_CONFIRMED;

                Date current = new Date();
                log.setConfirmdate(current);
                detectLogRepository.save(log);
            } catch (Exception e) {
                return BusinessExceptionDescription.USER_CONFIG_INCOMPLETE.ERROR;
            }

            return BusinessExceptionDescription.OK;
        }

        return BusinessExceptionDescription.LOG_NOT_EXIST;
    }

    @Override
    public Page<ContentDetectLog> paginate(ContentDetectQueryParams params, PageRequest pageRequest) {
        if (params == null) {
            return detectLogRepository.findAll(pageRequest);
        }
        BooleanBuilder queryBuilder = constructQueryBuilder(params);
        return detectLogRepository.findAll(queryBuilder, pageRequest);
    }

    @Override
    public List<ContentDetectLog> findAll(ContentDetectQueryParams params) {
        if (params == null) {
            return detectLogRepository.findAll();
        }
        return detectLogRepository.find(params);
    }

    @Override
    public List<ContentDetectLog> findAll(ChannelsContentDetectQueryParams params) {
        return detectLogRepository.find(params);
    }

    public List<ContentDetectLog> findByAllType(ChannelsContentDetectQueryParams params) {
        return detectLogRepository.findByAllType(params);
    }

    @Override
    public Long getCount(ContentDetectQueryParams params) {
        if (params == null) {
            return detectLogRepository.count();
        }
        BooleanBuilder queryBuilder = constructQueryBuilder(params);

        return detectLogRepository.count(queryBuilder);
    }

    @Override
    public List<ContentDetectLog> getByChannelName(String channelName) {
        return detectLogRepository.getByChannelName(channelName);
    }

    private BooleanBuilder constructQueryBuilder(ContentDetectQueryParams params) {
        QContentDetectLog qContentDetectLog = QContentDetectLog.contentDetectLog;
        BooleanBuilder queryBuilder = new BooleanBuilder();
        if (params.getStartTime() != null && params.getEndTime() != null) {
            queryBuilder.and(qContentDetectLog.startTime.between(params.getStartTime().getTime(),
                    params.getEndTime().getTime()));
        }

        List<ChannelGroup> groups = FluentIterable.from(params.getGroups()).transform(new Function<Integer, ChannelGroup>() {
            @Nullable
            @Override
            public ChannelGroup apply(Integer input) {
                if (input != -1) {
                    ChannelGroup group = new ChannelGroup();
                    group.setId(input);
                    return group;
                }
                return null;
            }
        }).filter(Predicates.notNull()).toList();

        List<Channel> channels = channelRepository.findByGroupIn(groups);

        if (params.getGroups().contains(-1)) {
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

        queryBuilder.and(qContentDetectLog.type.in(params.getTypes()));
        queryBuilder.and(qContentDetectLog.channelId.in(channelIds));

        if (StringUtils.isNotBlank(params.getChannelName())) {
            queryBuilder.and(qContentDetectLog.channelName.like("%" + params.getChannelName() + "%"));
        }
        return queryBuilder;
    }

    /**
     * Process <code>ContentDetectLogEvent</code>.
     *
     * @param event the event of content detect log
     */
    @EventReceiver(value = ContentDetectLogEvent.class)
    public void onContentDetectLogEvent(ContentDetectLogEvent event) {
        callReactors(event.getContentDetectLog());
        if (event.isEnableRecord()) {
            delayedCreateM3u8Queue.put(new DelayedM3u8Request(M3U8_DELAYED_SECONDS * 1000, event.getContentDetectLog()));
        }
    }

    @EventReceiver(value = TaskStopEvent.class)
    @Transactional
    public void onTaskStopped(TaskStopEvent event) {
        detectLogRepository.updateEndTime(event.getTaskId(), event.getStopTime().getTime());

    }

    private void callReactors(ContentDetectLog log) {
        if (detectLogReactors != null) {
            for (ContentDetectLogReactor reactor : detectLogReactors) {
                contentDetectLogReactorPool.execute(new ContentDetectReactorCaller(log, reactor));
            }
        }
    }

    /**
     * Async caller for each reactor of {@link #detectLogReactors}.
     */
    private final class ContentDetectReactorCaller implements Runnable {

        private final ContentDetectLog detectLog;

        private final ContentDetectLogReactor reactor;

        private ContentDetectReactorCaller(ContentDetectLog detectLog, ContentDetectLogReactor reactor) {
            this.detectLog = detectLog;
            this.reactor = reactor;
        }

        @Override
        public void run() {
            try {
                reactor.react(detectLog);
            } catch (Exception e) {
                logger.error("Encounter exception while calling react of [" + reactor.getName() + "] " +
                        "with ContentDetect[id=" + detectLog.getId() + "]", e);
            }
        }
    }


    private abstract class DelayedRequest implements Delayed {

        private long startTime;

        /**
         * Delay milliseconds.
         */
        private final long delay;

        /**
         * Construct a new instance with given delay of milliseconds.
         *
         * @param delay the milliseconds
         */
        public DelayedRequest(long delay) {
            this.delay = delay;
            resetStartTime();
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(this.startTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            if (this == o) {
                return 0;
            }
            long diff = getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS);
            return (diff == 0 ? 0 : ((diff < 0) ? -1 : 1));
        }

        protected void resetStartTime() {
            this.startTime = this.delay + System.currentTimeMillis();
        }
    }

    /**
     * A delayed request class to do create m3u8 file lazy.
     */
    private class DelayedM3u8Request extends DelayedRequest {

        private final ContentDetectLog contentDetectLog;
        private byte count = 0;

        public DelayedM3u8Request(long delay, ContentDetectLog contentDetectLog) {
            super(delay);
            this.contentDetectLog = contentDetectLog;
        }

        public ContentDetectLog getContentDetectLog() {
            return contentDetectLog;
        }

        public boolean canReDelayed() {
            return this.count < 3; //Do max 3 times retry if failed to create m3u8 file.
        }

        public void reDelay() {
            this.count++;
            resetStartTime();
        }
    }

    /**
     * A poison class indicates the queue have to be stop.
     */
    private class DelayedStopRequest extends DelayedRequest {

        public DelayedStopRequest(long delay) {
            super(delay);
        }
    }


    /**
     * A maker object to create m3u8 file base on content detect log.
     */
    private final class ContentDetectM3u8Maker implements Runnable {

        @Override
        public void run() {
            logger.info("Start ContentDetectM3u8Maker");
            while (true) {
                try {
                    DelayedRequest delayedRequest = delayedCreateM3u8Queue.take();
                    if (delayedRequest instanceof DelayedStopRequest) {
                        logger.debug("ContentDetectM3u8Maker will be stop because it receive a stop request");
                        break;
                    }
                    DelayedM3u8Request m3u8Request = (DelayedM3u8Request) delayedRequest;
                    if (!makeM3u8File(m3u8Request) && m3u8Request.canReDelayed()) {
                        m3u8Request.reDelay();
                        delayedCreateM3u8Queue.put(m3u8Request);
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.debug("Occurs InterruptedException the ContentDetectM3u8Maker will be stop");
                    break;
                } catch (Exception e) {
                    logger.error("", e);
                }
            }
            logger.info("Stop ContentDetectM3u8Maker");
        }

        private boolean makeM3u8File(DelayedM3u8Request request) {
            ContentDetectLog log = request.getContentDetectLog();
            boolean hasCreateM3u8File = false;
            if (log.getStartTime() != null && log.getStartTime() > 0
                    && log.getEndTime() != null && log.getEndTime() > 0
                    && log.getChannelId() != null) {
                try {
                    String channelRecordFilePath = getChannelRecordFilePathByContentDetectLogStartTime(log.getChannelId(),
                            log.getStartTime());
                    if (channelRecordFilePath != null) {
                        final ImmutablePair<String, List<PlayItem>> pathAndItemsPair = m3u8.createFromChildM3u8ByTimePeriod(channelRecordFilePath,
                                log.getStartTime(), log.getEndTime());
                        if (pathAndItemsPair != null) {
                            updateContentDetectLogVideoFilePathAndOffset(log.getId(),
                                    pathAndItemsPair.getLeft(),
                                    log.getStartTime() - pathAndItemsPair.getRight().get(0).getStartTime()
                            );
                            hasCreateM3u8File = true;
                            logger.info("Success create m3u8 file " + pathAndItemsPair.getLeft() + " for ContentDetectLog[id={}]", log.getId());
                        } else {
                            logger.error("Failed to create m3u8 with ContentDetectLog[id=" + log.getId() + "]." +
                                    "The items is empty.");
                        }
                    } else {
                        logger.debug("Skip ContentDetectLog[id={}] due to record file path is empty", log.getId());
                    }

                } catch (Exception e) {
                    logger.error("Failed to create m3u8 with ContentDetectLog[id=" + log.getId() + "]", e);
                }
            } else {
                //Avoid invalid content detect log
                hasCreateM3u8File = true;
            }
            return hasCreateM3u8File;
        }

        private String getChannelRecordFilePathByContentDetectLogStartTime(final int channelId, final long startTime) {
            return sessionTemplate.execute(new SessionCallBack<String>() {
                @Override
                public String doInSession() {
                    Channel channel = channelRepository.findOne(channelId);
                    if (channel != null) {
                        Optional<ChannelRecordHistory> historyOptional = channel.getHistoryByStartTime(startTime);
                        return historyOptional.isPresent()
                                ? historyOptional.get().getRecordBasePath() + SystemUtils.FILE_SEPARATOR + CHILD_M3U8
                                : null;
                    }
                    return null;
                }
            });
        }

        private void updateContentDetectLogVideoFilePathAndOffset(final long id, final String path, final long offset) {
            transactionTemplate.execute(new TransactionCallback<Void>() {
                @Override
                public Void doInTransaction(TransactionStatus status) {
                    DefaultContentDetectLogServiceImpl.this.updateVideoFilePathAndOffset(
                            id,
                            offset,
                            path
                    );
                    return null;
                }
            });
        }
    }


    @Override
    public void delete(final ContentDetectQueryParams params) {
        transactionTemplate.execute(new TransactionCallback<Void>() {

            @Override
            public Void doInTransaction(TransactionStatus status) {
                detectLogRepository.delete(params);
                return null;
            }

        });

    }


}
