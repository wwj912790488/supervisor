package com.arcsoft.supervisor.model.domain.server;

import com.arcsoft.supervisor.commons.profile.Production;
import com.arcsoft.supervisor.model.domain.graphic.WallPosition;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Entity class for OpsServer which used in {@code production} profile.
 *
 * @author zw.
 */

@Entity
@Table(name = "ops_servers")
@Production
public class OpsServer extends AbstractOpsServer {

    @OneToOne(mappedBy = "opsServer")
    @JsonIgnore
    private WallPosition wallPosition;

    public OpsServer() {
        super();
    }

    public OpsServer(String ip, String port) {
        super(ip, port);
    }

    @JsonProperty("wallPosition")
    public Integer getWallPositionId() {
        return wallPosition == null ? -1 : wallPosition.getId();
    }

    public WallPosition getWallPosition() {
        return wallPosition;
    }

    public void setWallPosition(WallPosition wallPosition) {
        this.wallPosition = wallPosition;
    }
}
