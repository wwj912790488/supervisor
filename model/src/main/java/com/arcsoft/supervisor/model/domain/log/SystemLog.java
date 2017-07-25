package com.arcsoft.supervisor.model.domain.log;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Entity class of <tt>Message</tt>.
 *
 * @author jt.
 */
@Entity
@Table(name = "systemlog")
public class SystemLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** The identify string */
    private String userName;

    private Integer funcType;
    
    private String operationInfo;
    
    private String dateTime;

    private String operationResult;
    
    private Date realDateTime;

    public SystemLog() {}
    
    public SystemLog(String operationInfo) {
        this.userName = "Test";
        this.operationInfo = operationInfo;
        this.realDateTime = new Date();
        this.dateTime = "2014-10-28 18:30:00";
        this.operationResult = "Operation OK";
        this.funcType = 1;
    }
    
    public SystemLog(String dateTime, String userName, Integer funcType, String operationInfo, String operationResult) {
        this.dateTime = dateTime;
        this.userName = userName;
        this.funcType = funcType;
        this.operationInfo = operationInfo;
        this.operationResult = operationResult;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    try {
	    	this.realDateTime = sdf.parse(dateTime);
		} catch (Exception e) {
		    
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

    public String getOperationInfo() {
        return operationInfo;
    }

    public void setOperationInfo(String operationInfo) {
        this.operationInfo = operationInfo;
    }

    public Integer getFuncType() {
        return funcType;
    }

    public void setFuncType(Integer funcType) {
    	this.funcType = funcType;
    }
    
    public Date getRealDateTime() {
        return realDateTime;
    }

    public void setRealDateTime(Date realDateTime) {
        this.realDateTime = realDateTime;
    }
    
    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
    
    public String getOperationResult() {
        return operationResult;
    }

    public void setOperationResult(String operationResult) {
        this.operationResult = operationResult;
    }
}
