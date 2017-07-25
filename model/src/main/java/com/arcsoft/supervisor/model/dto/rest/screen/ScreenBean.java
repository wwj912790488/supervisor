package com.arcsoft.supervisor.model.dto.rest.screen;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author zw.
 */
public class ScreenBean {

    private Integer id;
    @JsonProperty("template_id")
    private Integer templateId;
    @JsonProperty("template")
    private List<ScreenSchemaBean> screenSchemaBeans;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    public List<ScreenSchemaBean> getScreenSchemaBeans() {
        return screenSchemaBeans;
    }

    public void setScreenSchemaBeans(List<ScreenSchemaBean> screenSchemaBeans) {
        this.screenSchemaBeans = screenSchemaBeans;
    }
}
