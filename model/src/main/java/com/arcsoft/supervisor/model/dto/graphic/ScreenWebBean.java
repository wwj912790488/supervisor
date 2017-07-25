package com.arcsoft.supervisor.model.dto.graphic;

import com.arcsoft.supervisor.model.domain.graphic.MessageStyle;
import com.arcsoft.supervisor.model.domain.graphic.Screen;
import com.arcsoft.supervisor.model.domain.server.OpsServer;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class ScreenWebBean {
    private Integer id;
    private List<ScreenSchemaWebBean> schemas = new ArrayList<>();
    @JsonProperty("active")
    private Integer active;
    private String status;
    private Integer wallPosition;
    private String opsId;
    private String opsIp;
    private String outputAddr;
    private String output;
    private MessageStyle style;
    private String message;
    private String outputAddr2;
    private String wallName;

    public ScreenWebBean() {
    }

    public ScreenWebBean(Screen screen, String taskStatus) {
        this.id = screen.getId();
        this.status = taskStatus;
        for (int i = 0; i < screen.getSchemas().size(); i++) {
            this.schemas.add(new ScreenSchemaWebBean(screen.getSchemas().get(i)));
        }
        this.wallName=screen.getWallPosition().getWallName();
        this.active = screen.getActiveSchema().getId();
        this.wallPosition = screen.getWallPosition().getId();
        OpsServer opsServer = screen.getWallPosition().getOpsServer();
        this.opsId = opsServer == null ? "" : opsServer.getId();
        this.opsIp = opsServer == null ? "" : opsServer.getIp();
        this.outputAddr = screen.getAddress() == null? "" : screen.getAddress();
        this.output = screen.getWallPosition().getOutput();
        this.style = screen.getStyle();
        this.message = screen.getMessage();
        this.outputAddr2 = "";
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<ScreenSchemaWebBean> getSchemas() {
        return schemas;
    }

    public void setSchemas(List<ScreenSchemaWebBean> schemas) {
        this.schemas = schemas;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getWallPosition() {
        return wallPosition;
    }

    public void setWallPosition(Integer wallPosition) {
        this.wallPosition = wallPosition;
    }

    public String getOpsId() {
        return opsId;
    }

    public void setOpsId(String opsId) {
        this.opsId = opsId;
    }

    public String getOpsIp() {
        return opsIp;
    }

    public void setOpsIp(String opsIp) {
        this.opsIp = opsIp;
    }
    
    public String getOutputAddr() {
        return outputAddr;
    }

    public void setOutputAddr(String outputAddr) {
        this.outputAddr = outputAddr;
    }

    public MessageStyle getStyle() {
        return style;
    }

    public void setStyle(MessageStyle style) {
        this.style = style;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setOutputAddr2(String outputAddr2){this.outputAddr2=outputAddr2;}
    public String getOutputAddr2(){return outputAddr2;}

    public String getWallName() {
        return wallName;
    }

    public void setWallName(String wallName) {
        this.wallName = wallName;
    }
}
