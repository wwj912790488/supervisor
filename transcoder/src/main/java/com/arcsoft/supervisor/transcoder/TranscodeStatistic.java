package com.arcsoft.supervisor.transcoder;

import com.arcsoft.supervisor.transcoder.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


/**
 * @author Bing
 */
public class TranscodeStatistic {
    private static final Logger LOG = LoggerFactory.getLogger(TranscodeStatistic.class);
    public int duration = 0;
    public int transcodingTime = 0;
    public int postprocessTime = 0;


    /**
     * loadFromFile
     *
     * @param f
     * @return
     */
    public static TranscodeStatistic loadFromFile(File f) {
        // <transcodertime>111</transcodertime >
        // <postprocesstime>111</ postprocesstime>
        // <duration>111</duration >
        TranscodeStatistic st = new TranscodeStatistic();
        if (!f.exists())
            return st;

        String cnt = new String(FileUtils.getFullFile(f));

        String v, t;
        int p0, p1;

        t = "<transcodertime>";
        p0 = cnt.indexOf(t, 0);
        if (p0 != -1) {
            p0 += t.length();
            p1 = cnt.indexOf("</transcodertime>", p0);
            if (p1 != -1) {
                v = cnt.substring(p0, p1);
                try {
                    if (v != null && (v = v.trim()).length() != 0) {
                        st.transcodingTime = Integer.parseInt(v);
                    }
                } catch (NumberFormatException e) {
                    LOG.error("", e);
                }
            }
        }

        t = "<postprocesstime>";
        p0 = cnt.indexOf(t, 0);
        if (p0 != -1) {
            p0 += t.length();
            p1 = cnt.indexOf("</postprocesstime>", p0);
            if (p1 != -1) {
                v = cnt.substring(p0, p1);
                try {
                    if (v != null && (v = v.trim()).length() != 0) {
                        st.postprocessTime = Integer.parseInt(v);
                    }
                } catch (NumberFormatException e) {
                    LOG.error("", e);
                }
            }
        }

        t = "<duration>";
        p0 = cnt.indexOf(t, 0);
        if (p0 != -1) {
            p0 += t.length();
            p1 = cnt.indexOf("</duration>", p0);
            if (p1 != -1) {
                v = cnt.substring(p0, p1);
                try {
                    if (v != null && (v = v.trim()).length() != 0) {
                        st.duration = (int) Long.parseLong(v);
                    }
                } catch (NumberFormatException e) {
                    LOG.error("", e);
                }
            }
        }

        return st;
    }
}
