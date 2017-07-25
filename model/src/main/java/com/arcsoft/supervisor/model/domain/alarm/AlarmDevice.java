package com.arcsoft.supervisor.model.domain.alarm;

import com.arcsoft.supervisor.model.domain.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 * the class the <tt>alarm devices</tt>.
 *
 * @author jt.
 */
@Entity
@Table(name = "alarm_device")
public class AlarmDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /**
     * The channel id of alarm device from baidu push
     */
    private String channelId;
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    private String deviceType;

	private String tags;

	public AlarmDevice() {
	}

	public AlarmDevice(String channelid, String devtype, User user, String tags) {
		this.channelId = channelid;
		this.deviceType = devtype;
		this.user = user;
		this.tags = tags;
	}

	public AlarmDevice(String channelid, String devtype, User user) {
		this.channelId = channelid;
		this.deviceType = devtype;
		this.user = user;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
    
    public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}
}
