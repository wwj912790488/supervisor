package com.arcsoft.supervisor.model.dto.rest.wall;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author zw.
 */
public class WallBean {

    private Integer id;
    @JsonProperty("screens")
    private List<WallPositionBean> positions;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<WallPositionBean> getPositions() {
        return positions;
    }

    public void setPositions(List<WallPositionBean> positions) {
        this.positions = positions;
    }
}
