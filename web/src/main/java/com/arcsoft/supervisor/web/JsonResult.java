package com.arcsoft.supervisor.web;


import com.arcsoft.supervisor.exception.service.BusinessExceptionDescription;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * A convenient class to construct the json result.
 *
 * @author zw
 */
@JsonSerialize(using = JsonResultSerialize.class)
public class JsonResult implements Serializable {

    private final Map<String, Object> result;

    private static final int CODE_SUCCESS = BusinessExceptionDescription.OK.getCode();
    private static final int CODE_ERROR = BusinessExceptionDescription.ERROR.getCode();

    private int code = CODE_SUCCESS;

    private JsonResult() {
        this.result = new HashMap<>();
        putCode(code);
    }

    public JsonResult put(String key, Object value) {
        this.result.put(key, value);
        return this;
    }

    public JsonResult remove(String key) {
        this.result.remove(key);
        return this;
    }

    @JsonIgnore
    public int getCode() {
        return code;
    }

    public JsonResult setCode(int code) {
        this.code = code;
        putCode(code);
        return this;
    }

    public JsonResult success() {
        this.code = CODE_SUCCESS;
        putCode(code);
        return this;
    }

    public JsonResult error() {
        this.code = CODE_ERROR;
        putCode(code);
        return this;
    }

    @JsonProperty
    public Map<String, Object> getResult() {
        return result;
    }

    public static JsonResult fromSuccess() {
        return new JsonResult().success();
    }

    public static JsonResult fromError() {
        return new JsonResult().error();
    }

    public static JsonResult from(int code, String message){
        return new JsonResult().setCode(code).put("message", message);
    }

    public static JsonResult from(int code){
        return new JsonResult().setCode(code);
    }

    @JsonIgnore
    public boolean isSuccess() {
        return this.code == CODE_SUCCESS;
    }

    @JsonIgnore
    public boolean isError() {
        return !isSuccess();
    }

    private void putCode(int code) {
        this.result.put("code", code);
    }
}
