package com.arcsoft.supervisor.model.dto.rest.server;

import com.arcsoft.supervisor.model.domain.server.AbstractOpsServer;

public class OpsServerRecognize {
	private String id;
	private Integer clientIdentify;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getClientIdentify() {
		return clientIdentify;
	}

	public void setClientIdentify(Integer clientIdentify) {
		this.clientIdentify = clientIdentify;
	}

	public static <T extends AbstractOpsServer> OpsServerRecognize build(T server, Integer number) {
		OpsServerRecognize recognzie = new OpsServerRecognize();
		recognzie.setId(server.getId());
		recognzie.setClientIdentify(number);
		return recognzie;
	}
}
