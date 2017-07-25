package com.arcsoft.supervisor.commons.freemarker;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.text.StrBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

/**
 * @author zw.
 */
public class SpringFreemarkerServiceImpl implements FreemarkerService {

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Override
    public String renderFromTemplateFile(String template, Map<String, Object> values) throws IOException, TemplateException {
        Template tpl = freeMarkerConfigurer.getConfiguration().getTemplate(template);
        StringWriter writer = new StringWriter();
        tpl.process(values, writer);
        return writer.toString();
    }

    @Override
    public Template createTemplateFromString(String uniqueName, String templateString) throws IOException {
        return new Template(uniqueName, new StrBuilder(templateString).asReader(),
                freeMarkerConfigurer.getConfiguration());
    }


}
