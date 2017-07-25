package com.arcsoft.supervisor.model.domain.message;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Entity class of <tt>Message</tt>.
 *
 * @author jt.
 */
@Entity
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** The identify string */
    private String userName;

    private String message;

    @Column(name = "real_name")
    private String realName;
    
    private String dateTime;
    
    private Date realDateTime;
    
    private String ipAddress;

    public Message() {}

    public Message(String userName, String message) {
        this.userName = userName;
        this.message = message;
        this.dateTime = "2014.10.22 10:00:00";
        this.ipAddress = "192.168.1.1";
        this.realDateTime = new Date();
        this.realName = "unknown";
    }
    
    public Message(String userName, String realName, String message, String dateTime, String ipAddress) {
        this.userName = userName;
        this.message = message;
        this.dateTime = dateTime;
        this.ipAddress = ipAddress;
        this.realName = realName;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    try {
	    	this.realDateTime = sdf.parse(dateTime);
		} catch (Exception e) {
		    this.realDateTime = new Date();
		}
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }
    
    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
    
    public Date getRealDateTime() {
        return realDateTime;
    }

    public void setRealDateTime(Date realDateTime) {
        this.realDateTime = realDateTime;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
