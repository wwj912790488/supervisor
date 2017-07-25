package com.arcsoft.supervisor.service.m3u8;

import org.apache.commons.lang3.SystemUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import javax.annotation.concurrent.ThreadSafe;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A element parser implementation to parse {@code lines} to {@code List<PlayItem>}.
 *
 * @author zw.
 */
@ThreadSafe
public class PlayItemElementParser implements M3u8ElementParser<List<PlayItem>> {

    private static final String EXTINF = "#EXTINF";

    @Override
    public List<PlayItem> parse(List<String> lines, String baseFolder) {
        checkNotNull(lines == null || lines.isEmpty(), "The child m3u8 file content can't empty");
        List<PlayItem> playItems = new ArrayList<>();
        long endTime = 0;
        for (Iterator<String> lineItr = lines.iterator(); lineItr.hasNext(); ) {
            String line = lineItr.next();
            if (line.startsWith(EXTINF)) {
                PlayItem playItem = new DefaultPlayItem();
                playItem.setDuration(getDuration(line));
                String fileName = lineItr.next();
                playItem.setPath(baseFolder + SystemUtils.FILE_SEPARATOR + fileName);

                endTime = endTime == 0 ? getTimeByFileName(fileName) + playItem.getDuration()
                        : endTime + playItem.getDuration() + 1;

                playItem.setStartTime(endTime - playItem.getDuration());

                playItem.setEndTime(endTime);

                playItems.add(playItem);
            }
        }
        return playItems;
    }

    private long getDuration(String line) {
        String[] results = line.split(":");
        String duration = results[1].substring(0, results[1].length() - 1);
        return Long.valueOf(duration.replace(".", ""));
    }

    private long getTimeByFileName(String fileName){
        String time = fileName.split("-")[0];
        DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
                .appendPattern("yyyyMMdd'T'HHmmss")
                .toFormatter().withZoneUTC();
        //Because the time is UTC time, so we need doForward it to our locale time +08:00
        return DateTime.parse(time, dateTimeFormatter).withZone(DateTimeZone.forID("Asia/Shanghai")).getMillis();
    }
}
