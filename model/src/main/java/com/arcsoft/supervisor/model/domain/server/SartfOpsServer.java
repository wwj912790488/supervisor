package com.arcsoft.supervisor.model.domain.server;

import com.arcsoft.supervisor.commons.profile.Sartf;
import com.arcsoft.supervisor.model.domain.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

/**
 * Entity class for OpsServer which used in {@code sartf} profile.
 *
 * @author zw.
 */
@Entity
@Table(name = "ops_servers")
@Sartf
public class SartfOpsServer extends AbstractOpsServer {

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "user_ops",
            joinColumns = {@JoinColumn(name = "ops_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")})
    private User user;

    public SartfOpsServer() {
        super();
    }

    public SartfOpsServer(String ip, String port) {
        super(ip, port);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @JsonProperty("userId")
    public Integer getUserId() {
        return user != null ? user.getId() : -1;
    }
}
