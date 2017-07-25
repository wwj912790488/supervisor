package com.arcsoft.supervisor.commons.jsp.tag.utils.resource;

import org.apache.commons.lang3.tuple.Pair;

/**
 * A javascript path tag to auto generate suffix through the modified time of the file.
 *
 * @author zw.
 */
public class JavaScriptTag extends AbstractPathTagSupport {

    private static final String JAVASCRIPT_TAG = "<script type=\"text/javascript\" src=\"%s\"></script>";

    @Override
    protected String getTagAsString(Pair<String, String> virtualAndEncodedModifiedTime) {
        return String.format(JAVASCRIPT_TAG, virtualAndEncodedModifiedTime.getLeft() + "?"
                + virtualAndEncodedModifiedTime.getRight());
    }
}
