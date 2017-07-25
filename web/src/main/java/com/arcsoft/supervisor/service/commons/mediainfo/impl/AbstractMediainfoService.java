package com.arcsoft.supervisor.service.commons.mediainfo.impl;

import com.arcsoft.supervisor.exception.ResolveMediainfoException;
import com.arcsoft.supervisor.service.commons.mediainfo.MediainfoService;
import com.arcsoft.supervisor.utils.NamedThreadFactory;
import com.arcsoft.supervisor.utils.app.Environment;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * @author zw.
 */
public abstract class AbstractMediainfoService implements MediainfoService {


    //private static final String MEDIAINFO_PATH = "/usr/local/arcsoft/arcvideo/tmpdir/supervisor/mediainfo/";

    private static String MEDIAINFO_PATH = Environment.getProperty("commander.mediainfo.path", "/usr/local/arcvideo/supervisor/tmpdir/mediainfo/");

    private static String TRANSCODER_PATH = Environment.getProperty("commander.transcoder.path", "/usr/local/arcvideo/supervisor/transcoder-supervisor");

    private static final int MAX_TIME_OUT_SECONDS = 180;

    private final ExecutorService pool;

    /**
     * A cache object to holding the mediainfo xml file to reduce analyze time.
     */
    private static final Cache<String, String> mediainfoFileCache = CacheBuilder.newBuilder()
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .softValues().maximumSize(100)
            .removalListener(new RemovalListener<String, String>() {
                @Override
                public void onRemoval(RemovalNotification<String, String> notification) {
                    if (notification != null && !StringUtils.isEmpty(notification.getValue())) {
                        File cachedFile = new File(notification.getValue());
                        FileUtils.deleteQuietly(cachedFile);
                    }
                }
            }).build();

    public AbstractMediainfoService() {
        pool = Executors.newFixedThreadPool(100, NamedThreadFactory.create("AbstractMediainfoService"));
        //System.out.println(Environment.getProperty("commander.mediainfo.path"));
        File f = new File(MEDIAINFO_PATH);
        if (!f.exists()) {
            f.mkdirs();
        } else {
            try {
                FileUtils.cleanDirectory(f);
            } catch (IOException e) {

            }
        }
    }

    @PreDestroy
    public void destroy() {
        pool.shutdownNow();
        mediainfoFileCache.cleanUp();
    }

    /**
     * Returns the root <code>Element</code> of specific <code>path</code> represents
     * of url.
     *
     * @param path the xml file path
     * @return the root <code>Element</code>
     */
    protected final Element getRootElement(String path) {
        String xmlPath = mediainfoFileCache.getIfPresent(getEncodeKey(path));
        if (StringUtils.isBlank(xmlPath) || !new File(xmlPath).exists()) {
            xmlPath = analyze(path);
        }
        Document document = getDocument(xmlPath);
        Element root = document.getRootElement();
        Element container = document.getRootElement().element("container");
        if (root.element("programs") == null && container.getText().contains("FLV")) {
            document = compatible(xmlPath, document);
        }

        return document.getRootElement();
    }

    protected final Element getRootElementSDICount(String name) {
        String xmlPath = mediainfoFileCache.getIfPresent(getEncodeKey(name));
        if (StringUtils.isBlank(xmlPath) || !new File(xmlPath).exists()) {
            xmlPath = analyzeSDIChannel(name);
        }
        Document document = getDocument(xmlPath);
        Element root = document.getRootElement();
        Element container = document.getRootElement().element("Ports");
        if (container == null && container.getText().contains("Count")) {
            return container;
        }

        return root;
    }
    protected final Element getRootElementSDIChannelInfo(int port) {
        String xmlPath = mediainfoFileCache.getIfPresent(getEncodeKey(String.valueOf(port)));
        if (StringUtils.isBlank(xmlPath) || !new File(xmlPath).exists()) {
            xmlPath = analyzeSDIMediaInfo(port);
        }
        Document document = getDocument(xmlPath);
        Element root = document.getRootElement();
        Element container = root.element("container");
        if (container == null && container.getText().contains("video")) {
            return container;
        }

        return root;
    }


    private Document compatible(String path, Document document) {
        Element root = document.getRootElement();
        Element video = root.element("video");
        video.addAttribute("idx", "0");
        if (video.element("pid") == null)
            video.addElement("pid").setText("-1");
        if (video.element("name") == null)
            video.addElement("name").setText("Video");
        if (video.element("used") == null)
            video.addElement("used").setText("1");

        Element audio = root.element("audio");
        audio.addAttribute("idx", "0");
        if (audio.element("pid") == null)
            audio.addElement("pid").setText("-1");
        if (audio.element("name") == null)
            audio.addElement("name").setText("Audio");
        if (audio.element("used") == null)
            audio.addElement("used").setText("1");
        if (audio.element("language") == null)
            audio.addElement("language").setText("chi");

        Element contanier = root.element("container");
        root.clearContent();

        root.add(contanier);

        Element programs = root.addElement("programs");
        programs.addAttribute("count", "1");
        Element program = programs.addElement("program");
        program.addAttribute("idx", "0");
        program.addElement("name").setText("Default");
        program.addElement("used").setText("1");
        Element videos = program.addElement("videos");
        videos.addAttribute("count", "1");
        videos.add(video);

        Element audios = program.addElement("audios");
        audios.addAttribute("count", "1");
        audios.add(audio);

        try {
            OutputFormat of = OutputFormat.createPrettyPrint();
            of.setTrimText(true);
            of.setNewlines(true);
            XMLWriter writer = new XMLWriter(of);
            writer.setWriter(new OutputStreamWriter(new FileOutputStream(path), "utf-8"));
            writer.write(document);
            writer.close();

            return document;
        } catch (Exception e) {

        }

        return null;
    }

    /**
     * Returns the media info file path through the specific input source <code>path</code>.
     *
     * @param path the input source url to be analyze
     * @return the path of media info file
     */
    private String analyze(String path) {
        String mediaInfoXmlPath = MEDIAINFO_PATH + UUID.randomUUID().toString() + ".xml";
        final ProcessBuilder builder = new ProcessBuilder();
        builder.directory(new File(TRANSCODER_PATH));
        builder.command("./mediaanalyze.exe", "-i", path, "-o", mediaInfoXmlPath);
        Future<?> future = pool.submit(new Runnable() {
            @Override
            public void run() {
                Process process = null;
                try {
                    process = builder.start();
                    process.waitFor();
                } catch (InterruptedException | IOException e) {
                    if (process != null) {
                        process.destroy();
                    }
                }
            }
        });
        return submitAndWaitUntilTimeOut(path, mediaInfoXmlPath, future);
    }

    private String analyzeSDIChannel(String name) {
        String mediaInfoXmlPath = MEDIAINFO_PATH + UUID.randomUUID().toString() + ".xml";
        final ProcessBuilder builder = new ProcessBuilder();
        builder.directory(new File(TRANSCODER_PATH));
        builder.command("./mediaanalyze.exe", "-i", "PortIDsInfo", "-c", "SDI", "-o", mediaInfoXmlPath);
        Future<?> future = pool.submit(new Runnable() {
            @Override
            public void run() {
                Process process = null;
                try {
                    process = builder.start();
                    process.waitFor();
                } catch (InterruptedException | IOException e) {
                    if (process != null) {
                        process.destroy();
                    }
                }
            }
        });
        return submitAndWaitUntilTimeOut(name, mediaInfoXmlPath, future);
    }


    private String analyzeSDIMediaInfo(int port) {
        String mediaInfoXmlPath = MEDIAINFO_PATH + UUID.randomUUID().toString() + ".xml";
        final ProcessBuilder builder = new ProcessBuilder();
        builder.directory(new File(TRANSCODER_PATH));
        builder.command("./mediaanalyze.exe", "-i", String.valueOf(port), "-c", "SDI", "-o", mediaInfoXmlPath);
        Future<?> future = pool.submit(new Runnable() {
            @Override
            public void run() {
                Process process = null;
                try {
                    process = builder.start();
                    process.waitFor();
                } catch (InterruptedException | IOException e) {
                    if (process != null) {
                        process.destroy();
                    }
                }
            }
        });
        return submitAndWaitUntilTimeOut(String.valueOf(port), mediaInfoXmlPath, future);
    }

    private String submitAndWaitUntilTimeOut(String path, String mediaInfoXmlPath, Future<?> future) {
        try {
            try {
                future.get(MAX_TIME_OUT_SECONDS, TimeUnit.SECONDS);
                mediainfoFileCache.put(getEncodeKey(path), mediaInfoXmlPath);
                return mediaInfoXmlPath;
            } catch (Exception e) {
                future.cancel(true);
                throw new TimeoutException("Execute meidiaanalyze.exe timeout.");
            }
        } catch (Exception e) {
            throw new ResolveMediainfoException(e);
        }
    }

    private Document getDocument(String xmlFilePath) throws ResolveMediainfoException {
        SAXReader reader = new SAXReader();
        try {
            return reader.read(xmlFilePath);
        } catch (DocumentException e) {
            throw new ResolveMediainfoException(e);
        }
    }

    private String getEncodeKey(String path) {
        return DigestUtils.md2Hex(path);
    }
}
