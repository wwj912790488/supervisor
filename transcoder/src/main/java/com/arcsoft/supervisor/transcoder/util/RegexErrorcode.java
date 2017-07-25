package com.arcsoft.supervisor.transcoder.util;

import java.util.regex.Pattern;

/**
 * @author Bing
 */
public class RegexErrorcode {
    private Pattern ptn;

    /**
     * @param code 0xfffff
     */
    public RegexErrorcode(String code) {
        ptn = Pattern.compile(code.toUpperCase());
    }

    public boolean isEqual(int errrocde) {
        String x = String.format("0X%08X", errrocde);
        return ptn.matcher(x).matches();
    }

}
