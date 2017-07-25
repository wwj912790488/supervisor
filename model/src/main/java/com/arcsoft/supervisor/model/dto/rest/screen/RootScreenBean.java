package com.arcsoft.supervisor.model.dto.rest.screen;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author zw.
 */
public class RootScreenBean {

    private String token;
    @JsonProperty("screen")
    private List<ScreenBean> screenBeans;

    private Integer profileId;

    public Integer getProfileId() {
        return profileId;
    }

    public void setProfileId(Integer profileId) {
        this.profileId = profileId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<ScreenBean> getScreenBeans() {
        return screenBeans;
    }

    public void setScreenBeans(List<ScreenBean> screenBeans) {
        this.screenBeans = screenBeans;
    }
}
