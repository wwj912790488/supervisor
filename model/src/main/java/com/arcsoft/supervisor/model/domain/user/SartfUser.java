package com.arcsoft.supervisor.model.domain.user;

import com.arcsoft.supervisor.commons.profile.Sartf;
import com.arcsoft.supervisor.model.domain.server.SartfOpsServer;
import com.arcsoft.supervisor.model.domain.task.UserTaskInfo;
import com.arcsoft.supervisor.model.domain.userconfig.UserConfig;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Entity class for User which used in {@code sartf} profile.
 *
 * @author zw.
 */
@Entity
@Table(name = "user")
@DynamicUpdate
@Sartf
public class SartfUser extends AbstractUser {

    @Transient
    private String token;

    @OneToOne(fetch= FetchType.LAZY)
    @JoinTable(name="user_ops",
            joinColumns={@JoinColumn(name="user_id", referencedColumnName="id")},
            inverseJoinColumns={@JoinColumn(name="ops_id", referencedColumnName="id")})
    private SartfOpsServer ops;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user", fetch = FetchType.LAZY)
    private List<UserConfig> configs = new ArrayList<>();

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_config_id")
    private UserConfig current;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user", fetch = FetchType.LAZY)
    private UserTaskInfo info;

    public SartfUser() {
        super();
    }

    public SartfUser(String userName, String password) {
        super(userName, password);
    }

    public SartfUser(String userName, String password, Integer role) {
        super(userName, password, role);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public SartfOpsServer getOps() {
        return ops;
    }

    public void setOps(SartfOpsServer ops) {
        this.ops = ops;
    }

    public List<UserConfig> getConfigs() {
        return configs;
    }

    public void setConfigs(List<UserConfig> configs) {
        this.configs = configs;
    }

    public UserConfig getCurrent() {
        return current;
    }

    public void setCurrent(UserConfig current) {
        this.current = current;
    }

    public UserTaskInfo getInfo() {
        return info;
    }

    public void setInfo(UserTaskInfo info) {
        this.info = info;
    }
}
