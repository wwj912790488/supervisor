package com.arcsoft.supervisor.model.domain.server;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class defines commonly fields and properties for OpsServer.
 *
 * @author zw.
 */
@MappedSuperclass
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
public abstract class AbstractOpsServer {

    @Id
    private String id;

    private String ip;

    @JsonProperty("httpServerPort")
    private String port;

    @JsonProperty("mask")
    private String netmask;

    private String gateway;

    @JsonProperty("machineMac")
    private String mac;

    private String name;

    private String resolution;

    @JsonIgnore
    @Column(name = "support_resolutions")
    private String supportResolutions;

    @Transient
    private List<String> reslist = new ArrayList<>();

    @Transient
    private Integer code;

    public AbstractOpsServer() {
    }

    public AbstractOpsServer(String ip, String port) {
        this.ip = ip;
        this.port = port;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getNetmask() {
        return netmask;
    }

    public void setNetmask(String netmask) {
        this.netmask = netmask;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getSupportResolutions() {
        return supportResolutions;
    }

    public void setSupportResolutions(String supportResolutions) {
        this.supportResolutions = supportResolutions;
    }

    public List<String> getReslist() {
        return reslist;
    }

    public void setReslist(List<String> reslist) {
        this.reslist = reslist;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}
