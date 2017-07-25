package com.arcsoft.supervisor.cluster.action.server;

import com.arcsoft.supervisor.cluster.action.BaseRequest;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * The commander can send this request to agent to get the details, such as
 * version, license, transcoding ability.
 * 
 * @author fjli
 */
@XmlRootElement
public class GetAgentDescRequest extends BaseRequest {

}
