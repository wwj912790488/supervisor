package com.arcsoft.supervisor.model.dto.rest.wall;



/**
 * @author zw.
 */
public class RootWallBean {

    private String token;
    private WallBean wall;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public WallBean getWall() {
        return wall;
    }

    public void setWall(WallBean wall) {
        this.wall = wall;
    }
}
