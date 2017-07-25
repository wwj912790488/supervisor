package com.arcsoft.supervisor.model.domain.user;

import com.arcsoft.supervisor.commons.profile.Production;

import com.arcsoft.supervisor.model.domain.alarm.AlarmConfig;
import com.arcsoft.supervisor.model.domain.alarm.AlarmDevice;
import com.arcsoft.supervisor.model.domain.server.SartfOpsServer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;

/**
 * Entity class of User which used in {@code production} profile.
 *
 * @author zw.
 */
@Entity
@Table(name = "user")
@DynamicUpdate
@Production
public class User extends AbstractUser{

    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Transient
    private String token;
    
    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user", fetch = FetchType.LAZY)
    private AlarmConfig alarmConfig;


	public User() {
        super();
    }

    public User(String userName, String password) {
        super(userName, password);
    }

    public User(String userName, String password, Integer role) {
        super(userName, password, role);
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

    public AlarmConfig getAlarmConfig() {
        return alarmConfig;
    }

    public void setAlarmConfig(AlarmConfig alarmConfig) {
        this.alarmConfig = alarmConfig;
    }

}
