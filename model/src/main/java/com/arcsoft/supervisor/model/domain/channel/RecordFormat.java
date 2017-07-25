package com.arcsoft.supervisor.model.domain.channel;

/**
 * Defines video format for record file.
 *
 * @author zw.
 */
enum RecordFormat {

    MP4("mp4"),
    HLS("m3u8");


    private final String format;

    RecordFormat(String format) {
        this.format = format;
    }

    public String getFormat() {
        return format;
    }
}
