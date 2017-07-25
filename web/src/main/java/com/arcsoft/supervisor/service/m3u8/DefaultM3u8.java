package com.arcsoft.supervisor.service.m3u8;

import com.arcsoft.supervisor.service.ServiceSupport;
import com.google.common.base.Converter;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A default implementation for {@code M3u8}.
 *
 * @author zw.
 */
@Service
public class DefaultM3u8 extends ServiceSupport implements M3u8 {

    /**
     * The parser instance used to parse base m3u8 file.
     */
    private M3u8ElementParser<List<M3u8Info>> m3u8ElementParser;

    /**
     * The parser instance used to parse child m3u8 file.
     */
    private M3u8ElementParser<List<PlayItem>> playItemElmentParser;

    /**
     * The converter instance to doForward the string represent path to {@code List<M3u8Info>}.
     */
    private PathToM3u8InfosConverter defaultPathToM3u8InfosConverter;

    /**
     * The converter instance to doForward the string represent path to {@code List<PlayItem>}.
     */
    private PathToPlayItemsConverter defaultPathToPlayItemsConverter;

    public DefaultM3u8() {
        this(new M3u8InfoElementParser(), new PlayItemElementParser());
    }

    public DefaultM3u8(M3u8ElementParser<List<M3u8Info>> m3u8ElementParser,
                       M3u8ElementParser<List<PlayItem>> playItemElmentParser) {
        this.m3u8ElementParser = m3u8ElementParser;
        this.playItemElmentParser = playItemElmentParser;
        this.defaultPathToM3u8InfosConverter = new PathToM3u8InfosConverter();
        this.defaultPathToPlayItemsConverter = new PathToPlayItemsConverter();
    }

    @Override
    public List<M3u8Info> parse(String m3u8Path) {
        List<M3u8Info> m3u8Infos = defaultPathToM3u8InfosConverter.doForward(m3u8Path);
        for (M3u8Info m3u8Info : m3u8Infos) {
            List<PlayItem> playItems = defaultPathToPlayItemsConverter.doForward(m3u8Info.getPath().toString());
            if (!playItems.isEmpty()) {
                m3u8Info.setPlayItems(playItems);
            }
        }
        return m3u8Infos;
    }

    @Override
    public List<PlayItem> parseFromChild(String m3u8Path) {
        return defaultPathToPlayItemsConverter.doForward(m3u8Path);
    }


    @Override
    public String create(List<PlayItem> playItems) {
        return defaultPathToPlayItemsConverter.doBackward(playItems);
    }

    @Override
    public ImmutablePair<String, List<PlayItem>> createFromChildM3u8ByTimePeriod(String childM3u8Path, long startTime, long endTime) {
        List<PlayItem> items = getPlayItemsByTimePeriod(parseFromChild(childM3u8Path), startTime, endTime);
        String path = create(items);
        return path == null ? null : ImmutablePair.of(path, items);
    }

    @Override
    public List<PlayItem> getPlayItemsByTimePeriod(List<PlayItem> playItems, long startTime, long endTime) {
        List<PlayItem> truncatePlayItem = new ArrayList<>();
        boolean isFindStart = false;
        for (PlayItem item : playItems) {
            if ((startTime >= item.getStartTime() && startTime <= item.getEndTime())
                    || isFindStart) {
                if (!isFindStart) {
                    isFindStart = true;
                }
                truncatePlayItem.add(item);
                if (item.getEndTime() >= endTime) {
                    break;
                }
            }

        }
        return truncatePlayItem;
    }


    protected class PathToM3u8InfosConverter extends Converter<String, List<M3u8Info>> {

        @Override
        protected List<M3u8Info> doForward(String s) {
            try {
                List<String> allLines = Files.readAllLines(Paths.get(s), Charset.forName("UTF-8"));
                return m3u8ElementParser.parse(allLines, Paths.get(s).getParent().toString());
            } catch (IOException e) {
                logger.error("Failed to load m3u8 file with " + s, e);
            }
            return Collections.emptyList();
        }

        @Override
        protected String doBackward(List<M3u8Info> m3u8Infos) {
            return null;
        }
    }

    protected class PathToPlayItemsConverter extends Converter<String, List<PlayItem>> {

        @Override
        protected List<PlayItem> doForward(String s) {
            try {
                List<String> allLines = Files.readAllLines(Paths.get(s), Charset.forName("UTF-8"));
                return playItemElmentParser.parse(allLines, Paths.get(s).getParent().toString());
            } catch (IOException e) {
                logger.error("Failed to load m3u8 file with " + s, e);
            }
            return Collections.emptyList();
        }

        @Override
        protected String doBackward(List<PlayItem> playItems) {
            if (playItems == null || playItems.isEmpty()){
                return null;
            }
            String folder = playItems.get(0).getPath().getParent().toString();
            String m3u8FileName = playItems.get(0).getStartTime() + "-" + playItems.get(playItems.size() - 1).getEndTime();
            File file = new File(folder + SystemUtils.FILE_SEPARATOR + m3u8FileName + ".m3u8");
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    logger.error("Failed to create m3u8 file with " + file.getAbsolutePath(), e);
                    return null;
                }
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write("#EXTM3U");
                    writer.newLine();
                    writer.write("#EXT-X-VERSION:3");
                    writer.newLine();
                    writer.write("#EXT-X-TARGETDURATION:60");
                    writer.newLine();
                    for (PlayItem item : playItems) {
                        writer.write("#EXTINF:" + (item.getDuration() / 1000.0) + ",");
                        writer.newLine();
                        writer.write(item.getPath().getFileName().toString());
                        writer.newLine();
                    }
                    writer.write("#EXT-X-ENDLIST");
                    writer.newLine();
                    writer.flush();
                } catch (IOException e) {
                    logger.error("Failed to write content to m3u8 file with " + file.getAbsolutePath(), e);
                    return null;
                }
            }
            return file.getAbsolutePath();
        }
    }
}
