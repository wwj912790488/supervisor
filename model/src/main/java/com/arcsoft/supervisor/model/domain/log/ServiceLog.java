package com.arcsoft.supervisor.model.domain.log;

import javax.persistence.*;
import java.util.Date;

/**
 * Entity class of <tt>Message</tt>.
 *
 * @author jt.
 */
@Entity
@Table(name = "servicelog")
public class ServiceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The level of log.
     * <p>9 is warn 10 is error.
     */
    private byte level;

    private byte module;

    private Date time;

    private String description;

    private String ip;

    private boolean affix=false;

    public ServiceLog() {
    }

    public ServiceLog(byte level, byte module, String description, String ip) {
        this.level = level;
        this.module = module;
        this.description = description;
        this.ip = ip;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public byte getLevel() {
        return level;
    }

    public void setLevel(byte level) {
        this.level = level;
    }

    public byte getModule() {
        return module;
    }

    public void setModule(byte module) {
        this.module = module;
    }

    public Date getTime() {
        return time;
    }

    @PrePersist
    public void prePersist(){
        this.time = new Date();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public boolean isAffix() {
        return affix;
    }

    public void setAffix(boolean affix) {
        this.affix = affix;
    }
}
