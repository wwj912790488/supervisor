package com.arcsoft.supervisor.agent.service.task;


import com.arcsoft.supervisor.agent.Application;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.text.StrBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A utility class for generate transcoder xml through template with freemarker.
 *
 * @author zw.
 */
public final class TranscoderXmlUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(TranscoderXmlUtils.class);
    private static Configuration freemarkerConfig = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);

    static {
        setTemplateLoader();
        freemarkerConfig.setTemplateUpdateDelay(0);
    }

    /**
     * Sets the template loader of <code>freemarkerConfig</code>.
     * <p>Set the work dir of <code>Application</code> class as file template loader and
     * then set <code>TranscoderXmlUtils</code> class path as class path template loader.</p>
     * <p>The template will be find in <tt>conf</tt> folder of <code>work dir</code>, if nothing find
     * then it will find in classpath template loader.
     * </p>
     */
    private static void setTemplateLoader() {
        FileTemplateLoader workDirTemplateLoader = null;
        try {
            Path path = Paths.get(new File(Application.getWorkDir()).getParent(), "conf");
            workDirTemplateLoader = new FileTemplateLoader(path.toFile());
        } catch (IOException e) {
            LOGGER.error("Failed to set template loader with work dir, it will ignore it.");
        }
        ClassTemplateLoader defaultTemplateLoader = new ClassTemplateLoader(TranscoderXmlUtils.class, "");
        List<TemplateLoader> loaders = new ArrayList<>();
        if (workDirTemplateLoader != null) {
            loaders.add(workDirTemplateLoader);
        }
        loaders.add(defaultTemplateLoader);
        MultiTemplateLoader templateLoader = new MultiTemplateLoader(
                loaders.toArray(new TemplateLoader[loaders.size()])
        );
        freemarkerConfig.setTemplateLoader(templateLoader);
    }

    public static String generateTranscoderXml(String templateName, Map<String, Object> model)
            throws IOException, TemplateException {
        freemarkerConfig.clearTemplateCache(); //TODO: just for QA test, may be remove in production
        Template template = freemarkerConfig.getTemplate(templateName);
        StringWriter writer = new StringWriter();
        template.process(model, writer);
        return writer.toString();
    }

    public static String generateTranscoderXmlFromTemplateString(String templateString, Map<String, Object> model)
            throws IOException, TemplateException {
        StrBuilder strBuilder = new StrBuilder(templateString);
        Template template = new Template("transcoder-template", strBuilder.asReader(), freemarkerConfig);
        StringWriter writer = new StringWriter();
        template.process(model, writer);
        return writer.toString();
    }

}
