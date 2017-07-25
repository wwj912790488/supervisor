package com.arcsoft.supervisor.transcoder.util.errorcode;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ErrorCodeServiceImpl implements ErrorCodeService {
    private Map<String, String> codeMap;

    public Map<String, String> getCodeMap() {
        return codeMap;
    }

    public void setCodeMap(Map<String, String> codeMap) {
        this.codeMap = codeMap;
    }

    @Override
    public String transformErrorCode(Integer transcoderCode) {
        String outCode = null;
        String codeString = String.format("0x%08X", transcoderCode);

        for (Map.Entry<String, String> entry : codeMap.entrySet()) {
            Pattern p = Pattern.compile(entry.getKey(), Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(codeString);
            if (m.matches()) {
                outCode = entry.getValue();
                break;
            }
        }

        return outCode;
    }

}
