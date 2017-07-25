package com.arcsoft.supervisor.service.channel.impl;

import com.arcsoft.supervisor.commons.HttpClientUtils;
import com.arcsoft.supervisor.commons.json.JsonMapper;
import com.arcsoft.supervisor.commons.spring.event.EventReceiver;
import com.arcsoft.supervisor.model.domain.channel.Channel;
import com.arcsoft.supervisor.model.domain.channel.ChannelRecordTask;
import com.arcsoft.supervisor.model.domain.log.ContentDetectLog;
import com.arcsoft.supervisor.model.domain.system.ChannelRecordConfiguration;
import com.arcsoft.supervisor.repository.channel.ChannelRecordTaskRepository;
import com.arcsoft.supervisor.service.ServiceSupport;
import com.arcsoft.supervisor.service.channel.*;
import com.arcsoft.supervisor.service.channel.event.ChannelRemovedEvent;
import com.arcsoft.supervisor.service.channel.event.ChannelSavedEvent;
import com.arcsoft.supervisor.service.log.ContentDetectLogReactor;
import com.arcsoft.supervisor.service.log.ContentDetectLogService;
import com.arcsoft.supervisor.service.settings.impl.ChannelRecordConfigurationService;
import com.google.common.base.Optional;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.Seconds;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.*;

@Service
public class DefaultChannelRecordService extends ServiceSupport implements ChannelRecordService, ContentDetectLogReactor{

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private ChannelRecordTaskRepository channelRecordTaskRepository;

    @Autowired
    private ChannelRecordConfigurationService channelRecordConfigurationService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private FileDeleteStrategy fileDeleteStrategy;

    private Future regularDeleteTask=null;

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    private ScheduledExecutorService contentDetectReactExecutor = Executors.newSingleThreadScheduledExecutor();

    @Autowired
    private ContentDetectLogService contentDetectLogService;

    @Override
    public String getName() {
        return "RecordContentDetectLogReactor";
    }

    @Override
    public void react(final ContentDetectLog contentDetectLog) {
        if(contentDetectLog==null) {
            logger.info("Null pointer contentDetectLog");
            return;
        }
        Integer channelId = contentDetectLog.getChannelId();
        if(channelId == null) {
            logger.info("Null pointer channelId");
            return;
        }
        Channel channel = channelService.getById(channelId);
        ChannelRecordConfiguration cfg = channelRecordConfigurationService.getFromCache();
        if(cfg==null){
            logger.info("empty record cfg");
            return;
        }
        final String channelName = channel.getName();
        final Long id = contentDetectLog.getId();
        String storage = cfg.getSupervisorStoragePath();
        final String recordDir = storage==null?"":storage;
        String storage2 = cfg.getContentDetectStoragePath();
        final String cdDir = storage2==null?"":storage2;
        String guid = contentDetectLog.getGuid();
        final Date startTime = contentDetectLog.getStartTimeAsDate();
        if(channel.getEnableTriggerRecord() && contentDetectLog.getEndTime() == 0) {
            contentDetectReactExecutor.schedule(new Runnable() {
                @Override
                public void run() {
                    try {
                        transactionTemplate.execute(new TransactionCallback<Void>() {
                            @Override
                            public Void doInTransaction(TransactionStatus status) {
                                File channelDir = new File(cdDir + channelName);
                                File channelRecordDir = new File(recordDir + channelName);
                                if(!channelDir.exists()) {
                                    try {
                                        Files.createDirectories(channelDir.toPath());
                                    } catch (IOException e) {
                                        logger.debug(e.toString());
                                        return null;
                                    }
                                }
                                Optional<ImmutablePair<File, Long>> resultcd = findContentDetectVideo(startTime, channelDir);
                                if(resultcd.isPresent()) {
                                    contentDetectLogService.updateVideoFilePathAndOffset(id, resultcd.get().getRight(), resultcd.get().getLeft().getPath());
                                } else {
                                    Optional<ImmutablePair<File, Long>> result = findContentDetectVideo(startTime, channelRecordDir);
                                    if(result.isPresent()) {
                                        Path source = result.get().getLeft().toPath();
                                        Path targetDir = channelDir.toPath();
                                        try {
                                            Files.copy(source, targetDir.resolve(source.getFileName()));
                                        } catch (IOException e) {
                                            logger.debug(e.toString());
                                            return null;
                                        }
                                        contentDetectLogService.updateVideoFilePathAndOffset(id, result.get().getRight(), targetDir.resolve(source.getFileName()).toFile().getPath());
                                    }

                                }
                                return null;
                            }
                        });
                    } catch(Exception e) {
                        logger.debug(e.toString());
                    }

                }
            }, 11, TimeUnit.MINUTES);
        }

    }

    private Optional<ImmutablePair<File, Long>> findContentDetectVideo(Date startTime, File rootDir) {
        if(rootDir.isDirectory()) {
            File[] files = rootDir.listFiles();
            for(File file : files) {
                String fileName = file.getName();
                Long startOffset = isContentDetectVideo(fileName, startTime);
                if(startOffset >= 0) {
                    return Optional.of(new ImmutablePair<File, Long>(file, startOffset));
                }
            }
        }
        return Optional.absent();
    }

    private Long isContentDetectVideo(String fileName, Date startTime) {
        String[] fileNameParts = fileName.split("\\.");
        if(fileNameParts.length <= 1) {
            logger.debug("Unknown file extension format");
            return -1l;
        }
        String file = fileNameParts[fileNameParts.length - 2];
        String[] parts = file.split("-");
        if(parts.length > 1) {
            String fileDateTimeString = parts[parts.length - 2] + "-" + parts[parts.length - 1];
            LocalDateTime fileDateTime = null;
            try {
                fileDateTime = LocalDateTime.parse(fileDateTimeString, DateTimeFormat.forPattern("yyyyMMdd-HHmmss"));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            LocalDateTime fileEndDateTime;
            int minutes = (fileDateTime.getMinuteOfHour()/10+1)*10 - fileDateTime.getMinuteOfHour();
            fileEndDateTime = fileDateTime.plusMinutes(minutes).withSecondOfMinute(0);
            LocalDateTime startDateTime = new LocalDateTime(startTime, DateTimeZone.forTimeZone(TimeZone.getDefault()));
            if(fileDateTime != null && fileDateTime.minusSeconds(1).isBefore(startDateTime) && fileEndDateTime.isAfter(startDateTime)) {
                logger.debug(fileDateTime.toString());
                logger.debug(startDateTime.toString());
                logger.debug("match");
                return Long.valueOf(Seconds.secondsBetween(fileDateTime, startDateTime).getSeconds());
            } else {
                logger.debug(fileDateTime.toString());
                logger.debug(startDateTime.toString());
                logger.debug("not match");
                return -1l;
            }
        } else {
            logger.debug("Unknown file name format");
            return -1l;
        }
    }


    public static class RegularFileDeleter extends SimpleFileVisitor<Path> {

        private FileDeleteStrategy fileDeleteStrategy;

        private ContentDetectLogService contentDetectLogService;

        public RegularFileDeleter(FileDeleteStrategy fileDeleteStrategy, ContentDetectLogService contentDetectLogService) {
            this.fileDeleteStrategy = fileDeleteStrategy;
            this.contentDetectLogService = contentDetectLogService;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            if(fileDeleteStrategy.shouldDelete(file)) {
                contentDetectLogService.cleanVideoFilePathDeleted(file.toString());
                Files.delete(file);
            }
            return FileVisitResult.CONTINUE;
        }
    }

    @Override
    @PostConstruct
    public void startRegularFileDeleter() {
        if(regularDeleteTask != null) {
            regularDeleteTask.cancel(false);
        }
        regularDeleteTask = executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                logger.debug("clean content detect video file");
                transactionTemplate.execute(new TransactionCallback<Void>() {
                    @Override
                    public Void doInTransaction(TransactionStatus status) {
                        ChannelRecordConfiguration cfg = channelRecordConfigurationService.getFromCache();
                        String storagePath = cfg.getContentDetectStoragePath();
                        if(!StringUtils.isBlank(storagePath)) {
                            File file = new File(storagePath);
                            if (file.exists() && file.isDirectory()) {
                                Path path = file.toPath();
                                try {
                                    Files.walkFileTree(path, new RegularFileDeleter(fileDeleteStrategy, contentDetectLogService));
                                } catch (IOException e) {
                                    logger.debug(e.toString());
                                }
                            }
                        }
                        return null;
                    }
                });
            }
        }, 0, 1, TimeUnit.HOURS);
    }

    @PreDestroy
    public void stopRegularFileDeleter() {
        executor.shutdown();
    }

    @EventReceiver(ChannelSavedEvent.class)
    public void onChannelSaved(final ChannelSavedEvent event) {
        transactionTemplate.execute(new TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus status) {
                Integer channelId = event.getChannelId();
                Channel channel = channelService.getById(channelId);
                List<ChannelRecordTask> tasks = channelRecordTaskRepository.findBySupervisorChannelId(channelId);
                if(channel.getEnableRecord() || channel.getEnableTriggerRecord()) {
                    if(tasks == null || tasks.isEmpty()) {
                        startRecord(channelId);
                    }
                } else {
                    if(tasks != null && !tasks.isEmpty()) {
                        stopRecord(channelId);
                    }
                }
                return null;
            }
        });
    }

    @EventReceiver(ChannelRemovedEvent.class)
    public void onChannelRemoved(final ChannelRemovedEvent event) {
        transactionTemplate.execute(new TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus status) {
                Integer channelId = event.getChannelId();
                stopRecord(channelId);
                return null;
            }
        });
    }

    @Override
    public boolean exist() {
        ChannelRecordConfiguration cfg = channelRecordConfigurationService.getFromCache();
        String domain = cfg.getDomain();
        boolean exist = false;
        try {
            String getResponse = HttpClientUtils.doGetJSON(domain + "api/channels/1");
            RecordResponse get = JsonMapper.getMapper().readValue(getResponse, RecordResponse.class);
            if(get != null && get.getCode() == 0) {
                exist = true;
            }
        } catch (IOException e) {
        }
        return exist;
    }

    @Override
    public void startRecord(Integer channelId) {
        ChannelRecordConfiguration cfg = channelRecordConfigurationService.getFromCache();
        Channel channel = channelService.getById(channelId);
        RecordChannelData channelToAdd = new RecordChannelData(channel.getName(), channel.getIp(), Integer.parseInt(channel.getProgramId()), Integer.parseInt(channel.getAudioId()));
        try {
            String channelResponse = HttpClientUtils.doPostJSON(cfg.getDomain() + "api/channel", channelToAdd);
            logger.info(channelResponse);
            RecordResponse channelResult = JsonMapper.getMapper().readValue(channelResponse, RecordResponse.class);
            if (channelResult != null && channelResult.getCode() == 0) {
                Integer recordChannelId = channelResult.getId();
                RecordTaskData task = new RecordTaskData(channel.getName(), recordChannelId, cfg.getProfileId(), cfg.getRecorderStoragePath() + channel.getName(), cfg.getKeepTime());
                String taskResponse = HttpClientUtils.doPostJSON(cfg.getDomain() + "api/record/fulltime", task);
                logger.info(taskResponse);
                RecordResponse taskResult = JsonMapper.getMapper().readValue(taskResponse, RecordResponse.class);
                if (taskResult != null && taskResult.getCode() == 0) {
                    Integer recordTaskId = taskResult.getId();
                    ChannelRecordTask recordTask = new ChannelRecordTask(channelId, recordChannelId, recordTaskId);
                    channelRecordTaskRepository.save(recordTask);
                }
            }

        } catch (IOException e) {
        }
    }

    @Override
    public void stopRecord(Integer channelId) {
        List<ChannelRecordTask> tasks = channelRecordTaskRepository.findBySupervisorChannelId(channelId);
        for (ChannelRecordTask task : tasks) {
            stopRecord(task);
        }
    }

    public void stopRecord(ChannelRecordTask task) {
        try {
            ChannelRecordConfiguration cfg = channelRecordConfigurationService.getFromCache();
            String taskResponse = HttpClientUtils.doDeleteJSON(cfg.getDomain() + "api/record/" + task.getRecordTaskId());
            logger.info(taskResponse);
            RecordResponse taskResult = JsonMapper.getMapper().readValue(taskResponse, RecordResponse.class);
            if(taskResult != null && taskResult.getCode() == 0) {
                String channelResponse = HttpClientUtils.doDeleteJSON(cfg.getDomain() + "api/channel/" + task.getRecordChannelId());
                logger.info(channelResponse);
                RecordResponse channelResult = JsonMapper.getMapper().readValue(channelResponse, RecordResponse.class);
                if(channelResult != null && channelResult.getCode() == 0) {
                    channelRecordTaskRepository.delete(task);
                }
            }
        } catch(IOException e) {

        }
    }
}
