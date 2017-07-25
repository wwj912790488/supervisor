package com.arcsoft.supervisor.transcoder.util;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class FileUtils {
    private static Logger logger = Logger.getLogger(FileUtils.class);

    /**
     * only for normal file
     *
     * @param f
     * @return
     */
    public static byte[] getFullFile(File f) {
        if (f == null)
            return null;
        byte[] buf = null;
        FileInputStream fis = null;
        try {
            buf = new byte[(int) f.length()];
            fis = new FileInputStream(f);
            fis.read(buf);
        } catch (Exception e1) {
            logger.info(e1);
            buf = null;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    logger.error(null, e);
                }
            }
        }
        return buf;
    }

    /**
     * read the whole text file into memory by lines
     *
     * @param fcfg
     * @param encoding encoding, or null
     * @return
     */
    public static List<String> readTextFile(File fcfg, String encoding) {
        LinkedList<String> ret = new LinkedList<String>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(encoding == null ? new FileReader(fcfg) : new InputStreamReader(new FileInputStream(fcfg), encoding));
            String line;
            while ((line = br.readLine()) != null) {
                ret.add(line);
            }
        } catch (Exception e) {
            logger.error(null, e);
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException e) {
                logger.error(null, e);
            }
        }
        return ret;
    }

    /**
     * @param fcfg
     * @param lines
     * @param encoding
     */
    public static void writeTextFile(File fcfg, List<String> lines, String encoding) {
        PrintWriter bw = null;
        try {
            bw = new PrintWriter(new BufferedWriter(encoding == null ? new FileWriter(fcfg) : new OutputStreamWriter(new FileOutputStream(fcfg), encoding)));
            for (String str : lines) {
                bw.println(str);
            }
            bw.flush();
        } catch (Exception e) {
            logger.error(null, e);
        } finally {
            try {
                if (bw != null)
                    bw.close();
            } catch (Exception e) {
                logger.error(null, e);
            }
        }
    }

    public static String getFilename(String filepath) {
        int p = filepath.lastIndexOf('/');
        if (p == -1)
            p = filepath.lastIndexOf('\\');
        return filepath.substring(++p);
    }

    /**
     * @param filepath file path or name
     * @param withExt  is with extension
     * @return
     */
    public static String getFilename(String filepath, boolean withExt) {
        int pExt = filepath.lastIndexOf('.');
        int pSep = filepath.lastIndexOf("/");
        if (pSep == -1)
            pSep = filepath.lastIndexOf('\\');

        if (withExt) {
            return filepath.substring(pSep + 1);
        } else {
            if (pExt > pSep) {
                return filepath.substring(pSep + 1, pExt);
            } else {
                return filepath.substring(pSep + 1);
            }
        }
    }

    /**
     * is filename has extension (ignorecase)
     *
     * @param filename
     * @param ext
     * @return
     */
    public static boolean hasExt(String filename, String ext) {
        return filename.regionMatches(true, filename.length() - ext.length(), ext, 0, ext.length());
    }
}
