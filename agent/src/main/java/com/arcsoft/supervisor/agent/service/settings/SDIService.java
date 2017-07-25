package com.arcsoft.supervisor.agent.service.settings;

import com.arcsoft.supervisor.agent.service.agent.AgentComponentReporter;
import com.arcsoft.supervisor.cluster.action.ActionErrorCode;
import com.arcsoft.supervisor.cluster.action.ActionHandler;
import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.server.GetSDIRequest;
import com.arcsoft.supervisor.cluster.action.server.GetSDIResponse;
import com.arcsoft.supervisor.cluster.action.server.RecognizeSDIRequest;
import com.arcsoft.supervisor.cluster.action.server.RecognizeSDIResponse;
import com.arcsoft.supervisor.cluster.app.ActionException;
import com.arcsoft.supervisor.cluster.app.Request;
import com.arcsoft.supervisor.cluster.app.Response;
import com.arcsoft.supervisor.service.settings.LocalSDIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SDIService implements ActionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(SDIService.class);
	
	private AgentComponentReporter sdiReporter;
	
	private LocalSDIService localSDIService;

	@Override
	public Response execute(Request request) throws ActionException {
		if (request instanceof GetSDIRequest) {
			return getSDI();
		} else if(request instanceof RecognizeSDIRequest) {
			return recognizeSDI((RecognizeSDIRequest)request);
		}
		return null;
	}

	@Override
	public int[] getActions() {
		return new int[] {
				Actions.SDI_LIST,
				Actions.RECOGNIZE_SDI
		};
	}
	
	public AgentComponentReporter getSdiReporter() {
		return sdiReporter;
	}

	public void setSdiReporter(AgentComponentReporter sdiReporter) {
		this.sdiReporter = sdiReporter;
	}

	private GetSDIResponse getSDI() {
		reportSDI();
		GetSDIResponse res = new GetSDIResponse();
		return res;
	}
	
	public List<String> listSDI() {
		return localSDIService.list();
	}
	
	public void reportSDI() {
		if(sdiReporter != null) {
			sdiReporter.reportSDI(listSDI());
		}		
	}
	
	private RecognizeSDIResponse recognizeSDI(RecognizeSDIRequest request) {
		LOGGER.info("start recognize sdi");
		localSDIService.recognize(request.getSdiName(), request.getNumber());
		RecognizeSDIResponse res = new RecognizeSDIResponse();
		res.setErrorCode(ActionErrorCode.SUCCESS);
		return res;
	}

	public LocalSDIService getLocalSDIService() {
		return localSDIService;
	}

	public void setLocalSDIService(LocalSDIService localSDIService) {
		this.localSDIService = localSDIService;
	}

}
