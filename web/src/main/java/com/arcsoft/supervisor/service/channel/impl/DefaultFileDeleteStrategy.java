package com.arcsoft.supervisor.service.channel.impl;

import com.arcsoft.supervisor.model.domain.system.ChannelRecordConfiguration;
import com.arcsoft.supervisor.service.channel.FileDeleteStrategy;
import com.arcsoft.supervisor.service.settings.impl.ChannelRecordConfigurationService;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.TimeZone;

@Service
public class DefaultFileDeleteStrategy implements FileDeleteStrategy{

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ChannelRecordConfigurationService channelRecordConfigurationService;

    @Override
    public boolean shouldDelete(Path path) {
        logger.debug("Determine "  + path.toString() + " should be deleted");
        ChannelRecordConfiguration cfg = channelRecordConfigurationService.getFromCache();
        Integer keepTime = cfg.getContentDetectKeepTime();
        String fileName = path.getFileName().toString();
        String[] fileNameParts = fileName.split("\\.");
        if(fileNameParts.length <= 1) {
            logger.debug("Unknown file extension format");
            return false;
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

            LocalDateTime now = LocalDateTime.now(DateTimeZone.forTimeZone(TimeZone.getDefault()));
            if(fileDateTime != null && fileDateTime.plusMinutes(keepTime).isBefore(now)) {
                logger.debug(fileDateTime.toString());
                logger.debug(now.toString());
                logger.debug("should delete");
                return true;
            } else {
                return false;
            }
        } else {
            logger.debug("Unknown file name format");
            return false;
        }
    }
}
