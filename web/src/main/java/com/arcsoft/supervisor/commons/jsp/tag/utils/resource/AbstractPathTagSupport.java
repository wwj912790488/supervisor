package com.arcsoft.supervisor.commons.jsp.tag.utils.resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A skeleton support class for path tag to minimize the effort.
 *
 * @author zw.
 */
public abstract class AbstractPathTagSupport extends SimpleTagSupport {

    private String path;

    private String separatorChars = ",";

    /**
     * A prefix of the path represents to file path.
     */
    private String pathPrefix = "/WEB-INF";

    public void setPath(String path) {
        this.path = path;
    }

    public void setSeparatorChars(String separatorChars) {
        this.separatorChars = separatorChars;
    }

    public void setPathPrefix(String pathPrefix) {
        this.pathPrefix = pathPrefix;
    }

    @Override
    public void doTag() throws JspException, IOException {
        if (StringUtils.isBlank(path)) {
            return;
        }
        if (!path.startsWith("/")){
            path = "/" + path;
        }

        path = path.replaceAll("\\r|\\n|\\s", "");

        String[] virtualPaths = StringUtils.split(path, separatorChars);
        PageContext context = (PageContext) getJspContext();

        List<Pair<String, String>> virtualAndFilePaths = new ArrayList<>();
        for (String virtualPath : virtualPaths) {
            String filePath = context.getServletContext().getRealPath(pathPrefix + virtualPath);
            if (filePath != null) {
                virtualAndFilePaths.add(Pair.of(virtualPath, filePath));
            }
        }

        if (virtualAndFilePaths.isEmpty()) {
            return;
        }

        String contextPath = context.getServletContext().getContextPath();
        StringBuilder contentBuilder = new StringBuilder();
        for (Pair<String, String> pair : virtualAndFilePaths) {
            File file = new File(pair.getRight());
            if (!file.exists()){
                continue;
            }

            String tagStr = getTagAsString(Pair.of(contextPath + pair.getLeft(), Long.toString(file.lastModified(), Character.MAX_RADIX)));
            if (tagStr != null) {
                contentBuilder.append(tagStr);
            }
        }
        if (contentBuilder.length() > 0) {
            context.getOut().write(contentBuilder.toString());
        }
    }


    /**
     * Returns the string of tag.
     *
     * @param virtualAndEncodedModifiedTime the pair of virtual path(on the left) and the encoded last modified time of
     *                                      actually file path(on the right)
     * @return the string of tag
     */
    protected abstract String getTagAsString(Pair<String, String> virtualAndEncodedModifiedTime);


}
