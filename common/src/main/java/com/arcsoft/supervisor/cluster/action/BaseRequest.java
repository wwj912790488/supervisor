package com.arcsoft.supervisor.cluster.action;

import com.arcsoft.supervisor.cluster.app.Request;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Base request.
 * 
 * @author fjli
 */
@XmlRootElement
public abstract class BaseRequest implements Request {

	@Override
	@XmlTransient
	public int getMessageType() {
		return Actions.TYPE_REQUEST;
	}

}
