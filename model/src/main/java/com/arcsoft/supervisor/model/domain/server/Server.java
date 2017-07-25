package com.arcsoft.supervisor.model.domain.server;


import com.arcsoft.supervisor.cluster.NodeType;
import com.arcsoft.supervisor.cluster.node.NodeDescription;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * This class represents the core / live agent in the cluster.
 *
 * @author fjli
 * @author zw
 */
@Entity
@Table(name = "servers")
@DynamicUpdate
public class Server {

    /**
     * Indicate there has no task running in the server.
     */
    public static final int STATE_FREE = 0;

    /**
     * Indicate there some tasks are running in the server.
     */
    public static final int STATE_BUSY = 1;

    /**
     * Indicate there are some tasks failed.
     */
    public static final int STATE_ERROR = 2;

    @Id
    @Column(name = "server_id")
    private String id;
    @Column(length = 1, nullable = false, name = "server_type")
    private Integer type;
    @Column(name = "server_name")
    private String name;
    @Column(name = "server_ip")
    private String ip;
    @Column(name = "server_port")
    private Integer port;
    @Column(name = "server_state")
    private Integer state;
    @Column(name = "server_isalive")
    private boolean alive;
    /**
     * The comma separation value of ServerFunction
     */
    private String functions;
    @Column(name = "active_functions")
    private String activeFunctions;
    private String netmask;
    private String gateway;
    private String eth;
    /**
     * Indicate the server is join to cluster or not
     */
    private Boolean joined = false;
    private String remark;

    private Integer gpus;


    /**
     * Default construct.
     */
    public Server() {

    }

    /**
     * Construct server with the specified node description.
     *
     * @param desc
     */
    public Server(NodeDescription desc) {
        this.id = desc.getId();
        this.ip = desc.getIp();
        this.port = desc.getPort();
        this.name = desc.getName();
    }

    /**
     * Return the server id.
     */
    public String getId() {
        return id;
    }

    /**
     * Set the server id.
     *
     * @param id - the server id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Return the server name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the server name.
     *
     * @param name - the server name to be set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the server ip.
     */
    public String getIp() {
        return ip;
    }

    /**
     * Set the server ip.
     *
     * @param ip - the server ip.
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * Returns the server port.
     */
    public Integer getPort() {
        return port;
    }

    /**
     * Set the server port.
     *
     * @param port - the server port
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * Return the server state.
     */
    public Integer getState() {
        return state;
    }

    /**
     * Set the server state.
     *
     * @param state - the state to be set
     */
    public void setState(Integer state) {
        this.state = state;
    }

    /**
     * Returns the server type.
     *
     * @see NodeType#TYPE_CORE
     * @see NodeType#TYPE_LIVE
     */
    public Integer getType() {
        return type;
    }

    /**
     * Set the server type.
     *
     * @param type - the server type.
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * Indicate the server is alive or not.
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * Set the server is alive or not.
     *
     * @param alive - the alive flag to bet set
     */
    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public ServerType getServerType() {
        return ServerType.getTypeEnum(this.type);
    }

    public void setTypeFromEnum(ServerType type) {
        this.setType(type.getValue());
    }

    public String getFunctions() {
        return functions;
    }

    public void setFunctions(String functions) {
        this.functions = functions;
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

    public String getEth() {
        return eth;
    }

    public void setEth(String eth) {
        this.eth = eth;
    }

    public Boolean getJoined() {
        return joined;
    }

    public void setJoined(Boolean joined) {
        this.joined = joined;
    }



    public List<ServerFunction> getFunctionEnums() {
        if (StringUtils.isBlank(this.functions)) {
            return Collections.emptyList();
        }
        String[] typeArray = this.functions.split(",");
        List<ServerFunction> functionEnums = new ArrayList<>();
        for (String type : typeArray) {
            functionEnums.add(ServerFunction.getServerFunctionWithType(type));
        }
        return functionEnums;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getActiveFunctions() {
        return activeFunctions;
    }

    public void setActiveFunctions(String activeFunctions) {
        this.activeFunctions = activeFunctions;
    }

    public List<ServerFunction> getActiveFunctionsAsEnum() {
        this.activeFunctions = StringUtils.isBlank(this.activeFunctions) ? (this.joined ? this.activeFunctions : this.functions) : this.activeFunctions;
        if (StringUtils.isBlank(this.activeFunctions)) {
            return Collections.emptyList();
        }
        return convertFromStrings(this.activeFunctions);
    }

    private List<ServerFunction> convertFromStrings(String functions) {
        String[] typeArray = functions.split(",");
        List<ServerFunction> functionEnums = new ArrayList<>();
        for (String type : typeArray) {
            functionEnums.add(ServerFunction.getServerFunctionWithType(type));
        }
        return functionEnums;
    }

    /**
     * Checks if the <code>function</code> is exists in <tt>activeFunctions</tt> or not.
     *
     * @param function the function will be checked
     * @return <code>true</code> the function is in otherwise false
     */
    public boolean isInActiveFunctions(ServerFunction function) {
        return getActiveFunctionsAsEnum().contains(function);
    }

    public Integer getGpus() {
        return gpus;
    }

    public void setGpus(Integer gpus) {
        this.gpus = gpus;
    }
}
