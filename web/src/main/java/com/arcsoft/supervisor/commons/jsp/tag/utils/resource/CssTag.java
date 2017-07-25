package com.arcsoft.supervisor.commons.jsp.tag.utils.resource;

import org.apache.commons.lang3.tuple.Pair;


/**
 * A css path tag to auto generate suffix through the modified time of the file.
 *
 * @author zw.
 */
public class CssTag extends AbstractPathTagSupport {

    private static final String CSS_TAG = "<link rel=\"stylesheet\" type=\"text/css\" href=\"%s\">";

    @Override
    protected String getTagAsString(Pair<String, String> virtualAndEncodedModifiedTime) {
        return String.format(CSS_TAG, virtualAndEncodedModifiedTime.getLeft() + "?"
                + virtualAndEncodedModifiedTime.getRight());
    }

}
