package com.arcsoft.supervisor.service.m3u8;

import org.apache.commons.lang3.SystemUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A parser implementation to parse the {@code lines} to {@code List<M3u8Info>}.
 *
 * @author zw.
 */
public class M3u8InfoElementParser implements M3u8ElementParser<List<M3u8Info>> {

    private static final String EXT_X_STREAM_INF = "#EXT-X-STREAM-INF";

    @Override
    public List<M3u8Info> parse(List<String> lines, String baseFolder) {
        checkNotNull(lines == null || lines.isEmpty(), "The base m3u8 content can't empty");
        List<M3u8Info> m3u8Infos = new ArrayList<>();
        for (Iterator<String> lineItr = lines.iterator(); lineItr.hasNext();){
            String curLine = lineItr.next();
            if (curLine.startsWith(EXT_X_STREAM_INF)){
                M3u8Info m3u8Info = new DefaultM3u8Info();
                m3u8Info.setPath(baseFolder + SystemUtils.FILE_SEPARATOR + lineItr.next());
                m3u8Infos.add(m3u8Info);
            }
        }
        return m3u8Infos;
    }
}
