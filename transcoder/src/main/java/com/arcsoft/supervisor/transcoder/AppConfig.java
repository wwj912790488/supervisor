package com.arcsoft.supervisor.transcoder;

import com.arcsoft.supervisor.utils.ConfigVarProperties;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.Writer;
import java.util.Properties;

/**
 * app config file
 *
 * @author Bing
 */
public class AppConfig {
    //
    // property key
    //

    private static Logger logger = Logger.getLogger(AppConfig.class);

    /**
     * debug mode: true or false(default)
     */
    public static final String KEY_DEBUG = "debug";
    /**
     * PRODUCT - mainly used to resolve some unnormal issue
     */
    public static final String KEY_PRODUCT = "PRODUCT";
    /**
     * ui list page size
     */
    public static final String KEY_UILIST_PAGESIZE = "uilist_pagesize";
    /**
     * max file info cache count
     */
    public static final String KEY_MAX_FILE_INFO_CACHE_COUNT = "max_file_info_cache_count";
    /**
     * max transcoder count
     */
    public static final String KEY_MIN_GET_PROGRESS_INTERVAL = "min_get_progress_interval";
    /**
     * max transcoder count
     */
    public static final String KEY_MIN_GET_THUMB_INTERVAL = "min_get_thumb_interval";
    /**
     * transcoder work directory
     */
    public static final String KEY_TRANSCODER_WORK_DIR = "transcoder_work_dir";
    /**
     * max transcoder count
     */
    public static final String KEY_MAX_TRANSCODER_COUNT = "max_transcoder_count";
    /**
     * max outputgroup count
     */
    public static final String KEY_MAX_OUTPUTGROUP_COUNT = "max_outputgroup_count";
    /**
     * max hd count
     */
    public static final String KEY_MAX_HDOUTPUT_COUNT = "max_hdoutput_count";
    /**
     * max hd count
     */
    public static final String KEY_MAX_SDOUTPUT_COUNT = "max_sdoutput_count";
    /**
     * transcoder file path
     */
    public static final String KEY_TRANSCODER_PATH = "transcoder_path";
    /**
     * quick transcoder file path
     */
    public static final String KEY_QUICK_TRANSCODER_PATH = "quick_transcoder_path";
    /**
     * media info exe path
     */
    public static final String KEY_MEDIAINFO_PATH = "mediainfo_path";
    /**
     * transcoder manager server file path
     */
    public static final String KEY_TRANS_SVR_PATH = "trans_svr_path";
    /**
     * transcoder manager server port
     */
    public static final String KEY_TRANS_SVR_PORT = "trans_svr_port";
    /**
     * clip exe path
     */
    public static final String KEY_CLIP_EXE_PATH = "clip_exe_path";
    /**
     * max clip handler
     */
    public static final String KEY_MAX_CLIP_HANDLER = "max_clip_handler";
    /**
     * mix clip handler which keeps alive to wait command
     */
    public static final String KEY_MIN_CLIP_HANDLER = "min_clip_handler";
    /**
     * schedule command
     */
    public static final String KEY_LOOP_RUN_CMD = "loop_run_cmd";
    /**
     * schedule execute time
     */
    public static final String KEY_LOOP_RUN_CMD_INTERVAL = "loop_run_cmd_interval";
    /**
     * link path
     */
    public static final String KEY_LINK_PATH = "link_path";
    /**
     * list path
     */
    public static final String KEY_LIST_PATH = "storage.dir";
    /**
     * transcoder tmp ftp dir
     */
    public static final String TRANSCODER_TEMP_FTP_DIR = "transcoder_temp_ftp_dir";
    /**
     * default 0, our native transcoder support ftp read already.
     */
    public static final String TRANSCODER_FTP_DOWNLOAD = "transcoder_ftp_download";
    /**
     * default 1
     */
    public static final String TRANSCODER_FTP_UPLOAD = "transcoder_ftp_upload";
    /**
     * quick transcoder slide count
     */
    public static final String QUICK_TRANSCODER_SLICE_COUNT = "quick_transcoder.slice_count";

    public static final String I18N_LANGUAGE = "i18n.language";
    public static final String POST_PROCESS_PARAM_TYPE = "post_process_param_type";

    /**
     * properties
     */
    private static Properties props = null;
    private static ConfigVarProperties appConfig;
    static {
        reload();
    }

    public static void reload() {
        logger.info("--- load application configurations ... ");
        Properties theProps = new Properties();
        System.out.println("utils:"+AppConfig.class.getResource("/").getPath());
        System.out.println(System.getProperty("user.dir"));

        try {
            String cfgPath = System.getProperty("agent.config");
            if (cfgPath == null) {
                cfgPath = System.getenv("arcvideo.application.config");
            }

            if (cfgPath == null) {
                theProps.load(AppConfig.class.getResourceAsStream("/config.properties"));
            } else {
                FileInputStream fis = new FileInputStream(cfgPath);
                theProps.load(fis);
                fis.close();
            }
        } catch (Exception e) {
            logger.info(null, e);
        }
        props = theProps;
    }

    public static void print(Writer writer) {
        try {
            //props.store(writer, null);
            for (Object k : props.keySet()) {
                writer.append(k.toString()).append(':').append(props.getProperty(k.toString())).write("\r\n");
            }
        } catch (Exception e) {
            logger.info(e);
        }
    }

    public static void setProperty(String k, String v) {
        cDebugMask = null;
        props.setProperty(k, v);
    }

    /**
     * @param key
     * @return val or null
     */
    public static String getProperty(String key) {
        return props.getProperty(key);
    }

    /**
     * getPropertyAsLong
     * - value format examples: -1, 1, 0xff
     *
     * @param key
     * @return val or null
     */
    public static Long getPropertyAsLong(String key) {
        Long ret = null;
        String sVal = getProperty(key);
        if (sVal != null && sVal.length() > 0) {
            ret = Long.decode(sVal);
        }
        return ret;
    }

    /**
     * get property as int,
     *
     * @param key
     * @param defaultVal
     * @return
     */
    public static int getPropertyAsint(String key, int defaultVal) {
        Long i = getPropertyAsLong(key);
        return i == null ? defaultVal : i.intValue();
    }

    /**
     * the debug mask at config file
     *
     * @return 0 (default, no debug out) or mask
     */
    public static int getDebugMask() {
        if (cDebugMask == null) {
            int m = 0;
            String d = getProperty(KEY_DEBUG);
            if (d != null) {
                try {
                    m = Long.decode(d).intValue();
                } catch (Exception e) {
                    logger.error(null, e);
                }
            }
            cDebugMask = m;
        }
        return cDebugMask.intValue();
    }

    private static Integer cDebugMask = null;
}
