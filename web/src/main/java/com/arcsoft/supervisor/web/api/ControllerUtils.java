package com.arcsoft.supervisor.web.api;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by wwj on 2017/3/14.
 */
public class ControllerUtils {

    public static Map<String, Object> createSuccessMap() {
        return createModelMap(ApiErrorCode.API_SUCCESS, "success");
    }

    public static Map<String, Object> createModelMap(Integer code, String message, Object... args) {
        Map<String, Object> model = new LinkedHashMap<>();
        model.put("code", code);
        model.put("message", String.format(message, args));
        return model;
    }
}
