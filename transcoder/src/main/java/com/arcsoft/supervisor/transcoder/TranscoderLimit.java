package com.arcsoft.supervisor.transcoder;


/**
 * default by config, subclass can overwrite it, eg by license.
 *
 * @author Bing
 */
public class TranscoderLimit {
    // default value

    private static final int DEFAULT_MAX_TRANSCODER_COUNT = 256;
    private static final int DEFAULT_MAX_OUTPUT_GROUP_COUNT = 256;

    public int getMaxTranscoderCount() {
        return AppConfig.getPropertyAsint(AppConfig.KEY_MAX_TRANSCODER_COUNT, DEFAULT_MAX_TRANSCODER_COUNT);
    }

    public int getMaxHDCount() {
        return AppConfig.getPropertyAsint(AppConfig.KEY_MAX_HDOUTPUT_COUNT, Integer.MAX_VALUE);
    }

    public int getMaxSDCount() {
        return AppConfig.getPropertyAsint(AppConfig.KEY_MAX_SDOUTPUT_COUNT, Integer.MAX_VALUE);
    }

    public int getMaxOutputGroupCount() {
        return AppConfig.getPropertyAsint(AppConfig.KEY_MAX_OUTPUTGROUP_COUNT, DEFAULT_MAX_OUTPUT_GROUP_COUNT);
    }

    public int getMaxSDICount() {
        return AppConfig.getPropertyAsint("max_sdi_count", 256);
    }

    public int getMaxASICount() {
        return AppConfig.getPropertyAsint("max_asi_count", 256);
    }

    public int getMaxSupperTaskCount() {
        return AppConfig.getPropertyAsint("max_supper_task_count", 1);
    }

}
