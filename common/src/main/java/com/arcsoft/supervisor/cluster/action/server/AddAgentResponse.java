package com.arcsoft.supervisor.cluster.action.server;

import com.arcsoft.supervisor.cluster.action.BaseResponse;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * The response message for add agent request.
 * 
 * @author fjli
 */
@XmlRootElement
public class AddAgentResponse extends BaseResponse {
    Integer gpus;

    public Integer getGpus() {
        return gpus;
    }

    public void setGpus(Integer gpus) {
        this.gpus = gpus;
    }
}
