package com.arcsoft.supervisor.cluster.action.task;

import com.arcsoft.supervisor.cluster.action.BaseResponse;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author zw.
 */
@XmlRootElement
public class GetTranscoderXmlResponse extends BaseResponse {

    private String transcoderXml;

    public String getTranscoderXml() {
        return transcoderXml;
    }

    public void setTranscoderXml(String transcoderXml) {
        this.transcoderXml = transcoderXml;
    }
}
