package com.arcsoft.supervisor.commons.freemarker;

import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.util.Map;

/**
 * @author zw.
 */
public interface FreemarkerService {

    /**
     * Render string from given {@code template} and {@code values}.
     *
     * @param template the path of template file
     * @param values   the values in context
     * @return rendered string
     * @throws IOException
     * @throws TemplateException
     */
    String renderFromTemplateFile(String template, Map<String, Object> values) throws IOException, TemplateException;


    Template createTemplateFromString(String uniqueName, String templateString) throws IOException;

}
