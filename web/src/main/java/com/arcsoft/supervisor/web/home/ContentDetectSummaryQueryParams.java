package com.arcsoft.supervisor.web.home;

import com.arcsoft.supervisor.service.log.impl.ContentDetectQueryParams;

import java.util.ArrayList;

/**
 * Created by tw9632 on 2015/9/24.
 */
public class ContentDetectSummaryQueryParams {
    private Integer type;
    private ContentDetectQueryParams params;
    private ArrayList<Integer> errorTypes;

    public ArrayList<Integer> getErrorTypes() {
        return errorTypes;
    }

    public void setErrorTypes(ArrayList<Integer> errorTypes) {
        this.errorTypes = errorTypes;
    }

    public ContentDetectQueryParams getParams() {
        return params;
    }

    public void setParams(ContentDetectQueryParams params) {
        this.params = params;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
